package cl.inndev.utem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.utem.ApiUtem.BASE_URL;

public class PerfilActivity extends AppCompatActivity {

    private Toolbar toolbarPerfil;

    private NonScrollListView listDatos;

    private AlertDialog.Builder alertCancelar;
    private AlertDialog.Builder dialogSexo;
    private Dialog dialogCustom;
    private AlertDialog.Builder alertComuna;

    private SwipeRefreshLayout swipeContainer;
    private TextView textNombre;
    private TextView textTipo;
    private TextView textIngreso;
    private TextView textMatricula;
    private TextView textCarreras;
    private CircleImageView imagePerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        toolbarPerfil = findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbarPerfil);

        textNombre = findViewById(R.id.text_nombre);
        textTipo = findViewById(R.id.text_tipo);
        textIngreso = findViewById(R.id.text_ingreso);
        textMatricula = findViewById(R.id.text_matricula);
        textCarreras = findViewById(R.id.text_carreras);
        imagePerfil = findViewById(R.id.image_perfil);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listDatos = findViewById(R.id.list_datos);

        dialogSexo = new AlertDialog.Builder(PerfilActivity.this);

        dialogCustom = new Dialog(this, R.style.AppTheme);
        dialogCustom.setCancelable(true);
        dialogCustom.setContentView(R.layout.dialog_edit);
        dialogCustom.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        dialogSexo.setTitle("Seleccione su sexo");
        dialogSexo.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Estudiante nuevo = new Estudiante();
                nuevo.setSexo(((AlertDialog) dialog).getListView().getCheckedItemPosition());
                actualizarPerfil(nuevo);
                dialog.dismiss();
            }
        }).setNegativeButton("Cancelar", null);

        mostrarDatos();

        swipeContainer = findViewById(R.id.swipe_container);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                obtenerPerfil();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (System.currentTimeMillis() - contadorTiempo >= 30000 && contadorTiempo != 0) {
            startActivity(new Intent(PerfilActivity.this, LoginActivity.class));
            finish();
        }
        contadorTiempo = 0;*/
    }

    @Override
    public void onStop() {
        super.onStop();
        //contadorTiempo = System.currentTimeMillis();
    }


    private void mostrarDatos() {
        String[] claves = getResources().getStringArray(R.array.etiquetas_perfil);
        Map<String, String> lista = new LinkedHashMap();
        Estudiante valores = new Estudiante().convertirPreferencias(this);

        dialogSexo.setSingleChoiceItems(R.array.sexos,
                new Estudiante().convertirPreferencias(PerfilActivity.this).getSexo().sexoCodigo,
                null);

        textNombre.setText(valores.getNombre());
        textTipo.setText(valores.getTipo());

        new DownloadImageTask(imagePerfil).execute(valores.getFotoUrl());

        if (valores.getAnioIngreso() != null)
            textIngreso.setText(valores.getAnioIngreso().toString());
        if (valores.getUltimaMatricula() != null)
            textMatricula.setText(valores.getUltimaMatricula().toString());
        if (valores.getCarrerasCursadas() != null)
            textCarreras.setText(valores.getCarrerasCursadas().toString());
        if (valores.getRut() != null)
            lista.put(claves[0], valores.getRut());
        if (valores.getCorreoUtem() != null)
            lista.put(claves[1], valores.getCorreoUtem());
        if (valores.getCorreoPersonal() != null)
            lista.put(claves[2], valores.getCorreoPersonal());
        if (valores.getPuntajePsu() != null)
            lista.put(claves[3], valores.getPuntajePsu().toString());
        if (valores.getSexo().getSexoTexto() != null)
            lista.put(claves[4], valores.getSexo().getSexoTexto());
        if (valores.getEdad() != null)
            lista.put(claves[5], valores.getEdad().toString());
        if (valores.getTelefonoMovil() != null)
            lista.put(claves[6], valores.getTelefonoMovil().toString());
        if (valores.getTelefonoFijo() != null)
            lista.put(claves[7], valores.getTelefonoFijo().toString());
        if (valores.getDireccion() != null)
            lista.put(claves[8], valores.getDireccion());

        listDatos.setAdapter(new DatosAdapter(this, lista));

    }

    private void obtenerPerfil() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem restClient = retrofit.create(ApiUtem.class);

        Map<String, String> credenciales = Estudiante.getCredenciales(PerfilActivity.this);

        Call<Estudiante> call = restClient.obtenerPerfil(credenciales.get("rut"), credenciales.get("token"));

        call.enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(Call<Estudiante> call, Response<Estudiante> response) {
                switch (response.code()) {
                    case 200:
                        Estudiante usuario = response.body();
                        usuario.guardarDatos(PerfilActivity.this);
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
                if (t instanceof TimeoutException) {
                    Toast.makeText(PerfilActivity.this, "¡Ups! Parece que la conexión está algo lenta. Por favor inténtalo nuevamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PerfilActivity.this, "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void actualizarPerfil(Estudiante usuario) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem restClient = retrofit.create(ApiUtem.class);

        Map<String, String> credenciales = Estudiante.getCredenciales(PerfilActivity.this);

        Call<Estudiante> call = restClient.actualizarPerfil(credenciales.get("rut"), credenciales.get("token"),
                usuario.getCorreoPersonal(), usuario.getTelefonoMovil(),
                usuario.getTelefonoFijo(),
                usuario.getSexo() != null ? usuario.getSexo().getSexoCodigo() : null,
                null, null,
                usuario.getDireccion());

        call.enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(Call<Estudiante> call, Response<Estudiante> response) {
                switch (response.code()) {
                    case 200:
                        Toast.makeText(PerfilActivity.this, "Se actualizaron los datos correctamente", Toast.LENGTH_SHORT).show();
                        obtenerPerfil();
                        break;
                    default:
                        Toast.makeText(PerfilActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            @Override
            public void onFailure(Call<Estudiante> call, Throwable t) {
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

    private class DatosAdapter extends BaseAdapter {

        private Context context;
        private Map<String, String> listItems;

        public DatosAdapter(Context context, Map<String, String> listItems) {
            this.context = context;
            this.listItems = listItems;
        }

        @Override
        public int getCount() {
            return listItems.size();
        }

        @Override
        public Map.Entry<String, String> getItem(int i) {
            int c = 0;
            for (Map.Entry<String, String> entry : listItems.entrySet()) {
                if (c == i) {
                    return entry;
                }
                 c++;
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final Map.Entry<String, String> item = this.getItem(i);
            final String[] claves = getResources().getStringArray(R.array.etiquetas_perfil);
            view = LayoutInflater.from(context).inflate(R.layout.activity_perfil_listitem, null);
            TextView textEtiqueta = view.findViewById(R.id.text_etiqueta);
            TextView textValor = view.findViewById(R.id.text_valor);
            ImageButton buttonEditar = view.findViewById(R.id.button_editar);

            buttonEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText editInfo = dialogCustom.getWindow().getDecorView().findViewById(R.id.edit_info);

                    if (item.getKey().trim().equals(claves[2])) {
                        editInfo.setText(item.getValue().trim());
                        editInfo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        Button buttonGuardar = dialogCustom.getWindow().getDecorView().findViewById(R.id.button_guardar);
                        buttonGuardar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Estudiante nuevo = new Estudiante();
                                nuevo.setCorreoPersonal(editInfo.getText().toString());
                                actualizarPerfil(nuevo);
                                dialogCustom.dismiss();
                            }
                        });
                        dialogCustom.show();
                    }
                    if (item.getKey().trim().equals(claves[4]))
                        dialogSexo.show();
                /*if (item.getKey().trim().equals(claves[5])) {
                    dialogSexo.show();
                }*/
                    if (item.getKey().trim().equals(claves[6])) {
                        editInfo.setText(item.getValue().trim());
                        editInfo.setInputType(InputType.TYPE_CLASS_PHONE);
                        Button buttonGuardar = dialogCustom.getWindow().getDecorView().findViewById(R.id.button_guardar);
                        buttonGuardar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Estudiante nuevo = new Estudiante();
                                nuevo.setTelefonoMovil(Long.parseLong(editInfo.getText().toString()));
                                actualizarPerfil(nuevo);
                                dialogCustom.dismiss();
                            }
                        });
                        dialogCustom.show();
                    }
                    if (item.getKey().trim().equals(claves[7])) {
                        editInfo.setText(item.getValue().trim());
                        editInfo.setInputType(InputType.TYPE_CLASS_PHONE);
                        Button buttonGuardar = dialogCustom.getWindow().getDecorView().findViewById(R.id.button_guardar);
                        buttonGuardar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Estudiante nuevo = new Estudiante();
                                nuevo.setTelefonoFijo(Long.parseLong(editInfo.getText().toString()));
                                actualizarPerfil(nuevo);
                                dialogCustom.dismiss();
                            }
                        });
                        dialogCustom.show();
                    }
                    if (item.getKey().trim().equals(claves[8])) {
                        editInfo.setText(item.getValue().trim());
                        editInfo.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
                        Button buttonGuardar = dialogCustom.getWindow().getDecorView().findViewById(R.id.button_guardar);
                        buttonGuardar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Estudiante nuevo = new Estudiante();
                                nuevo.setDireccion(editInfo.getText().toString());
                                actualizarPerfil(nuevo);
                                dialogCustom.dismiss();
                            }
                        });
                        dialogCustom.show();
                    }

                    Button buttonCancelar = dialogCustom.getWindow().getDecorView().findViewById(R.id.button_cancelar);
                    buttonCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogCustom.dismiss();
                        }
                    });
                }
            });


            textEtiqueta.setText(item.getKey());
            textValor.setText(item.getValue());
            return view;
        }
    }
}
