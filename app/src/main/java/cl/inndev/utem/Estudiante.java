package cl.inndev.utem;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mapache on 14-03-18.
 */

public class Estudiante {
    private String token;
    private String nombre;
    private String rut;
    private String fotoUrl;
    private String tipo;
    private Integer edad;
    private String sexo;
    private String nacionalidad;
    private String correoUtem;
    private String correoPersonal;
    private Long telefonoMovil;
    private Long telefonoFijo;
    private Float puntajePsu;
    private String comuna;
    private String direccion;
    private Integer anioIngreso;
    private Integer ultimaMatricula;
    private Integer carrerasCursadas;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getCorreoUtem() {
        return correoUtem;
    }

    public void setCorreoUtem(String correoUtem) {
        this.correoUtem = correoUtem;
    }

    public String getCorreoPersonal() {
        return correoPersonal;
    }

    public void setCorreoPersonal(String correoPersonal) {
        this.correoPersonal = correoPersonal;
    }

    public Long getTelefonoMovil() {
        return telefonoMovil;
    }

    public void setTelefonoMovil(Long telefonoMovil) {
        this.telefonoMovil = telefonoMovil;
    }

    public Float getPuntajePsu() {
        return puntajePsu;
    }

    public void setPuntajePsu(Float puntajePsu) {
        this.puntajePsu = puntajePsu;
    }

    public Long getTelefonoFijo() {
        return telefonoFijo;
    }

    public void setTelefonoFijo(Long telefonoFijo) {
        this.telefonoFijo = telefonoFijo;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getAnioIngreso() {
        return anioIngreso;
    }

    public void setAnioIngreso(Integer anioIngreso) {
        this.anioIngreso = anioIngreso;
    }

    public Integer getUltimaMatricula() {
        return ultimaMatricula;
    }

    public Estudiante() {/*void constructor*/}

    public void setUltimaMatricula(Integer ultimaMatricula) {
        this.ultimaMatricula = ultimaMatricula;
    }

    public Integer getCarrerasCursadas() {
        return carrerasCursadas;
    }

    public void setCarrerasCursadas(Integer carrerasCursadas) {
        this.carrerasCursadas = carrerasCursadas;
    }

    public void guardarDatos(Context contexto) {
        SharedPreferences.Editor editor = contexto.getSharedPreferences("usuario", MODE_PRIVATE).edit();

        if (nombre  != null) {
            editor.putString("nombre", nombre);
        }

        if (rut  != null) {
            editor.putString("rut", rut);
        }

        if (correoUtem  != null) {
            editor.putString("correo-utem", correoUtem);
        }

        if (correoPersonal  != null) {
            editor.putString("correo-personal", correoPersonal);
        }

        if (tipo  != null) {
            editor.putString("tipo", tipo);
        }

        if (fotoUrl  != null) {
            editor.putString("foto-url", fotoUrl);
        }

        if (direccion  != null) {
            editor.putString("direccion", direccion);
        }

        if (edad != null) {
            editor.putInt("edad", edad);
        }

        if (telefonoFijo != null) {
            editor.putLong("telefono-fijo", telefonoFijo);
        }

        if (telefonoMovil != null) {
            editor.putLong("telefono-movil", telefonoMovil);
        }

        if (puntajePsu != null) {
            editor.putFloat("puntaje-psu", puntajePsu);
        }

        if (anioIngreso != null) {
            editor.putInt("anio-ingreso", anioIngreso);
        }

        if (ultimaMatricula != null) {
            editor.putInt("ultima-matricula", ultimaMatricula);
        }

        if (carrerasCursadas != null) {
            editor.putInt("carreras-cursadas", carrerasCursadas);
        }

        editor.apply();

    }
}
