package cl.inndev.miutem.classes;

import java.util.List;

public class Asignatura {

    private String nombre;
    private String codigo;
    private String estado;
    private String tipo;
    private Integer oportunidad;
    private Integer nivel;
    private Double nota;
    private String sala;
    private Integer seccion;

    private String profesor;

    public Asignatura() {}

    public Asignatura(String nombre, String tipo, String sala) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.sala = sala;
    }

    public Asignatura(String nombre, String tipo, String profesor, Integer seccion) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.profesor = profesor;
        this.seccion = seccion;
    }

    public Asignatura(String nombre, String codigo, String estado, String tipo, Integer oportunidad, Integer nivel, Double nota) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.estado = estado;
        this.tipo = tipo;
        this.oportunidad = oportunidad;
        this.nivel = nivel;
        this.nota = nota;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setOportunidad(Integer oportunidad) {
        this.oportunidad = oportunidad;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public Integer getSeccion() {
        return seccion;
    }

    public void setSeccion(Integer seccion) {
        this.seccion = seccion;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }
}