package cl.inndev.miutem.activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.concurrent.TimeUnit;

import cl.inndev.miutem.models.AuthPreferences;
import cl.inndev.miutem.interfaces.ApiUtem;
import cl.inndev.miutem.R;
import cl.inndev.miutem.models.Estudiante;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AccountAuthenticatorActivity {

    public static final String ARG_ACCOUNT_TYPE = "accountType";
    public static final String ARG_AUTH_TYPE = "authTokenType";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "isAddingNewAccount";
    public static final String PARAM_USER_PASS = "password";
    public static final String PARAM_USER_RUT = "rut";

    private AccountManager mAccountManager;
    private String mAuthTokenType;

    private FirebaseAnalytics mFirebaseAnalytics;

    private TextView mTextCorreo;
    private TextView mTextBienvenida;
    private TextView mTextNombre;
    private TextView mTextCambiar;
    private TextView mTextRecuperar;
    private EditText mEditCorreo;
    private EditText mEditContrasenia;
    private CheckBox mCheckRecordar;
    private ProgressBar mProgressIniciando;
    private Button mButtonEntrar;
    private ImageView mImagePerfil;
    private ConstraintLayout mLayoutLogin;
    private String mAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAccountManager = AccountManager.get(this);
        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);

        if (mAuthTokenType == null) {
            mAuthTokenType = AuthPreferences.AUTHTOKEN_TYPE_TEST;
        }

        if (mAccountType == null) {
            mAccountType = AuthPreferences.ACCOUNT_TYPE;
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mLayoutLogin = findViewById(R.id.layout_activity_login);
        mTextCorreo = findViewById(R.id.text_correo);
        mTextBienvenida = findViewById(R.id.text_bienvenida);
        mTextNombre = findViewById(R.id.text_nombre);
        mTextCambiar = findViewById(R.id.text_cambiar);
        mTextRecuperar = findViewById(R.id.text_recuperar);
        mEditCorreo = findViewById(R.id.edit_correo);
        mEditContrasenia = findViewById(R.id.edit_contrasenia);
        mCheckRecordar = findViewById(R.id.check_recordar);
        mProgressIniciando = findViewById(R.id.progress_iniciando);
        mButtonEntrar = findViewById(R.id.button_entrar);
        mImagePerfil = findViewById(R.id.image_perfil);

        mButtonEntrar.setEnabled(false);
        //configurarFormulario(true);

        mButtonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if (esPrimeraVez()) {
                    validarFormulario(mEditCorreo.getText().toString(),
                            mEditContrasenia.getText().toString());
                } else {
                    validarFormulario(
                            Prefs.getString("correo", null),
                            mEditContrasenia.getText().toString());
                }
                */
                validarFormulario(mEditCorreo.getText().toString(),
                        mEditContrasenia.getText().toString());
            }

        });

        /*
        mTextCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarUsuario();
                configurarFormulario(true);
                mCheckRecordar.setChecked(false);
                mButtonEntrar.setEnabled(false);
                mEditContrasenia.setText("");
            }
        });
        */

        mTextRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://pasaporte.utem.cl/reset";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        mEditCorreo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEditContrasenia.getText().toString().length() > 0 && mEditCorreo.getText().toString().length() > 0) {
                    mButtonEntrar.setEnabled(true);
                } else {
                    mButtonEntrar.setEnabled(false);
                }
            }
        });

        mEditContrasenia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEditContrasenia.getText().toString().length() > 0 && mEditCorreo.getText().toString().length() > 0) {
                    mButtonEntrar.setEnabled(true);
                } else {
                    mButtonEntrar.setEnabled(false);
                }
                /*
                if (!esPrimeraVez()) {
                    if (mEditContrasenia.getText().toString().length() > 0) {
                        mButtonEntrar.setEnabled(true);
                    } else {
                        mButtonEntrar.setEnabled(false);
                    }
                } else {
                    if (mEditContrasenia.getText().toString().length() > 0 && mEditCorreo.getText().toString().length() > 0) {
                        mButtonEntrar.setEnabled(true);
                    } else {
                        mButtonEntrar.setEnabled(false);
                    }
                }
                */

            }
        });
    }

    private void validarFormulario(String valorCorreo, String valorContrasenia) {
        if ((valorCorreo == null || valorCorreo.isEmpty()) && (valorContrasenia == null || valorContrasenia.isEmpty())) {
            mEditCorreo.setError("Este campo no puede estar vacío");
            mEditContrasenia.setError("Este campo no puede estar vacío");
        } else if (valorCorreo == null || valorCorreo.isEmpty()) {
            mEditCorreo.setError("Este campo no puede estar vacío");
        } else if (valorContrasenia == null || valorContrasenia.isEmpty()) {
            mEditContrasenia.setError("Este campo no puede estar vacío");
        } else if (!valorCorreo.endsWith("@utem.cl")) {
            mEditCorreo.setError("Debe ingresar un correo UTEM válido");
        } else {
            iniciarSesion(valorCorreo, valorContrasenia);
        }
    }

    private void eliminarUsuario() {
        Prefs.clear();
        Prefs.putBoolean("primera_vez", true);
    }

    private void iniciarSesion(final String correo, final String contrasenia) {
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

        ApiUtem restClient = retrofit.create(ApiUtem.class);
        Call<Estudiante.Credenciales> call = restClient.autenticar(correo, contrasenia);

        call.enqueue(new Callback<Estudiante.Credenciales>() {
            @Override
            public void onResponse(@NonNull Call<Estudiante.Credenciales> call,
                                   @NonNull Response<Estudiante.Credenciales> response) {
                switch (response.code()) {
                    case 200:
                        final Intent res = new Intent();
                        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, correo);
                        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                        res.putExtra(AccountManager.KEY_AUTHTOKEN, response.body().getToken());
                        res.putExtra(PARAM_USER_PASS, contrasenia);
                        res.putExtra(PARAM_USER_RUT, response.body().getRut().toString());
                        finishLogin(res);

                        /*
                        Prefs.putString("token", response.body().getToken());
                        Prefs.putString("correo", response.body().getCorreo());
                        Prefs.putLong("rut", response.body().getRut());
                        if (mCheckRecordar.isChecked()) {
                            Prefs.putString("contrasenia", contrasenia);
                        } else {
                            Prefs.remove("contrasenia");
                        }
                        */
                        break;
                    case 401:
                        //Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        //Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Estudiante.Credenciales> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void finishLogin(Intent intent) {
        String correo = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String contrasenia = intent.getStringExtra(PARAM_USER_PASS);
        String rut = intent.getStringExtra(PARAM_USER_RUT);
        final Account account = new Account(correo, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            String token = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String tokenType = mAuthTokenType;
            Bundle rutBundle = new Bundle();
            rutBundle.putString(PARAM_USER_RUT, rut);
            mAccountManager.addAccountExplicitly(account, contrasenia, rutBundle);
            mAccountManager.setAuthToken(account, tokenType, token);
        } else {
            mAccountManager.setPassword(account, contrasenia);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /*

    private void getPerfil() {
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

        ApiUtem client = retrofit.create(ApiUtem.class);

        Call<Estudiante> call = client.getPerfil(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null));

        call.enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(@NonNull Call<Estudiante> call, @NonNull Response<Estudiante> response) {
                switch (response.code()) {
                    case 200:
                        Estudiante usuario = response.body();
                        Reservoir.putAsync("usuario", usuario, new ReservoirPutCallback() {
                            @Override
                            public void onSuccess() {
                                mProcesos.set(0, 1);
                                Prefs.putBoolean("primera_vez", false);
                                cambiarActivity();
                                // Boolean eraPrimeraVez = esPrimeraVez();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                mProcesos.set(0, 0);
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "Error en el caché de los datos", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    default:
                        mProcesos.set(0, 0);
                        configurarFormulario(true);
                        Toast.makeText(LoginActivity.this, response.raw().toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Estudiante> call, @NonNull Throwable t) {
                mProcesos.set(0, 0);
                if (t instanceof TimeoutException) {
                    Toast.makeText(LoginActivity.this, "¡Ups! Parece que la conexión está algo lenta. Por favor inténtalo nuevamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
                }
                configurarFormulario(true);
            }
        });
    }

    private void getAsignaturas() {
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

        ApiUtem restClient = retrofit.create(ApiUtem.class);

        Call<List<Asignatura>> call = restClient.getAsignaturas(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null)
        );

        call.enqueue(new Callback<List<Asignatura>>() {
            @Override
            public void onResponse(@NonNull Call<List<Asignatura>> call, @NonNull Response<List<Asignatura>> response) {
                switch (response.code()) {
                    case 200:
                        mProcesos.set(1, 1);
                        try {
                            Reservoir.put("asignaturas", response.body());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        mProcesos.set(1, 0);
                        Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
                cambiarActivity();
            }

            @Override
            public void onFailure(@NonNull Call<List<Asignatura>> call, @NonNull Throwable t) {
                mProcesos.set(1, 0);
                Toast.makeText(LoginActivity.this, "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
                cambiarActivity();
            }
        });
    }

    private void getCarreras() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(List.class, new CarrerasDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUtem.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem client = retrofit.create(ApiUtem.class);

        Call<List<Carrera>> call = client.getCarreras(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null));

        call.enqueue(new Callback<List<Carrera>>() {
            @Override
            public void onResponse(@NonNull Call<List<Carrera>> call, @NonNull Response<List<Carrera>> response) {
                switch (response.code()) {
                    case 200:
                        mProcesos.set(2, 1);
                        try {
                            Reservoir.put("carreras", response.body());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        mProcesos.set(2, 0);
                        Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
                cambiarActivity();
            }

            @Override
            public void onFailure(@NonNull Call<List<Carrera>> call, @NonNull Throwable t) {
                mProcesos.set(2, 0);
                if (t instanceof TimeoutException) {
                    Toast.makeText(LoginActivity.this,
                            "¡Ups! Parece que la conexión está algo lenta. " +
                                    "Por favor inténtalo nuevamente",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Error: " + t.toString(),
                            Toast.LENGTH_SHORT).show();
                }
                cambiarActivity();
            }
        });
    }

    private void getHorario() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Horario.class, new HorariosDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem client = retrofit.create(ApiUtem.class);

        Call<Horario> call = client.getHorarios(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null)
        );

        call.enqueue(new Callback<Horario>() {
            @Override
            public void onResponse(Call<Horario> call, Response<Horario> response) {
                switch (response.code()) {
                    case 200:
                        mProcesos.set(3, 1);
                        try {
                            Reservoir.put("horarios", response.body());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        mProcesos.set(3, 0);
                        Toast.makeText(LoginActivity.this, "Ocurrió un error inesperado al cargar el horario", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<Horario> call, Throwable t) {
                t.printStackTrace();
                mProcesos.set(3, 0);
                Toast.makeText(LoginActivity.this, "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cambiarActivity() {
        if (mProcesos.get(0) == 1) {
            if (mProcesos.get(1) >= 0 && mProcesos.get(2) >= 0 && mProcesos.get(3) >= 0) {

                if (eraPrimeraVez) {
                    startActivity(new Intent(LoginActivity.this, BienvenidaActivity.class));
                } else {
                    startActivity(new Intent(LoginActivity.this, PerfilActivity.class));
                }


                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }
    }
    */
}
