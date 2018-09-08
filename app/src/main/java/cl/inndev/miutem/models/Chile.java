package cl.inndev.miutem.models;

import java.util.ArrayList;

public class Chile {
    public class Region {
        private String codigo;
        private String nombre;

        public String getCodigo() {
            return this.codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }


        public String getNombre() {
            return this.nombre;
        }
        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }

    public class Provincia {
        private String codigo;
        private String nombre;
        private String codigo_padre;

        public String getCodigo() {
            return this.codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }


        public String getNombre() {
            return this.nombre;
        }
        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getCodigo_padre() {
            return this.codigo_padre;
        }
        public void setCodigo_padre(String codigo_padre) {
            this.codigo_padre = codigo_padre;
        }

    }

    public class Comuna {
        private String codigo;
        private String nombre;
        private String codigo_padre;

        public String getCodigo() {
            return this.codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }


        public String getNombre() {
            return this.nombre;
        }
        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getCodigo_padre() {
            return this.codigo_padre;
        }
        public void setCodigo_padre(String codigo_padre) {
            this.codigo_padre = codigo_padre;
        }

    }

    private ArrayList<Region> regiones;
    private ArrayList<Provincia> provincias;
    private ArrayList<Comuna> comunas;

    public ArrayList<Region> getRegiones() {
        return regiones;
    }

    public void setRegiones(ArrayList<Region> regiones) {
        this.regiones = regiones;
    }

    public ArrayList<Provincia> getProvincias() {
        return provincias;
    }

    public void setProvincias(ArrayList<Provincia> provincias) {
        this.provincias = provincias;
    }

    public ArrayList<Comuna> getComunas() {
        return comunas;
    }

    public void setComunas(ArrayList<Comuna> comunas) {
        this.comunas = comunas;
    }
}
