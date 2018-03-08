package cl.inndev.utem;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
            Toast.makeText(LoginActivity.this, "Iniciando sesión...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String respuesta) {
            super.onPostExecute(respuesta);
            if (respuesta == null) {
                Toast.makeText(LoginActivity.this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(LoginActivity.this, respuesta, Toast.LENGTH_LONG).show();
                try {
                    JSONObject jObject = new JSONObject(respuesta);
                    Toast.makeText(LoginActivity.this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();
                    new Estudiante().execute("estudiantes/" + rut.getText(), jObject.getString("token"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }

        private class Estudiante extends ConexionApi.Get {

            @Override
            protected void onPreExecute() {
                Toast.makeText(LoginActivity.this, "Cargando datos...", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(String respuesta) {
                super.onPostExecute(respuesta);
                if (respuesta == null) {
                    Toast.makeText(LoginActivity.this, "No se pudo cargar los datos: " + respuesta, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jObject = new JSONObject(respuesta);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("NOMBRE", jObject.getString("nombre"));
                        intent.putExtra("CORREO", jObject.getString("rut"));
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }
        }
    }



}
