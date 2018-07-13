package cl.inndev.miutem.classes;

import java.util.ArrayList;
import java.util.List;

public class Carrera {
    public static class Nivel {
        private List<Asignatura> mAsignaturas;

        private String mTitulo;

        public Nivel() {
            mAsignaturas = new ArrayList<>();
            mTitulo = null;
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
        private List<Asignatura> mAsignaturas;
        private Integer mId;

        public List<Asignatura> getAsignaturas() {
            return mAsignaturas;
        }

        public void setAsignaturas(List<Asignatura> mAsignaturas) {
            this.mAsignaturas = mAsignaturas;
        }

        public Integer getSemestreId() {
            return mId;
        }

        public void setSemestreId(Integer mId) {
            this.mId = mId;
        }
    }

    private String mCodigo;
    private String mNombre;
    private String mPlan;
    private String mEstado;
    private Integer mSemestreInicio;
    private Integer mAnioInicio;
    private Integer mAnioTermino;
    private Integer mSemestreTermino;
    private Integer mSemestreInicioId;
    private Integer mSemestreTerminoId;
    private Integer mApeId;
    private Integer mCarreraId;
    private Integer mPlanId;

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

    public Integer getApeId() {
        return mApeId;
    }

    public void setApeId(Integer mApeId) {
        this.mApeId = mApeId;
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
