package cl.inndev.miutem.interfaces;

import java.util.List;

import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Carrera;
import cl.inndev.miutem.classes.Estudiante;
import cl.inndev.miutem.classes.Horario;
import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiUtem {
    String BASE_URL = "https://api-utem.herokuapp.com/";

    @FormUrlEncoded
    @POST("token")
    Call<Estudiante.Credenciales> autenticar(
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
    Single<Estudiante> getPerfil(
            @Path("rut") String rut,
            @Header("Authorization") String auth
    );

    @FormUrlEncoded
    @PUT("estudiantes/{rut}")
    Call<Estudiante> actualizarPerfil(
            @Path("rut") Long rut,
            @Header("Authorization") String auth,
            @Field("correo") String correo,
            @Field("movil") Long movil,
            @Field("fijo") Long fijo,
            @Field("sexo") Integer sexo,
            @Field("comuna") Integer comuna,
            @Field("nacionalidad") Integer nacionalidad,
            @Field("direccion") String direccion,
            @Field("nacimiento") String nacimiento
    );

    @GET("/estudiantes/{rut}/carreras")
    Call<List<Carrera>> getCarreras(
            @Path("rut") Long rut,
            @Header("Authorization") String auth
    );

    @GET("/estudiantes/{rut}/carreras/{idCarrera}")
    Call<Carrera> getCarrera(
            @Path("rut") Long rut,
            @Header("Authorization") String auth,
            @Path("idCarrera") Integer idCarrera
    );

    @GET("/estudiantes/{rut}/carreras/{idCarrera}/malla")
    Call<List<Carrera.Nivel>> getMalla(
            @Path("rut") Long rut,
            @Header("Authorization") String auth,
            @Path("idCarrera") Integer idCarrera
    );

    @GET("/estudiantes/{rut}/carreras/{idCarrera}/boletin")
    Call<List<Carrera.Semestre>> getBoletin(
            @Path("rut") Long rut,
            @Header("Authorization") String auth,
            @Path("idCarrera") Integer idCarrera
    );

    @GET("/estudiantes/{rut}/horarios")
    Call<Horario> getHorarios(
            @Path("rut") Long rut,
            @Header("Authorization") String auth
    );

    @GET("/estudiantes/{rut}/asignaturas")
    Call<List<Asignatura>> getAsignaturas(
            @Path("rut") Long rut,
            @Header("Authorization") String auth
    );

    @GET("/estudiantes/{rut}/asignaturas/{seccionId}")
    Call<Asignatura> getAsignatura(
            @Path("rut") Long rut,
            @Header("Authorization") String auth,
            @Path("seccionId") Integer seccionId
    );

    @GET("/estudiantes/{rut}/asignaturas/{seccionId}/bitacora")
    Call<Asignatura.Asistencia> getBitacora(
            @Path("rut") Long rut,
            @Header("Authorization") String auth,
            @Path("seccionId") Integer seccionId
    );

    @GET("/estudiantes/{rut}/asignaturas/{seccionId}/notas")
    Call<Asignatura> getNotas(
            @Path("rut") Long rut,
            @Header("Authorization") String auth,
            @Path("seccionId") Integer seccionId
    );
}
