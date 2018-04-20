package cl.inndev.utem;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.LocalDate;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mapache on 14-03-18.
 */

public class Estudiante {
    private String token;
    private String nombre;
    private Rut rut;
    private String fotoUrl;
    private String tipo;
    private Integer edad;
    private LocalDate nacimiento;
    private String sexo;
    private Integer sexoCodigo;
    private String nacionalidad;
    private Integer nacionalidadCodigo;
    private String correoUtem;
    private String correoPersonal;
    private Long telefonoMovil;
    private Long telefonoFijo;
    private Float puntajePsu;
    private String comuna;
    private Integer comunaCodigo;
    private String direccion;
    private Integer anioIngreso;
    private Integer ultimaMatricula;
    private Integer carrerasCursadas;

    public class Rut {
        private Long cuerpo;
        private Character dv;

        public Rut(String rut) {
            String auxRut = limpiar(rut);
            if (auxRut.length() >= 8 && auxRut.length() <= 9) {
                this.cuerpo = Long.valueOf(auxRut).longValue();
                this.dv = rut.substring(rut.length() - 1).charAt(0);
                if (!this.esValido()) {
                    this.cuerpo = null;
                    this.dv = null;
                }
            } else {
                this.cuerpo = null;
                this.dv = null;
            }
        }

        public Rut(Long cuerpo) {
            Integer largo = cuerpo.toString().length();
            if (largo >= 7 && largo <= 8) {
                this.cuerpo = cuerpo;
                this.dv = calcularDv(cuerpo);
            } else {
                this.cuerpo = null;
                this.dv = null;
            }
        }

        public Rut(Long cuerpo, Character dv) {
            Integer largo = cuerpo.toString().length();
            if (largo >= 7 && largo <= 8) {
                this.cuerpo = cuerpo;
                this.dv = dv;
                if (!this.esValido()) {
                    this.cuerpo = null;
                    this.dv = null;
                }
            } else {
                this.cuerpo = null;
                this.dv = null;
            }
        }

        public String getRut(Boolean conPuntos) {
            if (conPuntos) {
                return String.format("%.d", this.cuerpo) + "-" + dv;
            }
            return cuerpo + "-" + dv;
        }
        public Long getCuerpo() {
            return cuerpo;
        }
        public Character getDv() { return dv; }


        public Boolean esValido() {
            if (calcularDv(this.cuerpo) == this.dv) {
                return true;
            }
            return false;
        }

        private String limpiar(String rut) {
            String auxRut = rut.trim();
            auxRut = rut.replaceAll("[.-]", "");
            auxRut.toUpperCase();
            return auxRut;
        }

        private Character calcularDv(Long rut) {
            String auxRut = rut.toString();

            Integer largo = auxRut.length();
            Integer factor = 2;
            Integer suma = 0;
            Integer resultado;

            for (Integer i = largo; i > 0; i--) {
                if (factor > 7) {
                    factor = 2;
                }
                suma += (Integer.parseInt(auxRut.substring((i-1), i))) * factor;
                factor++;
            }

            resultado = 11 - suma % 11;

            if (resultado == 10) {
                return 'K';
            } else if (resultado == 11) {
                return '0';
            } else {
                return resultado.toString().charAt(0);
            }
        }
    }

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

    public String getRut(Boolean conPuntos) {
        return rut.getRut(conPuntos);
    }

    public void setRut(String rut) {
        this.rut = new Rut(rut);
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

    public void setEdad(Integer edad) { this.edad = edad; }

    public void setNacimiento(LocalDate nacimiento) {
        LocalDate hoy = LocalDate.now();
        LocalDate birthday = LocalDate.of(1960, Month.JANUARY, 1);

        Period p = Period.between(birthday, today);

        this.nacimiento = nacimiento;
        this.edad =
    }

    /*
    public void setEdad(String fecha) throws ParseException {
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        this.nacimiento = formateador.parse(fecha);
        Period.between(this.nacimiento, this.nacimiento).getYears();

    }
    */

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        switch (sexo) {
            case "Masculino":
                this.sexo = sexo;
                this.sexoCodigo = 1;
                break;
            case "Femenino":
                this.sexo = sexo;
                this.sexoCodigo = 2;
                break;
            default:
                this.sexo = "Sin Información";
                this.sexoCodigo = 0;
                break;
        }
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
            editor.putString("rut", rut.getRut(true));
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
