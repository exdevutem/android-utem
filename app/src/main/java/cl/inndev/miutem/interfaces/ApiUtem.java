package cl.inndev.miutem.interfaces;

import java.util.ArrayList;
import java.util.List;

import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Carrera;
import cl.inndev.miutem.classes.Estudiante;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiUtem {
    public String BASE_URL = "https://api-utem.herokuapp.com/";

    @FormUrlEncoded
    @POST("token")
    Call<Estudiante> autenticar(
            @Field("correo") String correo,
            @Field("contrasenia") String contrasenia
    );

    @Multipart
    @POST("pgai/perfil_foto.php")
    Call<ResponseBody> cambiarFoto(
            @Part("rut") RequestBody rut,
            @Part("t_usu") RequestBody tipo,
            @Part("foto_perfil\"; filename=\"pp.png\" ") RequestBody foto
    );

    @GET("estudiantes/{rut}")
    Call<Estudiante> getPerfil(
            @Path("rut") String rut,
            @Header("Authorization") String auth
    );

    @FormUrlEncoded
    @PUT("estudiantes/{rut}")
    Call<Estudiante> actualizarPerfil(
            @Path("rut") String rut,
            @Header("Authorization") String auth,
            @Field("correo") String correo,
            @Field("movil") Long movil,
            @Field("fijo") Long fijo,
            @Field("sexo") Integer sexo,
            @Field("comuna") Integer comuna,
            @Field("nacionalidad") Integer nacionalidad,
            @Field("direccion") String direccion
    );

    @GET("/estudiantes/{rut}/carreras")
    Call<List<Carrera>> getCarreras(
            @Path("rut") String rut,
            @Header("Authorization") String auth
    );

    @GET("/estudiantes/{rut}/carreras/{idCarrera}/malla")
    Call<List<Carrera.Nivel>> getMalla(
            @Path("rut") String rut,
            @Path("idCarrera") Integer idCarrera,
            @Header("Authorization") String auth
    );

    @GET("/estudiantes/{rut}/horarios")
    Call<Estudiante.Horario> getHorarios(
            @Path("rut") String rut,
            @Header("Authorization") String auth
    );

    @GET("/estudiantes/{rut}/asignaturas")
    Call<List<Asignatura>> getAsignaturas(
            @Path("rut") String rut,
            @Header("Authorization") String auth
    );
}
