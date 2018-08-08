package cl.inndev.miutem.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.anupcowkur.reservoir.Reservoir;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cl.inndev.miutem.activities.LoginActivity;
import cl.inndev.miutem.activities.MainActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mapache on 14-03-18.
 */

public class Estudiante {
    public static class Telefono {
        private int codigo;
        private Long numero;

        public Telefono(Context context, Long numeroConCodigo) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(context);
            try {
                Phonenumber.PhoneNumber numero = phoneUtil.parse("+" + numeroConCodigo, "CL");
                this.numero = numero.getNationalNumber();
                this.codigo = numero.getCountryCode();
            } catch (NumberParseException e) {
                e.printStackTrace();
            }
        }

        public int getCodigo() {
            return codigo;
        }

        public Long getNumero() {
            return numero;
        }
    }

    public static class Sexo {
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
                default:
                    this.id = 0;
                    this.descripcion = "Sin específicar";
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

    public static class Nacionalidad {
        @SerializedName("_id")
        Integer id;
        @SerializedName("nacionalidad")
        String descripcion;

        public Nacionalidad() { }

        public Nacionalidad(Integer codigo) {
            this.id = codigo;
        }

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

        public Integer utemACodigoComoInt(Integer codigoUtem) {
            Integer codigoInternacional;
            switch (codigoUtem) {
                case 1:
                    // Chile
                    codigoInternacional = 56;
                    break;
                case 2:
                    // Argentina
                    codigoInternacional = 54;
                    break;
                // case 3:
                case 4:
                    // España
                    codigoInternacional = 34;
                    break;
                case 5:
                    // Dinamarca
                    codigoInternacional = 45;
                    break;
                case 6:
                    // Suiza
                    codigoInternacional = 41;
                    break;
                case 7:
                    // Perú
                    codigoInternacional = 51;
                    break;
                case 8:
                    // Cuba
                    codigoInternacional = 53;
                    break;
                case 9:
                    // Bolivia
                    codigoInternacional = 591;
                    break;
                case 10:
                    // Ecuador
                    codigoInternacional = 593;
                    break;
                case 11:
                    // Italia
                    codigoInternacional = 39;
                    break;
                case 12:
                    // Australia
                    codigoInternacional = 61;
                    break;
                case 13:
                    // Brasil
                    codigoInternacional = 55;
                    break;
                case 14:
                    // Bulgaria
                    codigoInternacional = 359;
                    break;
                case 15:
                    // China
                    codigoInternacional = 86;
                    break;
                case 16:
                    // Colombia
                    codigoInternacional = 57;
                    break;
                //case 17:
                    // ¿Qué chucha es "Estervina"?
                case 18:
                    // Francia
                    codigoInternacional = 33;
                    break;
                case 19:
                    // Líbano
                    codigoInternacional = 961;
                    break;
                case 20:
                    // México
                    codigoInternacional = 52;
                    break;
                case 21:
                    // Suecia
                    codigoInternacional = 46;
                    break;
                case 22:
                    // Taiwan
                    codigoInternacional = 886;
                    break;
                case 23:
                    // Uruguay
                    codigoInternacional = 598;
                    break;
                case 24:
                    // Venezuela
                    codigoInternacional = 58;
                    break;
                case 25:
                    // Argelia
                    codigoInternacional = 213;
                    break;
                // case 26:
                case 27:
                    // Canada
                    codigoInternacional = 1;
                    break;
                case 28:
                    // Alemania
                    codigoInternacional = 49;
                    break;
                case 29:
                    // Extranjero
                    codigoInternacional = 0;
                    break;
                case 30:
                    // Estados Unidos
                    codigoInternacional = 1;
                    break;
                case 31:
                    // "Varsovia" no, Polonia
                    codigoInternacional = 48;
                    break;
                case 32:
                    // Lituania
                    codigoInternacional = 370;
                    break;
                case 33:
                    // Guatemala
                    codigoInternacional = 502;
                    break;
                case 34:
                    // Nueva Zelanda
                    codigoInternacional = 64;
                    break;
                case 35:
                    // Portugal
                    codigoInternacional = 351;
                    break;
                case 36:
                    // Haití
                    codigoInternacional = 509;
                    break;
                case 37:
                    // Senegal
                    codigoInternacional = 221;
                    break;
                default:
                    codigoInternacional = 0;
                    break;
            }
            return codigoInternacional;
        }

        public static String utemACodigo(Integer codigoUtem) {
            String codigoInternacional;
            switch (codigoUtem) {
                case 1:
                    // Chile
                    codigoInternacional = "CL";
                    break;
                case 2:
                    // Argentina
                    codigoInternacional = "AR";
                    break;
                // case 3:
                case 4:
                    // España
                    codigoInternacional = "ES";
                    break;
                case 5:
                    // Dinamarca
                    codigoInternacional = "DK";
                    break;
                case 6:
                    // Suiza
                    codigoInternacional = "CH";
                    break;
                case 7:
                    // Perú
                    codigoInternacional = "PE";
                    break;
                case 8:
                    // Cuba
                    codigoInternacional = "CU";
                    break;
                case 9:
                    // Bolivia
                    codigoInternacional = "BO";
                    break;
                case 10:
                    // Ecuador
                    codigoInternacional = "EC";
                    break;
                case 11:
                    // Italia
                    codigoInternacional = "IT";
                    break;
                case 12:
                    // Australia
                    codigoInternacional = "AU";
                    break;
                case 13:
                    // Brasil
                    codigoInternacional = "BR";
                    break;
                case 14:
                    // Bulgaria
                    codigoInternacional = "BG";
                    break;
                case 15:
                    // China
                    codigoInternacional = "CN";
                    break;
                case 16:
                    // Colombia
                    codigoInternacional = "CO";
                    break;
                //case 17:
                // ¿Qué chucha es "Estervina"?
                case 18:
                    // Francia
                    codigoInternacional = "FR";
                    break;
                case 19:
                    // Líbano
                    codigoInternacional = "LB";
                    break;
                case 20:
                    // México
                    codigoInternacional = "MX";
                    break;
                case 21:
                    // Suecia
                    codigoInternacional = "SE";
                    break;
                case 22:
                    // Taiwan
                    codigoInternacional = "TW";
                    break;
                case 23:
                    // Uruguay
                    codigoInternacional = "UY";
                    break;
                case 24:
                    // Venezuela
                    codigoInternacional = "VE";
                    break;
                case 25:
                    // Argelia
                    codigoInternacional = "DZ";
                    break;
                // case 26:
                case 27:
                    // Canada
                    codigoInternacional = "CA";
                    break;
                case 28:
                    // Alemania
                    codigoInternacional = "DE";
                    break;
                case 29:
                    // Extranjero
                    codigoInternacional = "";
                    break;
                case 30:
                    // Estados Unidos
                    codigoInternacional = "US";
                    break;
                case 31:
                    // "Varsovia" no, Polonia
                    codigoInternacional = "PL";
                    break;
                case 32:
                    // Lituania
                    codigoInternacional = "LT";
                    break;
                case 33:
                    // Guatemala
                    codigoInternacional = "GT";
                    break;
                case 34:
                    // Nueva Zelanda
                    codigoInternacional = "NZ";
                    break;
                case 35:
                    // Portugal
                    codigoInternacional = "PT";
                    break;
                case 36:
                    // Haití
                    codigoInternacional = "HT";
                    break;
                case 37:
                    // Senegal
                    codigoInternacional = "SN";
                    break;
                default:
                    codigoInternacional = "";
                    break;
            }
            return codigoInternacional;
        }

        public static Integer codigoAUtem(String codigoInternacional) {
            Integer codigoUtem;
            if (codigoInternacional.equalsIgnoreCase("CL")) {
                // Chile
                codigoUtem = 1;
            } else if (codigoInternacional.equalsIgnoreCase("AR")) {
                // Argentina
                codigoUtem = 2;
            } else if (codigoInternacional.equalsIgnoreCase("ES")) {
                // España
                codigoUtem = 4;
            } else if (codigoInternacional.equalsIgnoreCase("DK")) {
                // Dinamarca
                codigoUtem = 5;
            } else if (codigoInternacional.equalsIgnoreCase("CH")) {
                // Suiza
                codigoUtem = 6;
            } else if (codigoInternacional.equalsIgnoreCase("PE")) {
                // Perú
                codigoUtem = 7;
            } else if (codigoInternacional.equalsIgnoreCase("CU")) {
                // Cuba
                codigoUtem = 8;
            } else if (codigoInternacional.equalsIgnoreCase("BO")) {
                // Bolivia
                codigoUtem = 9;
            } else if (codigoInternacional.equalsIgnoreCase("EC")) {
                // Ecuador
                codigoUtem = 10;
            } else if (codigoInternacional.equalsIgnoreCase("IT")) {
                // Italia
                codigoUtem = 11;
            } else if (codigoInternacional.equalsIgnoreCase("AU")) {
                // Australia
                codigoUtem = 12;
            } else if (codigoInternacional.equalsIgnoreCase("BR")) {
                // Brasil
                codigoUtem = 13;
            } else if (codigoInternacional.equalsIgnoreCase("BG")) {
                // Bulgaria
                codigoUtem = 14;
            } else if (codigoInternacional.equalsIgnoreCase("CN")) {
                // China
                codigoUtem = 15;
            } else if (codigoInternacional.equalsIgnoreCase("CO")) {
                // Colombia
                codigoUtem = 16;
            } else if (codigoInternacional.equalsIgnoreCase("FR")) {
                // Francia
                codigoUtem = 18;
            } else if (codigoInternacional.equalsIgnoreCase("LB")) {
                // Líbano
                codigoUtem = 19;
            } else if (codigoInternacional.equalsIgnoreCase("MX")) {
                // México
                codigoUtem = 20;
            } else if (codigoInternacional.equalsIgnoreCase("SE")) {
                // Suecia
                codigoUtem = 21;
            } else if (codigoInternacional.equalsIgnoreCase("TW")) {
                // Taiwan
                codigoUtem = 22;
            } else if (codigoInternacional.equalsIgnoreCase("UY")) {
                // Uruguay
                codigoUtem = 23;
            } else if (codigoInternacional.equalsIgnoreCase("VE")) {
                // Venezuela
                codigoUtem = 24;
            } else if (codigoInternacional.equalsIgnoreCase("DZ")) {
                // Argelia
                codigoUtem = 25;
            } else if (codigoInternacional.equalsIgnoreCase("CA")) {
                // Canada
                codigoUtem = 27;
            } else if (codigoInternacional.equalsIgnoreCase("DE")) {
                // Alemania
                codigoUtem = 28;
            } else if (codigoInternacional.equalsIgnoreCase("US")) {
                // Estados Unidos
                codigoUtem = 30;
            } else if (codigoInternacional.equalsIgnoreCase("PO")) {
                // Polonia
                codigoUtem = 99;
                // TODO: Verificar que cambien de "Varsovia" a "Polaca"
            } else if (codigoInternacional.equalsIgnoreCase("LT")) {
                // Lituania
                codigoUtem = 32;
            } else if (codigoInternacional.equalsIgnoreCase("GT")) {
                // Guatemala
                codigoUtem = 33;
            } else if (codigoInternacional.equalsIgnoreCase("NZ")) {
                // Nueva Zelanda
                codigoUtem = 34;
            } else if (codigoInternacional.equalsIgnoreCase("PT")) {
                // Portugal
                codigoUtem = 35;
            } else if (codigoInternacional.equalsIgnoreCase("HT")) {
                // Haití
                codigoUtem = 36;
            } else if (codigoInternacional.equalsIgnoreCase("SN")) {
                // Senegal
                codigoUtem = 37;
            } else {
                codigoUtem = 99;
            }

            return codigoUtem;
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

    static public class Nombre {
        @SerializedName("nombres")
        @Expose
        private String nombres;
        @SerializedName("apellidos")
        @Expose
        private String apellidos;
        @SerializedName("completo")
        @Expose
        private String completo;

        public Nombre() {}

        public Nombre(String completo) {
            this.completo = completo;
        }

        public Nombre(String nombres, String apellidos) {
            this.nombres = nombres;
            this.apellidos = apellidos;
        }

        public String getPrimerNombre() {
            String[] nombres;
            if (this.completo != null) {
                nombres = this.completo.split(" ");
            } else {
                nombres = this.nombres.split(" ");
            }
            return nombres[0];
        }

        public String getNombre() {
            if (this.completo != null) {
                return this.completo;
            } else {
                String[] nombres = this.nombres.split(" ");
                return nombres[0] + " " + this.apellidos;
            }
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

    public class Direccion {
        private Comuna comuna;
        private String direccion;

        public Direccion(String direccion) {
            this.direccion = direccion;
        }

        public Comuna getComuna() {
            return comuna;
        }

        public void setComuna(Comuna comuna) {
            this.comuna = comuna;
        }

        public String getDireccion() {
            return direccion;
        }

        public void setDireccion(String direccion) {
            this.direccion = direccion;
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
    private Integer id;
    private Nombre nombre;
    private Long rut;
    private String fotoUrl;
    private List<String> tipos;
    private Sexo sexo;
    private String correoUtem;
    private String correoPersonal;
    private Long telefonoMovil;
    private Long telefonoFijo;
    private Float puntajePsu;
    private Comuna comuna;
    private String nacimiento;
    private Nacionalidad nacionalidad;
    private Integer anioIngreso;
    private Integer ultimaMatricula;
    private Integer carrerasCursadas;
    private Direccion direccion;

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

    public List<String> getTipos() { return tipos; }

    public void setTipos(List<String> tipos) {
        this.tipos = tipos;
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

    public String getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(String nacimiento) {
        this.nacimiento = nacimiento;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = new Direccion(direccion);
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

    public String getStringNacimiento() {
        if (this.nacimiento != null) {
            return this.nacimiento;
        }
        return null;
    }

    public String getStringNacionalidad() {
        if (this.nacionalidad != null) {
            return this.nacionalidad.getId().toString();
        }
        return null;
    }

    public String getStringSexoId() {
        if (this.sexo != null) {
            return this.sexo.getId().toString();
        }
        return null;
    }

    public String getStringSexoDescripcion() {
        if (this.sexo != null) {
            return this.sexo.getDescripcion().toString();
        }
        return null;
    }

    public String getStringDireccion() {
        if (this.direccion != null) {
            return this.direccion.getDireccion();
        }
        return null;
    }
}
