package modelo;

public class Alumnos {

	private int id_alumno;
	private String nombre;
	private String apellidoP;
	private String apellidoM;
	private String matricula;
	private String curp;
	private String id_grupo;
	private String estatus;
	private String foto_ruta;
	private String IdBot;
	public String getIdBot() {
		return IdBot;
	}
	public void setIdBot(String idBot) {
		IdBot = idBot;
	}
	public int getId_alumno() {
		return id_alumno;
	}
	public void setId_alumno(int id_alumno) {
		this.id_alumno = id_alumno;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellidoP() {
		return apellidoP;
	}
	public void setApellidoP(String apellidoP) {
		this.apellidoP = apellidoP;
	}
	public String getApellidoM() {
		return apellidoM;
	}
	public void setApellidoM(String apellidoM) {
		this.apellidoM = apellidoM;
	}
	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public String getCurp() {
		return curp;
	}
	public void setCurp(String curp) {
		this.curp = curp;
	}
	public String getId_grupo() {
		return id_grupo;
	}
	public void setId_grupo(String id_grupo) {
		this.id_grupo = id_grupo;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public String getFoto_ruta() {
		return foto_ruta;
	}
	public void setFoto_ruta(String foto_ruta) {
		this.foto_ruta = foto_ruta;
	}

}