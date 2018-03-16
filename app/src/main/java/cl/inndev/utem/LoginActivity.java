package cl.inndev.utem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
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
    private ProgressBar cargando;
    private Button entrar;

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
                rut.setText(Rut.Formatear(rut.getText().toString()));
                String url = "https://mi.utem.cl/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        rut.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (!rut.getText().toString().isEmpty()) {
                        rut.setText(Rut.Formatear(rut.getText().toString()));
                    }
                }
            }
        });
    }

    private void validar(String valorRut, String valorContrasenia) {
        rut = (EditText) findViewById(R.id.rutInput);
        contrasenia = (EditText) findViewById(R.id.contraseniaInput);
        if ((valorRut == null || valorRut.isEmpty()) && (valorContrasenia == null || valorContrasenia.isEmpty())) {
            rut.setError("Debe ingresar un RUT");
            contrasenia.setError("Debe ingresar una contraseña");
        } else if (valorRut == null || valorRut.isEmpty()) {
            rut.setError("Debe ingresar un RUT");
        } else if (valorContrasenia == null || valorContrasenia.isEmpty()) {
            contrasenia.setError("Debe ingresar una contraseña");
        } else if (!Rut.Validar(valorRut) || valorRut.length() < 8) {
            rut.setError("El RUT ingresado no es válido");
        } else {
            rut.setText(Rut.Formatear(rut.getText().toString()));
            configurarFormulario(false);
            new Autenticar().execute("autenticacion", Rut.Numerico(valorRut), valorContrasenia);
        }
    }

    private void configurarFormulario(boolean interruptor) {
        rut = (EditText) findViewById(R.id.rutInput);
        contrasenia = (EditText) findViewById(R.id.contraseniaInput);
        cargando = (ProgressBar) findViewById(R.id.iniciandoProgress);
        entrar = (Button) findViewById(R.id.entrarButton);

        if (interruptor) {
            rut.setEnabled(true);
            contrasenia.setEnabled(true);
            cargando.setVisibility(View.GONE);
            entrar.setClickable(true);
            entrar.setEnabled(true);
        } else {
            rut.setEnabled(false);
            contrasenia.setEnabled(false);
            cargando.setVisibility(View.VISIBLE);
            entrar.setClickable(false);
            entrar.setEnabled(false);
        }
    }

    private class Autenticar extends ApiUtem.Post {
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
                try {
                    SharedPreferences preferences = getSharedPreferences("alumno", Context.MODE_PRIVATE);
                    JSONObject jObject = new JSONObject(respuesta);
                    guardarString("alumno", "token", jObject.getString("token"));

                    rut = (EditText) findViewById(R.id.rutInput);

                    String token = preferences.getString("token", null);
                    String valorRut = Rut.Numerico(rut.getText().toString());
                    new Estudiante().execute("estudiantes/" + valorRut + "/", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private class Estudiante extends ApiUtem.Get {
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
                        guardarString("alumno", "email", "dzdf");;

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
