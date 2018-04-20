package cl.inndev.utem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private Toolbar toolbar;
    private Button editar;

    private Drawable ic_close;
    private Drawable ic_back;

    private Boolean editando = false;

    private TextView nombre;
    private EditText rut;
    private TextView tipo;
    private TextView ingreso;
    private TextView matricula;
    private TextView carreras;
    private EditText correoUtem;
    private EditText edad;
    private EditText correoPersonal;
    private CountryCodePicker cFijo;
    private EditText fijo;
    private CountryCodePicker cMovil;
    private EditText movil;
    private EditText direccion;
    private CountryCodePicker paisPicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        nombre = (TextView) findViewById(R.id.nombreText);
        rut = (EditText) findViewById(R.id.rutText);
        tipo = (TextView) findViewById(R.id.tipoText);
        ingreso = (TextView) findViewById(R.id.ingresoText);
        matricula = (TextView) findViewById(R.id.matriculaText);
        carreras = (TextView) findViewById(R.id.carrerasText);
        correoUtem = (EditText) findViewById(R.id.correoUtemText);
        edad = (EditText) findViewById(R.id.edadText);
        correoPersonal = (EditText) findViewById(R.id.correoPersonalText);
        cFijo = (CountryCodePicker) findViewById(R.id.codigoFijo);
        fijo = (EditText) findViewById(R.id.fijoText);
        cMovil = (CountryCodePicker) findViewById(R.id.codigoMovil);
        movil = (EditText) findViewById(R.id.movilText);
        direccion = (EditText) findViewById(R.id.direccionText);
        paisPicker = (CountryCodePicker) findViewById(R.id.paisPicker);

        ic_close = getResources().getDrawable(R.drawable.ic_close);
        ic_back = getResources().getDrawable(R.drawable.ic_back);
        ic_close.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        ic_back.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editando) {
                    editando = false;
                    editar.setEnabled(true);
                    toolbar.setTitle("Perfil");
                    toolbar.getMenu().clear();
                    toolbar.setNavigationIcon(ic_back);
                    mostrarDatos(false);
                } else {
                    Toast.makeText(PerfilActivity.this, "Volver presionado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editar = (Button) findViewById(R.id.editarButton);

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editando = true;
                editar.setEnabled(false);
                toolbar.inflateMenu(R.menu.perfil_menu);
                toolbar.setTitle("Editar perfil");
                toolbar.setNavigationIcon(ic_close);
                mostrarDatos(true);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences credenciales = getSharedPreferences("credenciales", MODE_PRIVATE);

                String rutAux = credenciales.getString("rut", null) != null ? credenciales.getString("rut", null).replaceAll("[.-]", "") : null;
                //rutAux = rutAux.substring(0, rutAux.length() - 1);

                if(item.getItemId() == R.id.confirmar) {
                    editando = false;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (null != getCurrentFocus())
                        imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
                    Estudiante actualizado = new Estudiante();
                    actualizado.setTelefonoFijo(Long.valueOf(fijo.getText().toString()).longValue());
                    actualizado.setTelefonoMovil(Long.valueOf(movil.getText().toString()).longValue());
                    actualizarPerfil(rutAux, "Bearer " + credenciales.getString("token", null), actualizado);
                    //Toast.makeText(PerfilActivity.this, "Guardar datos presionado", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });


        mostrarDatos(false);

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

        Spinner dropdown = (Spinner) findViewById(R.id.sexoSpinner);
        //create a list of items for the spinner.
        String[] items = new String[]{"Masculino", "Femenino", "Prefiero no especificar"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);



    }

    private void mostrarDatos(Boolean activado) {
        SharedPreferences usuario = getSharedPreferences("usuario", MODE_PRIVATE);

        if (activado) {
            correoPersonal.setVisibility(VISIBLE);
            correoPersonal.setEnabled(true);
            findViewById(R.id.correoPersonal).setVisibility(VISIBLE);

            cFijo.setVisibility(VISIBLE);
            cFijo.setCcpClickable(true);
            cFijo.showArrow(true);
            fijo.setVisibility(VISIBLE);
            fijo.setEnabled(true);
            findViewById(R.id.telefonoFijo).setVisibility(VISIBLE);

            cMovil.setVisibility(VISIBLE);
            cMovil.setCcpClickable(true);
            cMovil.showArrow(true);
            movil.setVisibility(VISIBLE);
            movil.setEnabled(true);
            findViewById(R.id.telefonoMovil).setVisibility(VISIBLE);

            direccion.setVisibility(VISIBLE);
            direccion.setEnabled(true);
            findViewById(R.id.direccion).setVisibility(VISIBLE);

            paisPicker.setCcpClickable(true);
            paisPicker.showArrow(true);
        } else {
            correoPersonal.setVisibility(View.GONE);
            correoPersonal.setEnabled(false);
            findViewById(R.id.correoPersonal).setVisibility(View.GONE);

            cFijo.setVisibility(View.GONE);
            cFijo.setCcpClickable(false);
            cFijo.showArrow(false);
            fijo.setVisibility(View.GONE);
            fijo.setEnabled(false);
            findViewById(R.id.telefonoFijo).setVisibility(View.GONE);

            cMovil.setVisibility(View.GONE);
            cMovil.setCcpClickable(false);
            cMovil.showArrow(false);
            movil.setVisibility(View.GONE);
            movil.setEnabled(false);
            findViewById(R.id.telefonoMovil).setVisibility(View.GONE);

            direccion.setVisibility(View.GONE);
            direccion.setEnabled(false);
            findViewById(R.id.direccion).setVisibility(View.GONE);

            paisPicker.setCcpClickable(false);
            paisPicker.showArrow(false);
        }

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

        paisPicker.setVisibility(VISIBLE);
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
                        mostrarDatos(false);
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

    private void actualizarPerfil(final String rut, final String token, Estudiante usuario) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestClient restClient = retrofit.create(RestClient.class);
        Call<Estudiante> call = restClient.actualizarPerfil(rut, token,
                null,
                null,
                usuario.getTelefonoMovil(),
                usuario.getTelefonoFijo(),
                null,
                null,
                null,
                null);

        call.enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(Call<Estudiante> call, Response<Estudiante> response) {
                switch (response.code()) {
                    case 200:
                        Toast.makeText(PerfilActivity.this, "Se actualizaron los datos correctamente", Toast.LENGTH_SHORT).show();
                        mostrarDatos(false);
                        editar.setEnabled(true);
                        toolbar.setTitle("Perfil");
                        toolbar.getMenu().clear();
                        toolbar.setNavigationIcon(R.drawable.ic_back);
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

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
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
