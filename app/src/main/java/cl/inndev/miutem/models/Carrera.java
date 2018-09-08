package cl.inndev.miutem.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Carrera {
    public class AsignaturasMalla {

        @SerializedName("totales")
        @Expose
        private Integer totales;
        @SerializedName("aprobadas")
        @Expose
        private Integer aprobadas;
        @SerializedName("reprobadas")
        @Expose
        private Integer reprobadas;
        @SerializedName("aprobadasHastaNivel")
        @Expose
        private Integer aprobadasHastaNivel;

        public Integer getTotales() {
            return totales;
        }

        public void setTotales(Integer totales) {
            this.totales = totales;
        }

        public Integer getAprobadas() {
            return aprobadas;
        }

        public void setAprobadas(Integer aprobadas) {
            this.aprobadas = aprobadas;
        }

        public Integer getReprobadas() {
            return reprobadas;
        }

        public void setReprobadas(Integer reprobadas) {
            this.reprobadas = reprobadas;
        }

        public Integer getAprobadasHastaNivel() {
            return aprobadasHastaNivel;
        }

        public void setAprobadasHastaNivel(Integer aprobadasHastaNivel) {
            this.aprobadasHastaNivel = aprobadasHastaNivel;
        }

    }

    public static class Nivel {
        private List<Asignatura> mAsignaturas;

        private String mTitulo;

        @SerializedName("nivel")
        @Expose
        private Integer numero;

        public Nivel() {
            mAsignaturas = new ArrayList<>();
            mTitulo = null;
        }

        public Integer getNumero() {
            return numero;
        }

        public void setNumero(Integer numero) {
            this.numero = numero;
        }

        public Nivel(List<Asignatura> asignaturas, String titulo) {
            this.mAsignaturas = asignaturas;
            this.mTitulo = titulo;
        }

        public List<Asignatura> getAsignaturas() {
            return mAsignaturas;
        }

        public void setAsignaturas(List<Asignatura> asignaturas) {
            this.mAsignaturas = asignaturas;
        }

        public String getTitulo() {
            return mTitulo;
        }

        public void setTitulo(String titulo) {
            this.mTitulo = titulo;
        }
    }

    public class Semestre {
        @SerializedName("nombre")
        @Expose
        private String nombre;
        @SerializedName("asignaturasAprobadas")
        @Expose
        private Integer asignaturasAprobadas;
        @SerializedName("asignaturasReprobadas")
        @Expose
        private Integer asignaturasReprobadas;
        @SerializedName("asignaturasConvalidadas")
        @Expose
        private Integer asignaturasConvalidadas;
        @SerializedName("promedioFinal")
        @Expose
        private Double promedioFinal;
        @SerializedName("asignaturas")
        @Expose
        private List<Asignatura> asignaturas = null;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public Integer getAsignaturasAprobadas() {
            return asignaturasAprobadas;
        }

        public void setAsignaturasAprobadas(Integer asignaturasAprobadas) {
            this.asignaturasAprobadas = asignaturasAprobadas;
        }

        public Integer getAsignaturasReprobadas() {
            return asignaturasReprobadas;
        }

        public void setAsignaturasReprobadas(Integer asignaturasReprobadas) {
            this.asignaturasReprobadas = asignaturasReprobadas;
        }

        public Integer getAsignaturasConvalidadas() {
            return asignaturasConvalidadas;
        }

        public void setAsignaturasConvalidadas(Integer asignaturasConvalidadas) {
            this.asignaturasConvalidadas = asignaturasConvalidadas;
        }

        public Double getPromedioFinal() {
            return promedioFinal;
        }

        public void setPromedioFinal(Double promedioFinal) {
            this.promedioFinal = promedioFinal;
        }

        public List<Asignatura> getAsignaturas() {
            return asignaturas;
        }

        public void setAsignaturas(List<Asignatura> asignaturas) {
            this.asignaturas = asignaturas;
        }
    }

    private Integer mId;
    private String mCodigo;
    private String mNombre;
    private Integer mCarreraId;
    private String mPlan;
    private Integer mPlanId;
    private String mEstado;
    private Integer mAnioInicio;
    private Integer mSemestreInicio;
    private Integer mSemestreInicioId;
    private Integer mAnioTermino;
    private Integer mSemestreTermino;
    private Integer mSemestreTerminoId;
    @SerializedName("asignaturas")
    @Expose
    private AsignaturasMalla asignaturas;
    @SerializedName("nivelActual")
    @Expose
    private Integer nivelActual;
    @SerializedName("malla")
    @Expose
    private List<Nivel> malla = null;

    public AsignaturasMalla getAsignaturas() {
        return asignaturas;
    }

    public void setAsignaturas(AsignaturasMalla asignaturas) {
        this.asignaturas = asignaturas;
    }

    public Integer getNivelActual() {
        return nivelActual;
    }

    public void setNivelActual(Integer nivelActual) {
        this.nivelActual = nivelActual;
    }

    public List<Nivel> getMalla() {
        return malla;
    }

    public void setMalla(List<Nivel> malla) {
        this.malla = malla;
    }

    public Carrera() { }

    public String getCodigo() {
        return mCodigo;
    }

    public void setCodigo(String mCodigo) {
        this.mCodigo = mCodigo;
    }

    public String getNombre() {
        return mNombre;
    }

    public void setNombre(String mNombre) {
        this.mNombre = mNombre;
    }

    public String getPlan() {
        return mPlan;
    }

    public void setPlan(String mPlan) {
        this.mPlan = mPlan;
    }

    public String getEstado() {
        return mEstado;
    }

    public void setEstado(String mEstado) {
        this.mEstado = mEstado;
    }

    public Integer getSemestreInicio() {
        return mSemestreInicio;
    }

    public void setSemestreInicio(Integer mSemestreInicio) {
        this.mSemestreInicio = mSemestreInicio;
    }

    public Integer getAnioInicio() {
        return mAnioInicio;
    }

    public void setAnioInicio(Integer mAnioInicio) {
        this.mAnioInicio = mAnioInicio;
    }

    public Integer getAnioTermino() {
        return mAnioTermino;
    }

    public void setAnioTermino(Integer manioTermino) {
        this.mAnioTermino = manioTermino;
    }

    public Integer getSemestreTermino() {
        return mSemestreTermino;
    }

    public void setSemestreTermino(Integer mSemestreTermino) {
        this.mSemestreTermino = mSemestreTermino;
    }

    public Integer getSemestreInicioId() {
        return mSemestreInicioId;
    }

    public void setSemestreInicioId(Integer mSemestreInicioId) {
        this.mSemestreInicioId = mSemestreInicioId;
    }

    public Integer getSemestreTerminoId() {
        return mSemestreTerminoId;
    }

    public void setSemestreTerminoId(Integer mSemestreTerminoId) {
        this.mSemestreTerminoId = mSemestreTerminoId;
    }

    public Integer getmId() {
        return mId;
    }

    public void setmId(Integer mId) {
        this.mId = mId;
    }

    public Integer getCarreraId() {
        return mCarreraId;
    }

    public void setCarreraId(Integer mCarreraId) {
        this.mCarreraId = mCarreraId;
    }

    public Integer getPlanId() {
        return mPlanId;
    }

    public void setPlanId(Integer mPlanId) {
        this.mPlanId = mPlanId;
    }
}
