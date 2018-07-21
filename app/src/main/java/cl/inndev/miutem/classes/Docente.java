package cl.inndev.miutem.classes;

import com.google.gson.annotations.SerializedName;

public class Docente {
    @SerializedName("_id")
    Integer id;
    Long rut;
    String nombre;

    public Docente(String nombre) {
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getRut() {
        return rut;
    }

    public void setRut(Long rut) {
        this.rut = rut;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
