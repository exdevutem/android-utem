package cl.inndev.miutem.activities;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.AccountGeneral;
import cl.inndev.miutem.classes.Estudiante;
import cl.inndev.miutem.interfaces.ApiUtem;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.observers.DisposableLambdaObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PortadaActivity extends AppCompatActivity {
    private final int DURACION_SPLASH = 2000;
    private AccountManager mAccountManager;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountManager = AccountManager.get(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_portada);
        new Handler().postDelayed(() -> {
            if (mAccountManager.getAccounts().length < 1) {
                addAccount();
            } else if (mAccountManager.getAccounts().length == 1) {
                getToken();
            } else {
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        }, DURACION_SPLASH);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void addAccount() {
        mAccountManager.addAccount(AccountGeneral.ACCOUNT_TYPE,
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
                    BarCode usuario = new BarCode(Estudiante.class.getSimpleName(), "19649846");
                    try {
                        mToken = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                        provideEstudianteStore()
                                .get(usuario)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<Estudiante>() {
                                    @Override
                                    public void onSubscribe(Disposable d) { }

                                    @Override
                                    public void onSuccess(Estudiante estudiante) {
                                        Log.d("POLLO", estudiante.getNombre().getCompleto());
                                        startActivity(new Intent(PortadaActivity.this, MainActivity.class));
                                        finish();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                    }
                                });
                    } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                        e.printStackTrace();
                    }
                }, null);
    }

    private Store<Estudiante, BarCode> provideEstudianteStore() throws IOException {
        Log.d("POLLO", mToken);
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        ApiUtem apiUtem = new Retrofit.Builder()
                .baseUrl(ApiUtem.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiUtem.class);

        return StoreBuilder.<Estudiante>barcode()
                .fetcher(barCode -> apiUtem.getPerfil(barCode.getKey(), "Bearer " + mToken))
                .open();
    }
}
