package cl.inndev.miutem.interfaces;

import cl.inndev.miutem.classes.Estudiante;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiUtem {
    public String BASE_URL = "https://api-utem.herokuapp.com/";

    @FormUrlEncoded
    @POST("autenticacion")
    Call<Estudiante> autenticar(
            @Field("correo") String correo,
            @Field("contrasenia_pasaporte") String contraseniaPasaporte
    );

    @Multipart
    @POST("pgai/perfil_foto.php")
    Call<ResponseBody> cambiarFoto(
            @Part("rut") RequestBody rut,
            @Part("t_usu") RequestBody tipo,
            @Part("foto_perfil\"; filename=\"pp.png\" ") RequestBody foto
    );

    @GET("estudiantes/{rut}")
    Call<Estudiante> obtenerPerfil(
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
}
