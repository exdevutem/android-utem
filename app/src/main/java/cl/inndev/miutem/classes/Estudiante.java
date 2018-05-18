package cl.inndev.miutem.classes;

import android.content.Context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by mapache on 14-03-18.
 */

public class Estudiante {
    static public class Rut {
        private Long cuerpo;
        private Character dv;

        public Rut(String rut) {
            String auxRut = limpiar(rut);
            if (auxRut.length() >= 8 && auxRut.length() <= 9) {
                this.cuerpo = Long.valueOf(auxRut.substring(0, auxRut.length() - 1)).longValue();
                this.dv = auxRut.substring(auxRut.length() - 1).charAt(0);
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

        static public String limpiar(String rut) {
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

    public class Sexo {
        String sexoTexto;
        Integer sexoCodigo;

        public Sexo() { /* void constructor */ }

        public Sexo(Integer sexoCodigo) {
            switch (sexoCodigo) {
                case 1:
                    this.sexoTexto = "Masculino";
                    this.sexoCodigo = sexoCodigo;
                    break;
                case 2:
                    this.sexoTexto = "Femenino";
                    this.sexoCodigo = sexoCodigo;
                    break;
                case 0:
                    this.sexoTexto = "Sin específicar";
                    this.sexoCodigo = sexoCodigo;
                    break;
                default:
                    this.sexoTexto = null;
                    this.sexoCodigo = null;
                    break;
            }
        }

        public String getSexoTexto() {
            return this.sexoTexto;
        }
        public void setSexoTexto(String sexoTexto) {
            this.sexoTexto = sexoTexto;
        }


        public Integer getSexoCodigo() {
            return this.sexoCodigo;
        }
        public void setSexoCodigo(Integer sexoCodigo) {
            this.sexoCodigo = sexoCodigo;
        }

    }

    private class Nacionalidad {
        String nacionalidadTexto;
        Integer nacionalidadCodigo;

        public String getNacionalidadTexto() {
            return this.nacionalidadTexto;
        }
        public void setNacionalidadTexto(String nacionalidadTexto) {
            this.nacionalidadTexto = nacionalidadTexto;
        }

        public Integer getNacionalidadCodigo() {
            return this.nacionalidadCodigo;
        }
        public void setNacionalidadCodigo(Integer nacionalidadCodigo) {
            this.nacionalidadCodigo = nacionalidadCodigo;
        }

    }

    private class Comuna  {
        String comunaTexto;
        Integer comunaCódigo;

        public String getComunaTexto() {
            return this.comunaTexto;
        }
        public void setComunaTexto(String comunaTexto) {
            this.comunaTexto = comunaTexto;
        }

        public Integer getComunaCódigo() {
            return this.comunaCódigo;
        }
        public void setComunaCódigo(Integer comunaCódigo) {
            this.comunaCódigo = comunaCódigo;
        }
    }

    private String token;
    private String nombre;
    private String rut;
    private String fotoUrl;
    private String tipo;
    private Integer edad;
    //private LocalDate nacimiento;
    private String nacimiento;
    private Sexo sexo;
    //private Nacionalidad nacionalidad;
    private String correoUtem;
    private String correoPersonal;
    private Long telefonoMovil;
    private Long telefonoFijo;
    private Float puntajePsu;
    //private Comuna comuna;
    private String direccion;
    private Integer anioIngreso;
    private Integer ultimaMatricula;
    private Integer carrerasCursadas;

    private ArrayList<Asignatura> avanceDeMalla;

    public Estudiante() {/*void constructor*/}

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

    public String getRut() { return rut; }

    public void setRut(String rut) { this.rut = rut; }

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

    public String getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(String nacimiento) {
        this.nacimiento = nacimiento;
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

    public void setUltimaMatricula(Integer ultimaMatricula) {
        this.ultimaMatricula = ultimaMatricula;
    }

    public Integer getCarrerasCursadas() {
        return carrerasCursadas;
    }

    public void setCarrerasCursadas(Integer carrerasCursadas) {
        this.carrerasCursadas = carrerasCursadas;
    }

    static public Map<String, String> getCredenciales(Context contexto) {
        Map<String, String> lista = new LinkedHashMap();
        lista.put("rut", PreferencesManager.getCredentials(contexto, "rut"));
        lista.put("token", PreferencesManager.getCredentials(contexto, "token"));
        lista.put("correo", PreferencesManager.getCredentials(contexto, "correo"));
        return lista;
    }

    static public Boolean setCredenciales(Context contexto, String token, String rut, String correo) {
        String rutLimpio = new Estudiante.Rut(rut).getCuerpo().toString();
        String tokenListo = token;

        if (!token.startsWith("Bearer ")) {
            tokenListo = "Bearer " + token;
        }

        Boolean resultado = PreferencesManager.setCredentials(contexto, "token", tokenListo);
        resultado = resultado && PreferencesManager.setCredentials(contexto, "rut", rutLimpio);
        resultado = resultado && PreferencesManager.setCredentials(contexto, "correo", correo);
        return resultado;
    }



    public void guardarDatos(Context contexto) {
        if (nombre  != null) {
            PreferencesManager.setUser(contexto, "nombre", nombre);
        }

        if (rut != null) {
            PreferencesManager.setUser(contexto, "rut", rut);
        }

        if (sexo != null) {
            PreferencesManager.setUser(contexto, "sexo", sexo.sexoCodigo);
        }

        if (correoUtem != null) {
            PreferencesManager.setUser(contexto, "correo_utem", correoUtem);
        }

        if (correoPersonal  != null) {
            PreferencesManager.setUser(contexto, "correo_personal", correoPersonal);
        }

        if (tipo  != null) {
            PreferencesManager.setUser(contexto, "tipo", tipo);
        }

        if (fotoUrl  != null) {
            PreferencesManager.setUser(contexto, "foto_url", fotoUrl);
        }

        if (direccion  != null) {
            PreferencesManager.setUser(contexto, "direccion", direccion);
        }

        if (edad != null) {
            PreferencesManager.setUser(contexto, "edad", edad);
        }

        if (nacimiento != null) {
            PreferencesManager.setUser(contexto, "fecha_nacimiento", nacimiento);
        }

        if (telefonoFijo != null) {
            PreferencesManager.setUser(contexto, "telefono_fijo", telefonoFijo);
        }

        if (telefonoMovil != null) {
            PreferencesManager.setUser(contexto, "telefono_movil", telefonoMovil);
        }

        if (puntajePsu != null) {
            PreferencesManager.setUser(contexto, "puntaje_psu", puntajePsu);
        }

        if (anioIngreso != null) {
            PreferencesManager.setUser(contexto, "anio_ingreso", anioIngreso);
        }

        if (ultimaMatricula != null) {
            PreferencesManager.setUser(contexto, "ultima_matricula", ultimaMatricula);
        }

        if (carrerasCursadas != null) {
            PreferencesManager.setUser(contexto, "carreras_cursadas", carrerasCursadas);
        }
    }

    public Estudiante convertirPreferencias(Context context) {
        Estudiante usuario = new Estudiante();
        usuario.setNombre(PreferencesManager.getStringUser(context, "nombre", null));
        usuario.setTipo(PreferencesManager.getStringUser(context, "tipo", null));
        usuario.setFotoUrl(PreferencesManager.getStringUser(context, "foto_url", null));
        usuario.setAnioIngreso(PreferencesManager.getIntUser(context, "anio_ingreso"));
        usuario.setUltimaMatricula(PreferencesManager.getIntUser(context, "ultima_matricula"));
        usuario.setCarrerasCursadas(PreferencesManager.getIntUser(context, "carreras_cursadas"));
        usuario.setRut(PreferencesManager.getStringUser(context, "rut", null));
        usuario.setDireccion(PreferencesManager.getStringUser(context, "direccion", null));
        usuario.setCorreoUtem(PreferencesManager.getStringUser(context, "correo_utem", null));
        usuario.setCorreoPersonal(PreferencesManager.getStringUser(context, "correo_personal", null));
        usuario.setEdad(PreferencesManager.getIntUser(context, "edad"));
        usuario.setNacimiento(PreferencesManager.getStringUser(context, "fecha_nacimiento", null));
        //usuario.setComuna(PreferencesManager.getStringUser(context, "rut", null));
        usuario.setTelefonoFijo(PreferencesManager.getLongUser(context, "telefono_fijo", new Long(0)));
        usuario.setTelefonoMovil(PreferencesManager.getLongUser(context, "telefono_movil", new Long(0)));
        if (PreferencesManager.getFloatUser(context, "puntaje_psu") != new Float(0)) {
            usuario.setPuntajePsu(PreferencesManager.getFloatUser(context, "puntaje_psu"));
        }
        if (PreferencesManager.getIntUser(context, "sexo") != null) {
            usuario.setSexo(PreferencesManager.getIntUser(context, "sexo"));
        }
        return usuario;
    }
}
