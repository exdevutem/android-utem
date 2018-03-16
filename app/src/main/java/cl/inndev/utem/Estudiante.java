package cl.inndev.utem;

/**
 * Created by mapache on 14-03-18.
 */

public class Estudiante {
    String nombre;
    String rut;
    String fotoUrl;
    Integer edad;
    Integer sexo;
    String nacionalidad;
    String correoUtem;
    String correoPersonal;
    Long celular;
    Long telefono;
    String comuna;
    String direccion;
    Integer anioIngreso;
    Integer anioUltimaMatricula;
    Carrera[] carreras;

    public class Carrera {
        Integer codigo;
        String nombre;
        Integer plan;
        String estado;
        Integer anioIngreso;
        Integer semestreIngreso;
        Integer anioTermino;
        Integer semestreTermino;
    }
}
