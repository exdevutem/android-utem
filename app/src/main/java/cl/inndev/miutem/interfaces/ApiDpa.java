package cl.inndev.miutem.interfaces;

import java.util.ArrayList;

import cl.inndev.miutem.models.Chile;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiDpa {

    public String BASE_URL = "https://apis.modernizacion.cl/";

    @GET("dpa/regiones/{region}")
    Call<Chile.Region> getRegiones(
            @Path("region") String region
    );

    @GET("dpa/provincias/{provincia}")
    Call<Chile.Provincia> getProvincias(
            @Path("provincia") String provincia
    );

    @GET("dpa/comunas/{comuna}")
    Call<ArrayList<Chile.Comuna>> getComunas(
            @Path("comuna") String comuna
    );

    @GET("dpa/regiones/{region}/comunas/{comuna}")
    Call<Chile.Comuna> getComunasPorRegion(
            @Path("region") String region,
            @Path("comuna") String comuna
    );

    @GET("dpa/provincias/{provincia}/comunas/{comuna}")
    Call<Chile.Comuna> getComunasPorProvincia(
            @Path("provincia") String provincia,
            @Path("comuna") String comuna
    );

    @GET("dpa/regiones/{region}/provincias/{provincia}/comunas/{comuna}")
    Call<Chile.Comuna> getComunasEspecificas(
            @Path("region") String region,
            @Path("provincia") String provincia,
            @Path("comuna") String comuna
    );


}
