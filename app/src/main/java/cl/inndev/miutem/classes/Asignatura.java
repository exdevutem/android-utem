package cl.inndev.miutem.classes;

import com.google.gson.annotations.SerializedName;

public class Asignatura {
    public class Seccion  {
        @SerializedName("_id")
        Integer id;
        Integer numero;
        Integer alumnoSeccionId;

        public Seccion(Integer numero) {
            this.numero = numero;
        }

        public Seccion(Integer id, Integer numero) {
            this.id = id;
            this.numero = numero;
        }

        public Integer getId() {
            return this.id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Integer getNumero() {
            return this.numero;
        }

        public void setNumero(int numero) {
            this.numero = numero;
        }

        public Integer getAlumnoSeccionId() {
            return this.alumnoSeccionId;
        }

        public void setAlumnoSeccionId(int alumnoSeccionId) {
            this.alumnoSeccionId = alumnoSeccionId;
        }
    }

    @SerializedName("_id")
    private Integer id;
    private String nombre;
    private String codigo;
    private String estado;
    private String tipo;
    private Integer oportunidades;
    private Integer nivel;
    private Double nota;
    private String sala;
    private Seccion seccion;
    private Docente docente;

    public Asignatura() {}

    public Asignatura(String nombre, String tipo, String sala) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.sala = sala;
    }

    public Asignatura(String nombre, String tipo, String nombreDocente, Integer seccionNumero) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.docente = new Docente(nombreDocente);
        this.seccion = new Seccion(seccionNumero);
    }

    public Asignatura(String nombre, String codigo, String estado, String tipo, Integer oportunidades, Double nota) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.estado = estado;
        this.tipo = tipo;
        this.oportunidades = oportunidades;
        this.nota = nota;
    }

    public Asignatura(Integer id, String codigo, String nombre, String nombreDocente, String tipo, Integer seccionId, Integer seccionNumero) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.tipo = tipo;
        this.docente = new Docente(nombreDocente);
        this.seccion = new Seccion(seccionId, seccionNumero);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente profesor) {
        this.docente = profesor;
    }

    public Integer getOportunidades() {
        return oportunidades;
    }

    public void setOportunidades(Integer oportunidades) {
        this.oportunidades = oportunidades;
    }
}