package cl.inndev.miutem.activities;

import android.content.Context;
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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import cl.inndev.miutem.interfaces.ApiUtem;
import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.Estudiante;
import cl.inndev.miutem.classes.PreferencesManager;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;

public class LoginActivity extends AppCompatActivity {
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
    private int reintento = 0;
    private ConstraintLayout loginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        imagePerfil = (CircleImageView) findViewById(R.id.image_perfil);

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
                            PreferencesManager.getStringUser(getApplicationContext(), "correo_utem", null),
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
        return PreferencesManager.getBoolPreference(getApplicationContext(),
                "primera_vez",
                true);
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
            Estudiante valores = new Estudiante().convertirPreferencias(this);
            imagePerfil.setVisibility(View.VISIBLE);
            textBienvenida.setVisibility(View.VISIBLE);
            textNombre.setVisibility(View.VISIBLE);
            textCambiar.setVisibility(View.VISIBLE);

            textCorreo.setVisibility(View.GONE);
            editCorreo.setVisibility(View.GONE);

            textNombre.setText(valores.getNombre());
            textCambiar.setText("¿No eres " + valores.getNombre().substring(0,  valores.getNombre().indexOf(' ')) + "?");
            new DownloadImageTask(imagePerfil).execute(valores.getFotoUrl());
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
        Context context = getApplicationContext();
        PreferencesManager.clearAll(context);

        PreferencesManager.setPreference(context,"primera_vez", true);
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
        Call<Estudiante> call = restClient.autenticar(correo, contrasenia);

        call.enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(Call<Estudiante> call, Response<Estudiante> response) {
                reintento = 0;
                switch (response.code()) {
                    case 200:
                        Estudiante.setCredenciales(LoginActivity.this,response.body().getToken(), response.body().getRut(), correo);
                        obtenerPerfil();
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
            public void onFailure(Call<Estudiante> call, Throwable t) {
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

    private void obtenerPerfil() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem client = retrofit.create(ApiUtem.class);

        Map<String, String> credenciales = Estudiante.getCredenciales(LoginActivity.this);

        Call<Estudiante> call = client.obtenerPerfil(credenciales.get("rut"), credenciales.get("token"));

        call.enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(Call<Estudiante> call, Response<Estudiante> response) {
                switch (response.code()) {
                    case 200:
                        Estudiante usuario = response.body();
                        usuario.guardarDatos(LoginActivity.this);

                        // Boolean eraPrimeraVez = esPrimeraVez();

                        PreferencesManager.setPreference(LoginActivity.this, "primera_vez", false);

                        /*
                        if (eraPrimeraVez) {
                            startActivity(new Intent(LoginActivity.this, BienvenidaActivity.class));
                        } else {
                            startActivity(new Intent(LoginActivity.this, PerfilActivity.class));
                        }*/

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                        break;
                    default:
                        configurarFormulario(true);
                        Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<Estudiante> call, Throwable t) {
                if (t instanceof TimeoutException) {
                    Toast.makeText(LoginActivity.this, "¡Ups! Parece que la conexión está algo lenta. Por favor inténtalo nuevamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
                }
                configurarFormulario(true);
            }
        });
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
