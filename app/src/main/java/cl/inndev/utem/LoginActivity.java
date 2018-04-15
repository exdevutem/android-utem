package cl.inndev.utem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.utem.RestClient.BASE_URL;

public class LoginActivity extends AppCompatActivity {
    private TextView correoText;
    private EditText correo;
    private EditText contrasenia;
    private ProgressBar cargando;
    private Button entrar;
    private CircleImageView perfilImage;
    private TextView bienvenidaMensaje;
    private TextView bienvenidaNombre;
    private TextView cambiarUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!esPrimeraVez()) {
            SharedPreferences usuario = getSharedPreferences("usuario", Context.MODE_PRIVATE);
            new DownloadImageTask((CircleImageView) findViewById(R.id.perfilImage))
                    .execute(usuario.getString("foto-url", null));
        }

        correoText = (TextView) findViewById(R.id.correoText);
        correo = (EditText) findViewById(R.id.correoInput);
        contrasenia = (EditText) findViewById(R.id.contraseniaInput);
        perfilImage = (CircleImageView) findViewById(R.id.perfilImage);
        bienvenidaMensaje = (TextView) findViewById(R.id.bienvenidaMensaje);
        bienvenidaNombre = (TextView) findViewById(R.id.nombreBienvenida);
        cambiarUsuario = (TextView) findViewById(R.id.cambiarUsuarioText);
        cargando = (ProgressBar) findViewById(R.id.iniciandoProgress);
        entrar = (Button) findViewById(R.id.entrarButton);

        configurarFormulario(true);

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (esPrimeraVez()) {
                    validar(correo.getText().toString(), contrasenia.getText().toString());
                } else {
                    SharedPreferences usuario = getSharedPreferences("usuario", Context.MODE_PRIVATE);
                    new DownloadImageTask((CircleImageView) findViewById(R.id.perfilImage))
                            .execute(usuario.getString("foto-url", null));
                    validar(usuario.getString("correo-utem", null), contrasenia.getText().toString());
                }

            }
        });

        TextView cambiar = (TextView) findViewById(R.id.cambiarUsuarioText);
        cambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(LoginActivity.this, "" + esPrimeraVez(), Toast.LENGTH_SHORT).show();
                eliminarUsuario();
                //Toast.makeText(LoginActivity.this, "" + esPrimeraVez(), Toast.LENGTH_SHORT).show();
                configurarFormulario(true);

            }
        });

        // Recuperar contraseña
        TextView recuperar = (TextView) findViewById(R.id.recuperarText);
        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://pasaporte.utem.cl/reset";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    private boolean esPrimeraVez() {
        SharedPreferences preferences = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        if (preferences.contains("primera-vez") && preferences.getBoolean("primera-vez", false)) {
            return true;
        } else {
            return false;
        }
    }

    private void validar(String valorCorreo, String valorContrasenia) {
        correo = (EditText) findViewById(R.id.correoInput);
        contrasenia = (EditText) findViewById(R.id.contraseniaInput);
        if ((valorCorreo == null || valorCorreo.isEmpty()) && (valorContrasenia == null || valorContrasenia.isEmpty())) {
            correo.setError("Debe ingresar un RUT");
            contrasenia.setError("Debe ingresar una contraseña");
        } else if (valorCorreo == null || valorCorreo.isEmpty()) {
            correo.setError("Debe ingresar un RUT");
        } else if (valorContrasenia == null || valorContrasenia.isEmpty()) {
            contrasenia.setError("Debe ingresar una contraseña");
        } else {
            configurarFormulario(false);
            login(valorCorreo, valorContrasenia);
        }
    }


    private void configurarFormulario(boolean interruptor) {
        if (esPrimeraVez()) {
            perfilImage.setVisibility(View.GONE);
            bienvenidaMensaje.setVisibility(View.GONE);
            bienvenidaNombre.setVisibility(View.GONE);
            cambiarUsuario.setVisibility(View.GONE);

            correoText.setVisibility(View.VISIBLE);
            correo.setVisibility(View.VISIBLE);
        } else {
            SharedPreferences preferences = getSharedPreferences("usuario", Context.MODE_PRIVATE);
            perfilImage.setVisibility(View.VISIBLE);
            bienvenidaMensaje.setVisibility(View.VISIBLE);
            bienvenidaNombre.setVisibility(View.VISIBLE);
            cambiarUsuario.setVisibility(View.VISIBLE);
            bienvenidaNombre.setText(preferences.getString("nombre", "Usuario desconocido"));
            cambiarUsuario.setText("¿No eres " + preferences.getString("nombre", "usuario") + "?");

            correoText.setVisibility(View.GONE);
            correo.setVisibility(View.GONE);
        }

        if (interruptor) {
            correo.setEnabled(true);
            contrasenia.setEnabled(true);
            cargando.setVisibility(View.GONE);
            entrar.setClickable(true);
            entrar.setEnabled(true);
        } else {
            correo.setEnabled(false);
            contrasenia.setEnabled(false);
            cargando.setVisibility(View.VISIBLE);
            entrar.setClickable(false);
            entrar.setEnabled(false);
        }
    }

    private void eliminarUsuario() {
        SharedPreferences.Editor credenciales = getSharedPreferences("credenciales", MODE_PRIVATE).edit();
        credenciales.clear();
        credenciales.apply();
        SharedPreferences.Editor usuario = getSharedPreferences("usuario", MODE_PRIVATE).edit();
        usuario.clear();
        usuario.apply();

        SharedPreferences.Editor preferencias = getSharedPreferences("preferencias", MODE_PRIVATE).edit();
        preferencias.putBoolean("primera-vez", true);
        preferencias.apply();
    }

    private void login(String correo, String contrasenia) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestClient restClient = retrofit.create(RestClient.class);
        Call<Estudiante> call = restClient.autenticar(correo, contrasenia);

        call.enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(Call<Estudiante> call, Response<Estudiante> response) {
                switch (response.code()) {
                    case 200:
                        String rutAux = response.body().getRut().replaceAll("[.-]", "");
                        rutAux = rutAux.substring(0, rutAux.length() - 1);

                        SharedPreferences.Editor credenciales = getSharedPreferences("credenciales", MODE_PRIVATE).edit();
                        credenciales.putString("rut", rutAux);
                        credenciales.putString("token", response.body().getToken());
                        credenciales.apply();

                        obtenerPerfil(rutAux, "Bearer " + response.body().getToken());
                        break;
                    default:
                        configurarFormulario(true);
                        Toast.makeText(LoginActivity.this, "Ocurrió un problema con el login", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<Estudiante> call, Throwable t) {
                configurarFormulario(true);
                Toast.makeText(LoginActivity.this, "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerPerfil(String rut, String token) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestClient restClient = retrofit.create(RestClient.class);
        Call<Estudiante> call = restClient.obtenerPerfil(rut, token);

        call.enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(Call<Estudiante> call, Response<Estudiante> response) {
                switch (response.code()) {
                    case 200:
                        Estudiante usuario = response.body();
                        //Toast.makeText(LoginActivity.this, usuario.getEdad() != null ? usuario.getEdad().toString() : "null", Toast.LENGTH_SHORT).show();
                        usuario.guardarDatos(getApplicationContext());

                        SharedPreferences.Editor preferencias = getSharedPreferences("preferencias", MODE_PRIVATE).edit();
                        preferencias.putBoolean("primera-vez", false);
                        preferencias.apply();

                        Intent intent = new Intent(LoginActivity.this, PerfilActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        configurarFormulario(true);
                        Toast.makeText(LoginActivity.this, "Ocurrió un problema con el login", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<Estudiante> call, Throwable t) {
                configurarFormulario(true);
                Toast.makeText(LoginActivity.this, "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
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
