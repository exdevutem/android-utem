package cl.inndev.miutem.activities;

import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirPutCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Carrera;
import cl.inndev.miutem.deserializers.CarrerasDeserializer;
import cl.inndev.miutem.interfaces.ApiUtem;
import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.Estudiante;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    private TextView textCorreo;
    private TextView textBienvenida;
    private TextView textNombre;
    private TextView textCambiar;
    private TextView textRecuperar;
    private EditText editCorreo;
    private EditText editContrasenia;
    private ProgressBar progressIniciando;
    private Button buttonEntrar;
    private CircleImageView imagePerfil;
    private ConstraintLayout loginLayout;

    private int reintento = 0;

    private List<Integer> mProcesos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        try {
            Reservoir.init(this, 4096);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mProcesos = new ArrayList<>();

        mProcesos.add(-1);
        mProcesos.add(-1);
        mProcesos.add(-1);
        mProcesos.add(0);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        loginLayout = findViewById(R.id.layout_activity_login);
        textCorreo = findViewById(R.id.text_correo);
        textBienvenida = findViewById(R.id.text_bienvenida);
        textNombre = findViewById(R.id.text_nombre);
        textCambiar = findViewById(R.id.text_cambiar);
        textRecuperar = findViewById(R.id.text_recuperar);
        editCorreo = findViewById(R.id.edit_correo);
        editContrasenia = findViewById(R.id.edit_contrasenia);
        progressIniciando = findViewById(R.id.progress_iniciando);
        buttonEntrar = findViewById(R.id.button_entrar);
        imagePerfil = findViewById(R.id.image_perfil);

        configurarFormulario(true);
        buttonEntrar.setEnabled(false);

        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (esPrimeraVez()) {
                    validarFormulario(editCorreo.getText().toString(),
                            editContrasenia.getText().toString());
                } else {
                    validarFormulario(
                            Prefs.getString("correo", null),
                            editContrasenia.getText().toString());
                }
            }
        });

        textCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarUsuario();
                configurarFormulario(true);
                buttonEntrar.setEnabled(false);

            }
        });

        textRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://pasaporte.utem.cl/reset";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        editCorreo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editContrasenia.getText().toString().length() > 0 && editCorreo.getText().toString().length() > 0) {
                    buttonEntrar.setEnabled(true);
                } else {
                    buttonEntrar.setEnabled(false);
                }
            }
        });

        editContrasenia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!esPrimeraVez()) {
                    if (editContrasenia.getText().toString().length() > 0) {
                        buttonEntrar.setEnabled(true);
                    } else {
                        buttonEntrar.setEnabled(false);
                    }
                } else {
                    if (editContrasenia.getText().toString().length() > 0 && editCorreo.getText().toString().length() > 0) {
                        buttonEntrar.setEnabled(true);
                    } else {
                        buttonEntrar.setEnabled(false);
                    }
                }

            }
        });
    }

    private boolean esPrimeraVez() {
        return Prefs.getBoolean("primera_vez", true);
    }

    private void configurarFormulario(boolean interruptor) {
        if (esPrimeraVez()) {
            imagePerfil.setVisibility(View.GONE);
            textBienvenida.setVisibility(View.GONE);
            textNombre.setVisibility(View.GONE);
            textCambiar.setVisibility(View.GONE);

            textCorreo.setVisibility(View.VISIBLE);
            editCorreo.setVisibility(View.VISIBLE);
        } else {
            try {
                Estudiante valores = Reservoir.get("usuario", Estudiante.class);
                imagePerfil.setVisibility(View.VISIBLE);
                textBienvenida.setVisibility(View.VISIBLE);
                textNombre.setVisibility(View.VISIBLE);
                textCambiar.setVisibility(View.VISIBLE);

                textCorreo.setVisibility(View.GONE);
                editCorreo.setVisibility(View.GONE);

                textNombre.setText(valores.getNombre().getCompleto());
                //textCambiar.setText("¿No eres " + valores.getNombre().substring(0,  valores.getNombre().indexOf(' ')) + "?");
                new DownloadImageTask(imagePerfil).execute(valores.getFotoUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (interruptor) {
            editCorreo.setEnabled(true);
            editContrasenia.setEnabled(true);
            progressIniciando.setVisibility(View.INVISIBLE);
            buttonEntrar.setClickable(true);
            buttonEntrar.setEnabled(true);
        } else {
            editCorreo.setEnabled(false);
            editContrasenia.setEnabled(false);
            progressIniciando.setVisibility(View.VISIBLE);
            buttonEntrar.setClickable(false);
            buttonEntrar.setEnabled(false);
        }

    }

    private void validarFormulario(String valorCorreo, String valorContrasenia) {
        if ((valorCorreo == null || valorCorreo.isEmpty()) && (valorContrasenia == null || valorContrasenia.isEmpty())) {
            editCorreo.setError("Este campo no puede estar vacío");
            editContrasenia.setError("Este campo no puede estar vacío");
        } else if (valorCorreo == null || valorCorreo.isEmpty()) {
            editCorreo.setError("Este campo no puede estar vacío");
        } else if (valorContrasenia == null || valorContrasenia.isEmpty()) {
            editContrasenia.setError("Este campo no puede estar vacío");
        } else if (!valorCorreo.endsWith("@utem.cl")) {
            editCorreo.setError("Debe ingresar un correo UTEM válido");
        } else {
            configurarFormulario(false);
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem restClient = retrofit.create(ApiUtem.class);
        Call<Estudiante.Credenciales> call = restClient.autenticar(correo, contrasenia);

        call.enqueue(new Callback<Estudiante.Credenciales>() {
            @Override
            public void onResponse(Call<Estudiante.Credenciales> call, Response<Estudiante.Credenciales> response) {
                reintento = 0;
                switch (response.code()) {
                    case 200:
                        Prefs.putString("token", response.body().getToken());
                        Prefs.putString("correo", response.body().getCorreo());
                        Prefs.putLong("rut", response.body().getRut());
                        Bundle event = new Bundle();
                        event.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, "pasaporte");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, event);
                        getPerfil();
                        getCarreras();
                        getAsignaturas();
                        // getHorarios();
                        break;
                    case 401:
                        configurarFormulario(true);
                        Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        configurarFormulario(true);
                        Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<Estudiante.Credenciales> call, Throwable t) {
                configurarFormulario(true);
                if (t instanceof SocketTimeoutException) {
                    switch (reintento) {
                        case 0:
                            reintento = 1;
                            Snackbar.make(loginLayout, "Tiempo de espera agotado.", 5000)
                                    .setAction("Reintentar", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            iniciarSesion(correo, contrasenia);
                                            configurarFormulario(false);
                                        }
                                    })
                                    .show();
                            break;
                        default:
                            reintento = 0;
                            Snackbar.make(loginLayout, "Tiempo de espera agotado.", 5000).show();
                            break;
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getPerfil() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem client = retrofit.create(ApiUtem.class);

        Call<Estudiante> call = client.getPerfil(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null));

        call.enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(Call<Estudiante> call, Response<Estudiante> response) {
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
            public void onFailure(Call<Estudiante> call, Throwable t) {
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem restClient = retrofit.create(ApiUtem.class);

        Call<List<Asignatura>> call = restClient.getAsignaturas(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null)
        );

        call.enqueue(new Callback<List<Asignatura>>() {
            @Override
            public void onResponse(Call<List<Asignatura>> call, Response<List<Asignatura>> response) {
                switch (response.code()) {
                    case 200:
                        mProcesos.set(1, 1);
                        try {
                            Reservoir.put("asignaturas", response.body());
                        } catch (IOException e) {
                            Toast.makeText(LoginActivity.this, "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<List<Asignatura>> call, Throwable t) {
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
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem client = retrofit.create(ApiUtem.class);

        Call<List<Carrera>> call = client.getCarreras(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null));

        call.enqueue(new Callback<List<Carrera>>() {
            @Override
            public void onResponse(Call<List<Carrera>> call, Response<List<Carrera>> response) {
                switch (response.code()) {
                    case 200:
                        mProcesos.set(2, 1);
                        try {
                            Reservoir.put("carreras", response.body());
                        } catch (IOException e) {
                            Toast.makeText(LoginActivity.this, "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<List<Carrera>> call, Throwable t) {
                mProcesos.set(2, 0);
                if (t instanceof TimeoutException) {
                    Toast.makeText(LoginActivity.this, "¡Ups! Parece que la conexión está algo lenta. Por favor inténtalo nuevamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
                }
                cambiarActivity();
            }
        });
    }

    private void cambiarActivity() {
        if (mProcesos.get(0) == 1) {
            if (mProcesos.get(1) >= 0 && mProcesos.get(2) >= 0 && mProcesos.get(3) >= 0) {
                /*
                if (eraPrimeraVez) {
                    startActivity(new Intent(LoginActivity.this, BienvenidaActivity.class));
                } else {
                    startActivity(new Intent(LoginActivity.this, PerfilActivity.class));
                }
                */

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        CircleImageView bmImage;

        public DownloadImageTask(CircleImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
