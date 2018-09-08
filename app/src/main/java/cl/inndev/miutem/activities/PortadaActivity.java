package cl.inndev.miutem.activities;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nytimes.android.external.fs3.SourcePersisterFactory;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.MemoryPolicy;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import com.nytimes.android.external.store3.middleware.GsonParserFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import cl.inndev.miutem.R;
import cl.inndev.miutem.models.AuthPreferences;
import cl.inndev.miutem.models.Estudiante;
import cl.inndev.miutem.interfaces.ApiUtem;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PortadaActivity extends AppCompatActivity {
    private final int DURACION_SPLASH = 2000;
    private AccountManager mAccountManager;
    private FirebaseAnalytics mFirebaseAnalytics;
    private ApiUtem mApiUtem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        mAccountManager = AccountManager.get(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

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

        mApiUtem = retrofit.create(ApiUtem.class);
        */

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_portada);
        new Handler().postDelayed(() -> {
            startActivity(new Intent(PortadaActivity.this, MainActivity.class));
            finish();
            /*
            if (mAccountManager.getAccounts().length < 1) {
                addAccount();
            } else if (mAccountManager.getAccounts().length == 1) {
                getToken();
            } else {
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            }*/
        }, DURACION_SPLASH);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*
    public void addAccount() {
        mAccountManager.addAccount(AuthPreferences.ACCOUNT_TYPE,
                "Bearer", null, null, this,
                future -> {
                    try {
                        Bundle bundle = future.getResult();
                        if (mAccountManager.getAccounts().length == 1) {
                            getToken();
                        } else {
                            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    } catch (OperationCanceledException | AuthenticatorException | IOException e) {
                        e.printStackTrace();
                    }
                }, null);
    }

    private void getToken() {
        mAccountManager.getAuthToken(mAccountManager.getAccounts()[0],
                "Bearer", null, this,
                future -> {
                    try {
                        String token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                        String rut = mAccountManager.getUserData(mAccountManager.getAccounts()[0], LoginActivity.PARAM_USER_RUT);
                        Call<Estudiante.Credenciales> credencialesCall = mApiUtem.validar("Bearer " + token);
                        credencialesCall.enqueue(new Callback<Estudiante.Credenciales>() {
                            @Override
                            public void onResponse(Call<Estudiante.Credenciales> call, Response<Estudiante.Credenciales> response) {
                                if (response.body().getEsValido()) {
                                    getEstudiante(token, rut);
                                } else {
                                    mAccountManager.invalidateAuthToken(AuthPreferences.ACCOUNT_TYPE, token);
                                    getToken();
                                }
                            }

                            @Override
                            public void onFailure(Call<Estudiante.Credenciales> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                        e.printStackTrace();
                    }
                }, null);
    }

    private void getEstudiante(String token, String rut) {
        BarCode usuario = new BarCode(Estudiante.class.getSimpleName(), rut);
        try {
            Store<Estudiante, BarCode> store = provideEstudianteStore(token);
            store.get(usuario)
                    .subscribeOn(Schedulers.io())
                    .subscribe(estudiante -> {
                        setFirebaseUserProperties(estudiante);
                        startActivity(new Intent(PortadaActivity.this, MainActivity.class));
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final class BufferedSourceConverterFactory extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                                Retrofit retrofit) {
            if (!BufferedSource.class.equals(type)) {
                return null;
            }
            return (Converter<ResponseBody, BufferedSource>) value -> value.source();
        }
    }

    private Store<Estudiante, BarCode> provideEstudianteStore(String token) throws IOException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        ApiUtem apiUtem = new Retrofit.Builder()
                .baseUrl(ApiUtem.BASE_URL)
                .addConverterFactory(new BufferedSourceConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiUtem.class);

        return StoreBuilder.<BarCode, BufferedSource, Estudiante>parsedWithKey()
                .fetcher(barCode -> apiUtem.getBufferedEstudiante(barCode.getKey(), "Bearer " + token))
                .parser(GsonParserFactory.createSourceParser(gson, Estudiante.class))
                .persister(SourcePersisterFactory.create(this.getCacheDir()))
                //.refreshOnStale()
                .memoryPolicy(MemoryPolicy.builder()
                        .setExpireAfterWrite(10)
                        .setExpireAfterTimeUnit(TimeUnit.MINUTES)
                        .build())
                .open();
    }
    */

    private void setFirebaseUserProperties(Estudiante estudiante) {
        mFirebaseAnalytics.setUserProperty("anio_ingreso", estudiante.getStringAnioIngreso());
        mFirebaseAnalytics.setUserProperty("carreras_cursadas", estudiante.getStringCarrerasCursadas());
        mFirebaseAnalytics.setUserProperty("ultima_matricula", estudiante.getStringUltimaMatricula());
        //mFirebaseAnalytics.setUserProperty("anio_nacimiento", estudiante.getNacimiento().toString());
        if (estudiante.getSexo() != null) {
            mFirebaseAnalytics.setUserProperty("sexo", estudiante.getSexo().getDescripcion());
        }
    }
}
