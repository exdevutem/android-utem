package cl.inndev.miutem.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Docente {
    @SerializedName("_id")
    @Expose
    private Integer id;
    @SerializedName("nombre")
    @Expose
    private Estudiante.Nombre nombre;
    @SerializedName("rut")
    @Expose
    private Long rut;
    @SerializedName("correo")
    @Expose
    private String correo;

    public Docente(String nombre) {
        this.nombre = new Estudiante.Nombre(nombre);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Estudiante.Nombre getNombre() {
        return nombre;
    }

    public void setNombre(Estudiante.Nombre nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Long getRut() {
        return rut;
    }

    public void setRut(Long rut) {
        this.rut = rut;
    }

}
