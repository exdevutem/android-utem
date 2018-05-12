package cl.inndev.utem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.utem.ApiUtem.BASE_URL;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textCorreo = findViewById(R.id.text_correo);
        textBienvenida = findViewById(R.id.text_bienvenida);
        textNombre = findViewById(R.id.text_nombre);
        textCambiar = findViewById(R.id.text_cambiar);
        textRecuperar = findViewById(R.id.text_recuperar);
        editCorreo = findViewById(R.id.edit_correo);
        editContrasenia = findViewById(R.id.edit_contrasenia);
        progressIniciando = findViewById(R.id.progress_iniciando);
        buttonEntrar = findViewById(R.id.button_entrar);
        // perfilImage = (CircleImageView) findViewById(R.id.image_perfil);

        configurarFormulario(true);
        buttonEntrar.setEnabled(false);

        /*
        if (!esPrimeraVez()) {
            SharedPreferences usuario = getSharedPreferences("usuario", Context.MODE_PRIVATE);
            new DownloadImageTask((CircleImageView) findViewById(R.id.perfilImage))
                    .execute(usuario.getString("foto-url", null));
        }*/


        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (esPrimeraVez()) {
                    validarFormulario(editCorreo.getText().toString(),
                            editContrasenia.getText().toString());
                } else {
                    validarFormulario(
                            PrefManager.getStringUser(getApplicationContext(), "correo_utem", null),
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
        return PrefManager.getBoolPreference(getApplicationContext(),
                "primera_vez",
                true);
    }

    private void configurarFormulario(boolean interruptor) {
        if (esPrimeraVez()) {
            //perfilImage.setVisibility(View.GONE);
            textBienvenida.setVisibility(View.GONE);
            textNombre.setVisibility(View.GONE);
            textCambiar.setVisibility(View.GONE);

            textCorreo.setVisibility(View.VISIBLE);
            editCorreo.setVisibility(View.VISIBLE);
        } else {
            String nombre = PrefManager.getStringUser(getApplicationContext(), "nombre", null);
            //perfilImage.setVisibility(View.VISIBLE);
            textBienvenida.setVisibility(View.VISIBLE);
            textNombre.setVisibility(View.VISIBLE);
            textCambiar.setVisibility(View.VISIBLE);

            textCorreo.setVisibility(View.GONE);
            editCorreo.setVisibility(View.GONE);

            textNombre.setText(nombre);
            textCambiar.setText("¿No eres " + nombre + "?");
        }

        if (interruptor) {
            editCorreo.setEnabled(true);
            editContrasenia.setEnabled(true);
            progressIniciando.setVisibility(View.GONE);
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
        PrefManager.clearAll(context);

        PrefManager.setPreference(context,"primera_vez", true);
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
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(LoginActivity.this, "¡Ups! Parece que la conexión está algo lenta. Por favor inténtalo nuevamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
                }
                configurarFormulario(true);
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

                        PrefManager.setPreference(LoginActivity.this, "primera_vez", false);

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

    /*
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
    } */
}
