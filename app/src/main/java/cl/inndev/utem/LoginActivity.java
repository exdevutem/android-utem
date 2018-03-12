package cl.inndev.utem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText rut;
    private EditText contrasenia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rut = (EditText) findViewById(R.id.rutInput);
        contrasenia = (EditText) findViewById(R.id.contraseniaInput);

        Button entrar = (Button) findViewById(R.id.entrarButton);
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validar(rut.getText().toString(), contrasenia.getText().toString());
            }
        });

        TextView recuperar = (TextView) findViewById(R.id.recuperarText);
        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://mi.utem.cl/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    private void validar(String rut, String pass) {
        if (rut == null || rut.isEmpty() || pass == null || pass.isEmpty()) {

        } else {
            new Autenticar().execute("autenticacion", rut, pass);
        }
    }

    private class Autenticar extends ConexionApi.Post {

        @Override
        protected void onPreExecute() {
            ProgressBar cargando = (ProgressBar)findViewById(R.id.iniciandoProgress);
            cargando.setVisibility(View.VISIBLE);
            Button entrar = (Button) findViewById(R.id.entrarButton);
            entrar.setClickable(false);

            //Toast.makeText(LoginActivity.this, "Iniciando sesión...", Toast.LENGTH_SHORT).show();
        }



        @Override
        protected void onPostExecute(String respuesta) {
            super.onPostExecute(respuesta);
            if (respuesta == null) {
                ProgressBar cargando = (ProgressBar)findViewById(R.id.iniciandoProgress);
                cargando.setVisibility(View.GONE);
                Button entrar = (Button) findViewById(R.id.entrarButton);
                entrar.setClickable(true);

                Toast.makeText(LoginActivity.this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(LoginActivity.this, respuesta, Toast.LENGTH_LONG).show();
                try {
                    SharedPreferences preferences = getSharedPreferences("alumno", Context.MODE_PRIVATE);
                    preferences.getString("token", "No existe");
                    JSONObject jObject = new JSONObject(respuesta);
                    guardarString("alumno", "token", jObject.getString("token"));
                    //Toast.makeText(LoginActivity.this, getSharedPreferences("alumno", Context.MODE_PRIVATE).getString("token", "No existe"), Toast.LENGTH_SHORT).show();
                    new Estudiante().execute("estudiantes/" + preferences.getString("rut", "").substring(0, 8), preferences.getString("token", ""));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private class Estudiante extends ConexionApi.Get {
            @Override
            protected void onPostExecute(String respuesta) {
                super.onPostExecute(respuesta);
                if (respuesta == null) {
                    ProgressBar cargando = (ProgressBar)findViewById(R.id.iniciandoProgress);
                    cargando.setVisibility(View.GONE);
                    Button entrar = (Button) findViewById(R.id.entrarButton);
                    entrar.setClickable(true);

                    Toast.makeText(LoginActivity.this, "No se pudo cargar los datos: " + respuesta, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jObject = new JSONObject(respuesta);
                        guardarString("alumno", "nombre", jObject.getString("nombre"));
                        guardarString("alumno", "rut", jObject.getString("rut"));
                        guardarString("alumno", "email", jObject.getString("email"));;

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }
        }
    }

    private void guardarString(String preferencias, String clave, String valor) {
        SharedPreferences preferences = getSharedPreferences(preferencias, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(clave, valor);
        editor.commit();
    }
}
