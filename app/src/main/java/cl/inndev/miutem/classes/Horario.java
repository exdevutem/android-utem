package cl.inndev.miutem.classes;

import java.util.List;

public class Horario {
    private Carrera mCarrera;
    private List<Asignatura> mAsignaturas;
    private List<List<Asignatura>> mDatos;

    public Horario() { }

    public Horario(List<List<Asignatura>> horario) {
        this.mDatos = horario;
    }

    public Horario(List<Asignatura> asignaturas, List<List<Asignatura>> horario) {
        this.mAsignaturas = asignaturas;
        this.mDatos = horario;
    }

    public Horario(Carrera carrera, List<Asignatura> asignaturas, List<List<Asignatura>> horario) {
        this.mCarrera = carrera;
        this.mAsignaturas = asignaturas;
        this.mDatos = horario;
    }

    public List<List<Asignatura>> getDatos() {
        return mDatos;
    }

    public void setDatos(List<List<Asignatura>> datos) {
        this.mDatos = datos;
    }
}
