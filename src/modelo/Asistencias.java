package modelo;

import java.sql.Date;
import java.sql.Time;

public class Asistencias {

    private int idAsistencia;
    private int idAlumno;
    private Date fecha;
    private Time horaEntrada;
    private Time horaSalida;
    private String estatus;
    private String observaciones;

    public Asistencias() {
    }

    public Asistencias(int idAsistencia, int idAlumno, Date fecha, Time horaEntrada, Time horaSalida, String estatus, String observaciones) {
        this.idAsistencia = idAsistencia;
        this.idAlumno = idAlumno;
        this.fecha = fecha;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.estatus = estatus;
        this.observaciones = observaciones;
    }

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public int getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Time getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(Time horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public Time getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(Time horaSalida) {
        this.horaSalida = horaSalida;
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