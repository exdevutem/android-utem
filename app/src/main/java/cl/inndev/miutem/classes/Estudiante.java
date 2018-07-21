package cl.inndev.miutem.classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.pixplicity.easyprefs.library.Prefs;

import java.lang.reflect.Type;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cl.inndev.miutem.activities.LoginActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mapache on 14-03-18.
 */

public class Estudiante {
    public class Sexo {
        @SerializedName("_id")
        Integer id;
        @SerializedName("sexo")
        String descripcion;

        public Sexo() {}

        public Sexo(Integer id) {
            this.id = id;
            switch (id) {
                case 1:
                    this.descripcion = "Masculino";
                    break;
                case 2:
                    this.descripcion = "Femenino";
                    break;
                case 0:
                    this.descripcion = "Sin específicar";
                    break;
                default:
                    this.id = null;
                    this.descripcion = null;
                    break;
            }
        }

        public Integer getId() {
            return this.id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDescripcion() {
            return this.descripcion;
        }

        public void setDescripcion(String sexoTexto) {
            this.descripcion = sexoTexto;
        }

    }

    private class Nacionalidad {
        @SerializedName("_id")
        Integer id;
        @SerializedName("nacionalidad")
        String descripcion;

        public Nacionalidad() { }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }
    }

    private class Comuna  {
        @SerializedName("_id")
        Integer id;
        @SerializedName("comuna")
        String descripcion;

        public Comuna() { }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }
    }

    static public class Horario {
        List<List<Asignatura>> datos;

        public Horario() {}

        public Horario(List<List<Asignatura>> datos) {
            this.datos = datos;
        }

        public List<List<Asignatura>> getDatos() {
            return datos;
        }

        public void setDatos(List<List<Asignatura>> datos) {
            this.datos = datos;
        }
    }

    static public class Nombre {
        private String nombres;
        private String apellidos;
        private String completo;

        public Nombre() {}

        public Nombre(String completo) {
            this.completo = completo;
        }

        public Nombre(String nombres, String apellidos) {
            this.nombres = nombres;
            this.apellidos = apellidos;
        }

        public void setNombres(String nombres) {
            this.nombres = nombres;
        }

        public String getApellidos() {
            return apellidos;
        }

        public void setApellidos(String apellidos) {
            this.apellidos = apellidos;
        }

        public String getCompleto() {
            if (completo != null && !completo.isEmpty()) {
                return completo;
            } else {
                return nombres + " " + apellidos;
            }
        }

        public void setCompleto(String completo) {
            this.completo = completo;
        }

    }

    public static class Credenciales {
        private String token;
        private String correo;
        private Long rut;

        public Credenciales(String token, String correo, Long rut) {
            this.token = token;
            this.correo = correo;
            this.rut = rut;
        }

        public String getToken() {
            if (!token.startsWith("Bearer ")) {
                token = "Bearer " + token;
            }
            return token;
        }

        public String getCorreo() {
            return correo;
        }

        public Long getRut() {
            return rut;
        }
    }

    @SerializedName("_id")
    Integer id;
    Nombre nombre;
    Long rut;
    String fotoUrl;
    String tipo;
    Sexo sexo;
    String correoUtem;
    String correoPersonal;
    Long telefonoMovil;
    Long telefonoFijo;
    Float puntajePsu;
    Comuna comuna;
    Nacionalidad nacionalidad;
    Integer anioIngreso;
    Integer ultimaMatricula;
    Integer carrerasCursadas;

    public Estudiante() { }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Nombre getNombre() {
        return nombre;
    }

    public void setNombre(String nombreCompleto) {
        this.nombre = new Nombre(nombreCompleto);
    }

    public void setNombre(Nombre nombre) {
        this.nombre = nombre;
    }

    public Long getRut() { return rut; }

    public void setRut(Long rut) { this.rut = rut; }

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

    /*
    public void setNacimiento(LocalDate nacimiento) {
        LocalDate hoy = LocalDate.now();
        LocalDate birthday = LocalDate.of(1960, Month.JANUARY, 1);

        Period p = Period.between(birthday, today);

        this.nacimiento = nacimiento;
        this.edad =
    }

    public void setEdad(String fecha) throws ParseException {
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        this.nacimiento = formateador.parse(fecha);
        Period.between(this.nacimiento, this.nacimiento).getYears();

    }
    */

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

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public void setSexo(Integer sexo) {
        this.sexo = new Sexo(sexo);
    }

    public Comuna getComuna() {
        return comuna;
    }

    public void setComuna(Comuna comuna) {
        this.comuna = comuna;
    }

    public Nacionalidad getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(Nacionalidad nacionalidad) {
        this.nacionalidad = nacionalidad;
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

    public void setUltimaMatricula(Integer ultimaMatricula) {
        this.ultimaMatricula = ultimaMatricula;
    }

    public Integer getCarrerasCursadas() {
        return carrerasCursadas;
    }

    public void setCarrerasCursadas(Integer carrerasCursadas) {
        this.carrerasCursadas = carrerasCursadas;
    }

    public String getStringAnioIngreso() {
        if (anioIngreso != null) {
            return anioIngreso.toString();
        }
        return null;
    }

    public String getStringUltimaMatricula() {
        if (ultimaMatricula != null) {
            return ultimaMatricula.toString();
        }
        return null;
    }

    public String getStringCarrerasCursadas() {
        if (carrerasCursadas != null) {
            return carrerasCursadas.toString();
        }
        return null;
    }

    public String getStringTelefonoFijo() {
        if (telefonoFijo != null) {
            return telefonoFijo.toString();
        }
        return null;
    }

    public String getStringTelefonoMovil() {
        if (telefonoMovil != null) {
            return telefonoMovil.toString();
        }
        return null;
    }

    public String getStringPuntajePsu() {
        if (puntajePsu != null) {
            return puntajePsu.toString();
        }
        return null;
    }
}
