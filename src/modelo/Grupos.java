package modelo;

import java.util.Objects;

public class Grupos {
    private int idGrupo;
    private String nombre;
    private String tutor;

    public Grupos() {
    }

    public Grupos(int idGrupo, String nombre, String tutor) {
        this.idGrupo = idGrupo;
        this.nombre = nombre;
        this.tutor = tutor;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTutor() {
        return tutor;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

    @Override
    public String toString() {
        return nombre != null ? nombre : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grupos grupos = (Grupos) o;
        return idGrupo == grupos.idGrupo || Objects.equals(nombre, grupos.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idGrupo, nombre);
    }
}