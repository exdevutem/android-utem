package cl.inndev.utem;

import android.support.annotation.Nullable;

import retrofit2.Call;
import retrofit2.http.*;

public interface RestClient {
    public String BASE_URL = "https://api-utem.herokuapp.com/";

    @FormUrlEncoded
    @POST("autenticacion")
    Call<Estudiante> autenticar(
            @Field("correo") String correo,
            @Field("contrasenia_pasaporte") String contraseniaPasaporte
            // @Field("contrasenia_dirdoc") String contraseniaDirdoc
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
            @Field("nacimiento") String nacimiento,
            @Field("movil") Long movil,
            @Field("fijo") Long fijo,
            @Field("sexo") Integer sexo,
            @Field("nacionalidad") Integer nacioanlidad,
            @Field("comuna") Integer comuna,
            @Field("direccion") String direccion
    );
}
