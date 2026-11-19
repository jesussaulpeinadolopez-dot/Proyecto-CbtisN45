package modelo;

import java.sql.Date;
import java.sql.Time;

public class Reportes {

	private int idReporte;
	private int idAlumno;
	private Integer idAsistencia; // Usamos Integer por ser un campo NULLABLE
	private Date fecha;
	private Time hora;
	private String tipoReporte;
	private String estatus;
	private String observaciones;

	public Reportes() {
	}

	public Reportes(int idReporte, int idAlumno, Integer idAsistencia, Date fecha, Time hora, String tipoReporte,
			String estatus, String observaciones) {
		this.idReporte = idReporte;
		this.idAlumno = idAlumno;
		this.idAsistencia = idAsistencia;
		this.fecha = fecha;
		this.hora = hora;
		this.tipoReporte = tipoReporte;
		this.estatus = estatus;
		this.observaciones = observaciones;
	}

	// Getters y Setters
	public int getIdReporte() {
		return idReporte;
	}

	public void setIdReporte(int idReporte) {
		this.idReporte = idReporte;
	}

	public int getIdAlumno() {
		return idAlumno;
	}

	public void setIdAlumno(int idAlumno) {
		this.idAlumno = idAlumno;
	}

	public Integer getIdAsistencia() {
		return idAsistencia;
	}

	public void setIdAsistencia(Integer idAsistencia) {
		this.idAsistencia = idAsistencia;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Time getHora() {
		return hora;
	}

	public void setHora(Time hora) {
		this.hora = hora;
	}

	public String getTipoReporte() {
		return tipoReporte;
	}

	public void setTipoReporte(String tipoReporte) {
		this.tipoReporte = tipoReporte;
	}

	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
}