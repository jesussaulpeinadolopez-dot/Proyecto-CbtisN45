package modelo;


public class Usuarios {
private static String username;
private String password;
private String rol;

public String getRol() {
	return rol;
}
public void setRol(String rol) {
	this.rol = rol;
}
public static String getUsername() {
	return username;
}
public void setUsername(String username) {
	Usuarios.username = username;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
}
