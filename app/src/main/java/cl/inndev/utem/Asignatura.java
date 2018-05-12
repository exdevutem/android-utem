package cl.inndev.utem;

public class Asignatura {
    private String nombre;
    private String codigo;
    private String estado;
    private String tipo;
    private Integer oportunidad;
    private Integer nivel;
    private Double nota;

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

}
