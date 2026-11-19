package controlador;

public final class SesionAplicacion {
	private static String usuario;
	private static String rol;

	private SesionAplicacion() {
	}

	static synchronized void iniciar(String nombreUsuario, String rolUsuario) {
		if (nombreUsuario == null || nombreUsuario.isBlank()
				|| !("1".equals(rolUsuario) || "2".equals(rolUsuario) || "3".equals(rolUsuario))) {
			throw new IllegalArgumentException("La sesión requiere un usuario y un rol válidos.");
		}
		usuario = nombreUsuario;
		rol = rolUsuario;
	}

	public static synchronized boolean estaAutenticada() {
		return usuario != null;
	}

	public static synchronized boolean esAdministrador() {
		return estaAutenticada() && "1".equals(rol);
	}

	public static synchronized String getUsuario() {
		return usuario;
	}

	public static synchronized void cerrar() {
		usuario = null;
		rol = null;
	}
}
