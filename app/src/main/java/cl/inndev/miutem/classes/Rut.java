package cl.inndev.miutem.classes;

public class Rut {
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
