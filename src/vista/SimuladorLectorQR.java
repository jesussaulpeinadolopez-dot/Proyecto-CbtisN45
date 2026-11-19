package vista;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import controlador.controlAsistencia;
import modelo.Alumnos;

/**
 * Simula un lector QR externo para pruebas con los alumnos de prueba
 * (matriculas 990000000000001 en adelante, generadas por alumnos_prueba.sql).
 *
 * Modos (sin argumentos = oleadas):
 *
 *   oleadas [alumnosPorOleada=15] [intervaloSeg=3] [hilos=1] [total=400]
 *        Llega un grupo de alumnos cada N segundos y se procesa con 'hilos' lectores
 *        (1 = un solo lector en fila, como la app real). Mide cuanto espera cada alumno.
 *   directo [hilos=20] [repeticiones=1] [total=400]
 *        Carga maxima sin pausas (prueba de limite).
 *   teclado [retardoMs=300] [total=400]
 *        Teclea cada matricula + ENTER en la ventana con foco (prueba la interfaz).
 */
public class SimuladorLectorQR {

	private static final String PREFIJO = "99";

	public static void main(String[] args) throws Exception {
		String modo = args.length > 0 ? args[0] : "oleadas";

		if (modo.equals("oleadas")) {
			oleadas(generarMatriculas(arg(args, 4, 400)), arg(args, 1, 15), arg(args, 2, 3), arg(args, 3, 1));
		} else if (modo.equals("directo")) {
			directo(generarMatriculas(arg(args, 3, 400)), arg(args, 1, 20), arg(args, 2, 1));
		} else if (modo.equals("teclado")) {
			teclado(generarMatriculas(arg(args, 2, 400)), arg(args, 1, 300));
		} else {
			System.out.println("Modo desconocido: " + modo
					+ "\nUso: oleadas [porOleada] [intervaloSeg] [hilos] [total] | directo [hilos] [repeticiones] [total] | teclado [retardoMs] [total]");
		}
	}

	private static int arg(String[] args, int i, int porDefecto) {
		return args.length > i ? Integer.parseInt(args[i]) : porDefecto;
	}

	// Mismo formato que alumnos_prueba.sql: '99' + numero de 13 digitos = 15 digitos
	private static List<String> generarMatriculas(int total) {
		List<String> lista = new ArrayList<>();
		for (int i = 1; i <= total; i++) {
			lista.add(PREFIJO + String.format("%013d", i));
		}
		System.out.println("Matriculas generadas: " + lista.size() + " (" + lista.get(0) + " ... "
				+ lista.get(lista.size() - 1) + ")");
		return lista;
	}

	// Acumula resultados de las llamadas a controlAsistencia
	private static class Resultados {
		final Map<String, AtomicInteger> resumen = new ConcurrentHashMap<>();
		final Map<String, String> ejemplos = new ConcurrentHashMap<>();
		final ConcurrentLinkedQueue<Long> servicio = new ConcurrentLinkedQueue<>(); // lo que tarda el registro
		final ConcurrentLinkedQueue<Long> espera = new ConcurrentLinkedQueue<>(); // desde que llega hasta que termina
		final AtomicInteger alertas = new AtomicInteger();

		void registrar(String matricula, long llegadaNs) {
			long t0 = System.nanoTime();
			String clave;
			try {
				Alumnos a = new Alumnos();
				a.setMatricula(matricula);
				String resp = controlAsistencia.registrarAsistenciaPorMatricula(a);
				clave = resp == null ? "NULL" : resp.split(":")[0];
				if (clave.length() > 40) {
					clave = clave.substring(0, 40);
				}
				ejemplos.putIfAbsent(clave, String.valueOf(resp));
			} catch (Throwable e) {
				clave = "EXCEPCION " + e.getClass().getSimpleName();
				ejemplos.putIfAbsent(clave, e.toString());
			}
			long fin = System.nanoTime();
			long ms = (fin - t0) / 1_000_000;
			servicio.add(ms);
			espera.add((fin - llegadaNs) / 1_000_000);
			resumen.computeIfAbsent(clave, k -> new AtomicInteger()).incrementAndGet();
			// avisos en vivo para pruebas largas (no hace falta esperar al final)
			if (!clave.startsWith("ENTRADA") && !clave.startsWith("SALIDA") && alertas.incrementAndGet() <= 10) {
				System.out.println("  ! " + matricula + " -> " + ejemplos.get(clave));
			}
			if (ms > 1000) {
				System.out.println("  ! lento: " + matricula + " tardo " + ms + " ms");
			}
		}

		void imprimir(boolean conEspera) {
			System.out.println("Respuestas:");
			for (Map.Entry<String, AtomicInteger> e : new TreeMap<>(resumen).entrySet()) {
				System.out.println("  " + e.getKey() + ": " + e.getValue());
				System.out.println("      ejemplo completo -> " + ejemplos.get(e.getKey()));
			}
			linea("Tiempo de registro (ms)        ", servicio);
			if (conEspera) {
				linea("Espera total del alumno (ms)   ", espera);
			}
		}

		private void linea(String titulo, ConcurrentLinkedQueue<Long> datos) {
			List<Long> l = new ArrayList<>(datos);
			if (l.isEmpty()) {
				return;
			}
			Collections.sort(l);
			System.out.printf("%s-> min %d | p50 %d | p95 %d | max %d%n", titulo, l.get(0), pct(l, 0.50),
					pct(l, 0.95), l.get(l.size() - 1));
		}

		private long pct(List<Long> ordenada, double p) {
			return ordenada.get(Math.max(0, (int) Math.ceil(p * ordenada.size()) - 1));
		}
	}

	// Modo realista: llegan 'porOleada' alumnos cada 'intervaloSeg' segundos
	private static void oleadas(List<String> matriculas, int porOleada, int intervaloSeg, int hilos) throws Exception {
		ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(hilos);
		ScheduledExecutorService reloj = Executors.newSingleThreadScheduledExecutor();
		Resultados res = new Resultados();
		AtomicInteger maxPendientes = new AtomicInteger();
		CountDownLatch terminados = new CountDownLatch(matriculas.size());
		int totalOleadas = (int) Math.ceil(matriculas.size() / (double) porOleada);

		System.out.printf("Oleadas de %d alumnos cada %d s (%.1f escaneos/s), %d lector(es), %d oleadas%n", porOleada,
				intervaloSeg, porOleada / (double) intervaloSeg, hilos, totalOleadas);

		long inicio = System.nanoTime();
		for (int o = 0; o < totalOleadas; o++) {
			final int num = o + 1;
			final List<String> lote = matriculas.subList(o * porOleada,
					Math.min(matriculas.size(), (o + 1) * porOleada));
			reloj.schedule(() -> {
				int pendientes = pool.getQueue().size() + pool.getActiveCount();
				maxPendientes.accumulateAndGet(pendientes, Math::max);
				System.out.printf("[%s] Oleada %d/%d (%d alumnos) - sin atender al llegar: %d%n",
						java.time.LocalTime.now().withNano(0), num, totalOleadas, lote.size(), pendientes);
				long llegada = System.nanoTime();
				for (String m : lote) {
					pool.submit(() -> {
						try {
							res.registrar(m, llegada);
						} finally {
							terminados.countDown();
						}
					});
				}
			}, (long) o * intervaloSeg, TimeUnit.SECONDS);
		}

		terminados.await();
		reloj.shutdownNow();
		pool.shutdown();
		double seg = (System.nanoTime() - inicio) / 1e9;

		System.out.println("\n=== Resultados (oleadas) ===");
		System.out.printf("%d registros en %.1f s (llegada teorica: %.1f s)%n", matriculas.size(), seg,
				(double) (totalOleadas - 1) * intervaloSeg);
		System.out.println("Maximo de escaneos sin atender al llegar una oleada: " + maxPendientes.get());
		res.imprimir(true);
		if (maxPendientes.get() > porOleada) {
			System.out.println("-> El sistema NO alcanza el ritmo: la fila de espera crece de oleada en oleada.");
		} else {
			System.out.println("-> El sistema alcanza el ritmo: cada oleada se atiende antes de que llegue la siguiente.");
		}
	}

	// Modo carga maxima: todo sin pausas y en paralelo
	private static void directo(List<String> matriculas, int hilos, int repeticiones) throws Exception {
		ExecutorService pool = Executors.newFixedThreadPool(hilos);
		Resultados res = new Resultados();

		long inicio = System.nanoTime();
		for (int r = 0; r < repeticiones; r++) {
			for (String m : matriculas) {
				pool.submit(() -> res.registrar(m, System.nanoTime()));
			}
		}
		pool.shutdown();
		pool.awaitTermination(1, TimeUnit.HOURS);
		double seg = (System.nanoTime() - inicio) / 1e9;

		System.out.println("\n=== Resultados (directo) ===");
		System.out.printf("%d registros en %.2f s (%.1f/s) con %d hilos%n", res.servicio.size(), seg,
				res.servicio.size() / seg, hilos);
		res.imprimir(false);
	}

	// Modo interfaz: pulsaciones reales de teclado hacia la ventana que tenga el foco
	private static void teclado(List<String> matriculas, int retardoMs) throws Exception {
		Robot robot = new Robot();
		robot.setAutoDelay(5); // un lector real teclea muy rapido

		System.out.println("En 6 segundos empiezo a teclear. Abre Control, activa 'Lector externo' y haz clic en el campo de matricula.");
		Thread.sleep(6000);

		long inicio = System.nanoTime();
		int n = 0;
		for (String m : matriculas) {
			for (char c : m.toCharArray()) {
				int tecla = KeyEvent.VK_0 + (c - '0');
				robot.keyPress(tecla);
				robot.keyRelease(tecla);
			}
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			System.out.println(++n + "/" + matriculas.size() + " -> " + m);
			Thread.sleep(retardoMs);
		}
		System.out.printf("Listo: %d escaneos en %.1f s.%n", n, (System.nanoTime() - inicio) / 1e9);
	}
}