package controlador;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.atomic.AtomicBoolean;

public class TelegramListener {

    private static int lastUpdateId = 0;
    private static final AtomicBoolean INICIADO = new AtomicBoolean();

    /**
     * Inicia el hilo en segundo plano que atiende las solicitudes de vinculación de los padres.
     */
    @SuppressWarnings("deprecation")
    public static void iniciarEscuchaAutoRespuesta() {
        if (!INICIADO.compareAndSet(false, true)) {
            return;
        }
        String botToken = TelegramService.obtenerTokenBot();
        if (botToken.isEmpty()) {
            INICIADO.set(false);
            System.err.println("No se inició Telegram: falta configurar TELEGRAM_BOT_TOKEN.");
            return;
        }

        new Thread(() -> {
            System.out.println("🤖 [Telegram] Servidor de escucha interactivo INICIADO.");
            while (true) {
                try {
                    String urlStr = "https://api.telegram.org/bot" + botToken
                            + "/getUpdates?offset=" + (lastUpdateId + 1) + "&timeout=5";
                    
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(6000);
                    conn.setReadTimeout(6000);

                    if (conn.getResponseCode() == 200) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();

                        String json = response.toString();

                        Pattern pattern = Pattern.compile("\"update_id\":(\\d+).*?\"chat\":\\{\"id\":(-?\\d+).*?\"text\":\"([^\"]+)\"", Pattern.DOTALL);
                        Matcher matcher = pattern.matcher(json);

                        while (matcher.find()) {
                            int updateId = Integer.parseInt(matcher.group(1));
                            String chatId = matcher.group(2);
                            String textoRecibido = matcher.group(3).trim();

                            if (updateId > lastUpdateId) {
                                lastUpdateId = updateId;
                            }

                            if (textoRecibido.equalsIgnoreCase("/start")) {
                                String msj = "CBTis N°45 - Control de Acceso\n\n"
                                        + "Por favor, escribe y envía la *matrícula* del alumno para vincular este chat.";
                                
                                TelegramService.enviarNotificacionAsync(chatId, msj);
                            } 
                            else {
                                // Coincide con Pattern MATRICULA = [0-9]{1,15} de controlAlumno
                                if (!textoRecibido.matches("^[0-9]{1,15}$")) {
                                    String msjFormato = "⚠️ Formato no válido. Por favor ingresa únicamente los números de la matrícula del alumno (sin espacios ni letras).";
                                    TelegramService.enviarNotificacionAsync(chatId, msjFormato);
                                    continue;
                                }

                                String resultado = controlAlumno.vincularTelegramPorMatricula(textoRecibido, chatId);

                                if (resultado.startsWith("EXITO:")) {
                                    String nombreAlumno = resultado.substring("EXITO:".length());
                                    String msjExito = "✅ *¡Vinculación Exitosa!*\n\n"
                                            + "👤 *Alumno:* " + nombreAlumno + "\n"
                                            + "🎓 *Matrícula:* " + textoRecibido + "\n\n"
                                            + "A partir de ahora recibirás notificaciones automáticas cada vez que entre o salga de la institución.";
                                    
                                    TelegramService.enviarNotificacionAsync(chatId, msjExito);

                                } else if (resultado.equals("NO_EXISTE")) {
                                    String msjNoExiste = "❌ No se encontró ningún alumno registrado con la matrícula *" + textoRecibido + "*.\n\n"
                                            + "Verifica los datos e inténtalo nuevamente.";
                                    
                                    TelegramService.enviarNotificacionAsync(chatId, msjNoExiste);

                                } else if (resultado.equals("YA_VINCULADO")) {
                                    String msjVinculado = "ℹ️ La matrícula *" + textoRecibido + "* ya cuenta con un chat de Telegram vinculado.\n\n"
                                            + "Si necesitas cambiar el número vinculado, solicita al personal escolar que desvincule el anterior.";
                                    
                                    TelegramService.enviarNotificacionAsync(chatId, msjVinculado);

                                } else {
                                    String msjError = "⚠️ Ocurrió un inconveniente al procesar la solicitud. Por favor intenta más tarde.";
                                    TelegramService.enviarNotificacionAsync(chatId, msjError);
                                }
                            }
                        }
                    }
                    conn.disconnect();
                    Thread.sleep(2000);
                } catch (Exception e) {
                    try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
                }
            }
        }, "HiloEscuchaTelegram").start();
    }
}