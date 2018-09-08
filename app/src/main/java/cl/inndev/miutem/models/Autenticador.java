package cl.inndev.miutem.models;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cl.inndev.miutem.interfaces.ApiUtem;
import cl.inndev.miutem.interfaces.IAuthenticatorServer;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Autenticador implements IAuthenticatorServer {

    private ApiUtem mApiUtem;

    public Autenticador() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUtem.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        this.mApiUtem = retrofit.create(ApiUtem.class);
    }

    @Override
    public String getToken(String userName, String pwd) {
        Call<Estudiante.Credenciales> credencialesCall = mApiUtem.autenticar(userName, pwd);

        try {
            Response<Estudiante.Credenciales> response = credencialesCall.execute();
            return response.body().getToken();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
