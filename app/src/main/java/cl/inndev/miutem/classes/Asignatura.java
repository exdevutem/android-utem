package cl.inndev.miutem.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Asignatura {
    public class Bloque {

        @SerializedName("numero")
        @Expose
        private Integer numero;
        @SerializedName("horaInicio")
        @Expose
        private String horaInicio;
        @SerializedName("horaTermino")
        @Expose
        private String horaTermino;

        public Integer getNumero() {
            return numero;
        }

        public void setNumero(Integer numero) {
            this.numero = numero;
        }

        public String getHoraInicio() {
            return horaInicio;
        }

        public void setHoraInicio(String horaInicio) {
            this.horaInicio = horaInicio;
        }

        public String getHoraTermino() {
            return horaTermino;
        }

        public void setHoraTermino(String horaTermino) {
            this.horaTermino = horaTermino;
        }

    }

    public class Periodo {

        @SerializedName("numero")
        @Expose
        private Integer numero;
        @SerializedName("bloque")
        @Expose
        private Bloque bloque;

        public Integer getNumero() {
            return numero;
        }

        public void setNumero(Integer numero) {
            this.numero = numero;
        }

        public Bloque getBloque() {
            return bloque;
        }

        public void setBloque(Bloque bloque) {
            this.bloque = bloque;
        }

    }

    public class Registro {
        @SerializedName("numero")
        @Expose
        private Integer numero;
        @SerializedName("fecha")
        @Expose
        private String fecha;
        @SerializedName("periodo")
        @Expose
        private Periodo periodo;
        @SerializedName("observaciones")
        @Expose
        private List<String> observaciones;
        @SerializedName("registrado")
        @Expose
        private Boolean registrado;
        @SerializedName("suspendido")
        @Expose
        private Boolean suspendido;
        @SerializedName("asistencia")
        @Expose
        private Boolean asistencia;

        public Integer getNumero() {
            return numero;
        }

        public void setNumero(Integer numero) {
            this.numero = numero;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public Boolean getRegistrado() {
            return registrado;
        }

        public void setRegistrado(Boolean registrado) {
            this.registrado = registrado;
        }

        public Boolean getSuspendido() {
            return suspendido;
        }

        public void setSuspendido(Boolean suspendido) {
            this.suspendido = suspendido;
        }

        public Boolean getAsistencia() {
            return asistencia;
        }

        public void setAsistencia(Boolean asistencia) {
            this.asistencia = asistencia;
        }

        public Periodo getPeriodo() {
            return periodo;
        }

        public void setPeriodo(Periodo periodo) {
            this.periodo = periodo;
        }

        public List<String> getObservaciones() {
            return observaciones;
        }

        public void setObservaciones(List<String> observaciones) {
            this.observaciones = observaciones;
        }

    }

    public class Nota {
        @SerializedName("tipo")
        @Expose
        private String tipo;
        @SerializedName("ponderador")
        @Expose
        private Double ponderador;
        @SerializedName("nota")
        @Expose
        private Double nota;

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public Double getPonderador() {
            return ponderador;
        }

        public void setPonderador(Double ponderador) {
            this.ponderador = ponderador;
        }

        public Double getNota() {
            return nota;
        }

        public void setNota(Double nota) {
            this.nota = nota;
        }

    }

    public static class Seccion  {
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

    public class Asistencia {

        @SerializedName("bloques")
        @Expose
        private Integer bloques;
        @SerializedName("registrados")
        @Expose
        private Integer registrados;
        @SerializedName("asistencias")
        @Expose
        private Integer asistencias;
        @SerializedName("inasistencias")
        @Expose
        private Integer inasistencias;
        @SerializedName("suspendidos")
        @Expose
        private Integer suspendidos;
        @SerializedName("bitacora")
        @Expose
        private List<Registro> bitacora = null;

        public Integer getBloques() {
            return bloques;
        }

        public void setBloques(Integer bloques) {
            this.bloques = bloques;
        }

        public Integer getRegistrados() {
            return registrados;
        }

        public void setRegistrados(Integer registrados) {
            this.registrados = registrados;
        }

        public Integer getAsistencias() {
            return asistencias;
        }

        public void setAsistencias(Integer asistencias) {
            this.asistencias = asistencias;
        }

        public Integer getInasistencias() {
            return inasistencias;
        }

        public void setInasistencias(Integer inasistencias) {
            this.inasistencias = inasistencias;
        }

        public Integer getSuspendidos() {
            return suspendidos;
        }

        public void setSuspendidos(Integer suspendidos) {
            this.suspendidos = suspendidos;
        }

        public List<Registro> getBitacora() {
            return bitacora;
        }

        public void setBitacora(List<Registro> bitacora) {
            this.bitacora = bitacora;
        }

    }

    @SerializedName("_id")
    @Expose
    private Integer id;
    @SerializedName("codigo")
    @Expose
    private String codigo;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("tipo")
    @Expose
    private String tipo;
    @SerializedName("docente")
    @Expose
    private Docente docente;
    @SerializedName("docentes")
    @Expose
    private List<Docente> docentes;
    @SerializedName("seccion")
    @Expose
    private Seccion seccion;
    @SerializedName("convalidado")
    @Expose
    private Boolean convalidado;
    @SerializedName("oportunidades")
    @Expose
    private Integer oportunidades;
    @SerializedName("estado")
    @Expose
    private String estado;
    @SerializedName("nota")
    @Expose
    private Double nota;
    @SerializedName("sala")
    @Expose
    private String sala;
    @SerializedName("nivel")
    @Expose
    private Integer nivel;
    @SerializedName("notas")
    @Expose
    private List<Nota> notas;
    @SerializedName("ponderadoresRegistrados")
    @Expose
    private Boolean ponderadoresRegistrados;
    @SerializedName("presentacion")
    @Expose
    private Double presentacion;
    @SerializedName("examenes")
    @Expose
    private List<Double> examenes;
    @SerializedName("observacion")
    @Expose
    private String observacion;

    public Asignatura() {}

    public Asignatura(Asignatura otra) {
        this.id = otra.id;
        this.codigo = otra.codigo;
        this.nombre = otra.nombre;
        this.tipo = otra.tipo;
        this.docente = otra.docente;
        this.docentes = otra.docentes;
        this.seccion = otra.seccion;
        this.convalidado = otra.convalidado;
        this.oportunidades = otra.oportunidades;
        this.estado = otra.estado;
        this.nota = otra.nota;
        this.sala = otra.sala;
        this.nivel = otra.nivel;
        this.notas = otra.notas;
        this.ponderadoresRegistrados = otra.ponderadoresRegistrados;
        this.presentacion = otra.presentacion;
        this.examenes = otra.examenes;
        this.observacion = otra.observacion;
    }

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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }

    public Boolean getConvalidado() {
        return convalidado;
    }

    public void setConvalidado(Boolean convalidado) {
        this.convalidado = convalidado;
    }

    public Integer getOportunidades() {
        return oportunidades;
    }

    public void setOportunidades(Integer oportunidades) {
        this.oportunidades = oportunidades;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public List<Nota> getNotas() {
        return notas;
    }

    public void setNotas(List<Nota> notas) {
        this.notas = notas;
    }

    public Boolean getPonderadoresRegistrados() {
        return ponderadoresRegistrados;
    }

    public void setPonderadoresRegistrados(Boolean ponderadoresRegistrados) {
        this.ponderadoresRegistrados = ponderadoresRegistrados;
    }

    public Double getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(Double presentacion) {
        this.presentacion = presentacion;
    }

    public List<Double> getExamenes() {
        return examenes;
    }

    public void setExamenes(List<Double> examenes) {
        this.examenes = examenes;
    }
    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public List<Docente> getDocentes() {
        return docentes;
    }

    public void setDocentes(List<Docente> docentes) {
        this.docentes = docentes;
    }
}