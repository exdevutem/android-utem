package cl.inndev.utem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hbb20.CountryCodePicker;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.VISIBLE;
import static cl.inndev.utem.RestClient.BASE_URL;

public class PerfilActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mostrarDatos();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPreferences credenciales = getSharedPreferences("credenciales", MODE_PRIVATE);

                String rutAux = credenciales.getString("rut", null) != null ? credenciales.getString("rut", null).replaceAll("[.-]", "") : null;
                //rutAux = rutAux.substring(0, rutAux.length() - 1);
                obtenerPerfil(rutAux, "Bearer " + credenciales.getString("token", null));

            }
        });


        /*
        //get the spinner from the xml.
        Spinner dropdown = (Spinner) findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        String[] items = new String[]{"Masculino", "Femenino", "Prefiero no especificar"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        */


    }

    private void mostrarDatos() {
        TextView nombre = (TextView) findViewById(R.id.nombreText);
        TextView rut = (TextView) findViewById(R.id.rutText);
        TextView tipo = (TextView) findViewById(R.id.tipoText);
        TextView ingreso = (TextView) findViewById(R.id.ingresoText);
        TextView matricula = (TextView) findViewById(R.id.matriculaText);
        TextView carreras = (TextView) findViewById(R.id.carrerasText);
        TextView correoUtem = (TextView) findViewById(R.id.correoUtemText);
        EditText edad = (EditText) findViewById(R.id.edadText);
        EditText correoPersonal = (EditText) findViewById(R.id.correoPersonalText);
        CountryCodePicker cFijo = (CountryCodePicker) findViewById(R.id.codigoFijo);
        EditText fijo = (EditText) findViewById(R.id.fijoText);
        CountryCodePicker cMovil = (CountryCodePicker) findViewById(R.id.codigoMovil);
        EditText movil = (EditText) findViewById(R.id.movilText);
        EditText direccion = (EditText) findViewById(R.id.direccionText);

        SharedPreferences usuario = getSharedPreferences("usuario", MODE_PRIVATE);

        new DownloadImageTask((CircleImageView) findViewById(R.id.profile_image))
                .execute(usuario.getString("foto-url", null));

        nombre.setText(usuario.getString("nombre", "Usuario desconocido"));
        rut.setText(usuario.getString("rut", "Sin RUT"));
        tipo.setText(usuario.getString("tipo", "Desconocido"));
        ingreso.setText(Integer.toString(usuario.getInt("anio-ingreso", 0)));
        matricula.setText(Integer.toString(usuario.getInt("ultima-matricula", 0)));
        carreras.setText(Integer.toString(usuario.getInt("carreras-cursadas", 0)));
        correoUtem.setText(usuario.getString("correo-utem", "No hay correo UTEM registrado"));
        if (usuario.contains("edad")) {
            edad.setText(Integer.toString(usuario.getInt("edad", 0)) + " años");
            edad.setVisibility(VISIBLE);
            findViewById(R.id.edad).setVisibility(VISIBLE);
        }

        if (usuario.contains("correo-personal")) {
            correoPersonal.setText(usuario.getString("correo-personal", null));
            correoPersonal.setVisibility(VISIBLE);
            findViewById(R.id.correoPersonal).setVisibility(VISIBLE);
        }

        if (usuario.contains("telefono-fijo")) {
            cFijo.setVisibility(VISIBLE);
            fijo.setText(Long.toString(usuario.getLong("telefono-fijo", 0)));
            fijo.setVisibility(VISIBLE);
            findViewById(R.id.telefonoFijo).setVisibility(VISIBLE);
        }

        if (usuario.contains("telefono-movil")) {
            cMovil.setVisibility(VISIBLE);
            movil.setText(Long.toString(usuario.getLong("telefono-movil", 0)));
            movil.setVisibility(VISIBLE);
            findViewById(R.id.telefonoMovil).setVisibility(VISIBLE);
        }

        if (usuario.contains("direccion")) {
            direccion.setText(usuario.getString("direccion", null));
            direccion.setVisibility(VISIBLE);
            findViewById(R.id.direccion).setVisibility(VISIBLE);
        }
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
                        usuario.guardarDatos(getApplicationContext());
                        mostrarDatos();
                        break;
                    default:
                        Toast.makeText(PerfilActivity.this, response.toString(), Toast.LENGTH_SHORT).show();

                        break;
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Estudiante> call, Throwable t) {
                swipeContainer.setRefreshing(false);
                Toast.makeText(PerfilActivity.this, "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
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
