package cl.inndev.miutem.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cl.inndev.miutem.interfaces.ApiUtem;
import cl.inndev.miutem.views.NonScrollListView;
import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.Estudiante;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;

public class PerfilActivity extends AppCompatActivity {

    private Toolbar mToolbarPerfil;
    private NonScrollListView mListDatos;
    private AlertDialog.Builder mDialogSexo;
    private Dialog mDialogEditar;
    private SwipeRefreshLayout mSwipeContainer;
    private TextView mTextNombre;
    private TextView mTextTipo;
    private TextView mTextIngreso;
    private TextView mTextMatricula;
    private TextView mTextCarreras;
    private CircleImageView mImagePerfil;
    private FloatingActionButton mFabCambiarFoto;
    private DatePickerDialog mDialogFecha;
    private Calendar mFechaActual;
    private DatePickerDialog.OnDateSetListener dateListener;
    private long mContadorVida = 0;


    private int CAMERA_REQUEST_CODE = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mToolbarPerfil = findViewById(R.id.toolbar_perfil);
        mSwipeContainer = findViewById(R.id.swipe_container);
        mTextNombre = findViewById(R.id.text_nombre);
        mTextTipo = findViewById(R.id.text_tipo);
        mTextIngreso = findViewById(R.id.text_ingreso);
        mTextMatricula = findViewById(R.id.text_matricula);
        mTextCarreras = findViewById(R.id.text_carreras);
        mImagePerfil = findViewById(R.id.image_perfil);
        mFabCambiarFoto = findViewById(R.id.fab_cambiar_foto);
        mDialogSexo = new AlertDialog.Builder(this);
        mDialogEditar = new Dialog(this, R.style.AppTheme);

        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Estudiante nuevo = new Estudiante();
                nuevo.setNacimiento(day + "-" + month + 1 + "-" + year);
                actualizarPerfil(nuevo);
            }
        };

        mFechaActual = Calendar.getInstance();

        setSupportActionBar(mToolbarPerfil);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListDatos = findViewById(R.id.list_datos);

        mDialogEditar.setCancelable(true);
        mDialogEditar.setContentView(R.layout.dialog_perfil_editar);
        mDialogEditar
                .getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mDialogSexo.setTitle("Selecciona tu sexo");
        mDialogSexo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Estudiante nuevo = new Estudiante();
                nuevo.setSexo(((AlertDialog) dialog).getListView().getCheckedItemPosition());
                actualizarPerfil(nuevo);
                dialog.dismiss();
            }
        }).setNegativeButton("Cancelar", null);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                obtenerPerfil();
            }
        });

        mFabCambiarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(PerfilActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(PerfilActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Mostrar explicación de por qué se necesitan permisos
                    }

                    ActivityCompat.requestPermissions(PerfilActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    return;
                }

                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheDir());

                // Filesystem
                final Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                // Chooser of filesystem options.
                final Intent chooserIntent = Intent.createChooser(galleryIntent, "Seleccona una opción");

                // Add the camera options.
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, captureIntent);

                startActivityForResult(chooserIntent, CAMERA_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mostrarDatos();
        if (System.currentTimeMillis() - mContadorVida >= 60 * 1000 && mContadorVida != 0)
            finish();
        else
            obtenerPerfil();
        mContadorVida = 0;
    }

    @Override
    public void onStop() {
        super.onStop();
        mContadorVida = System.currentTimeMillis();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UCrop.RESULT_ERROR) {
            Toast.makeText(PerfilActivity.this, "Ocurrió un error al cortar la imagen", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == UCrop.REQUEST_CROP) {
            final Uri imgUri = UCrop.getOutput(data);
            Estudiante actual = new Estudiante().convertirPreferencias(this);
            String rut = new Estudiante.Rut(actual.getRut()).getCuerpo().toString();
            Toast.makeText(PerfilActivity.this, "Subiendo...", Toast.LENGTH_SHORT).show();

            cambiarFoto(imgUri, rut);
            return;
        }

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            final Uri sourceUri = data.getData();
            if (sourceUri != null) {
                File tempCropped = new File(getCacheDir(), "tempPerfil.png");
                Uri destinationUri = Uri.fromFile(tempCropped);
                UCrop uCrop = UCrop.of(sourceUri, destinationUri);
                uCrop = configurarUcrop(uCrop);
                uCrop.start(PerfilActivity.this);
            } else {
                Toast.makeText(this, "Ocurrió un error al cargar la foto", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public UCrop configurarUcrop(UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarTitle("Editar imagen");
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setActiveWidgetColor(getResources().getColor(R.color.colorPrimary));
        options.withAspectRatio(1, 1);
        options.withMaxResultSize(500, 500);
        options.setCircleDimmedLayer(true);
        return uCrop.withOptions(options);
    }

    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(PerfilActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PerfilActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                    .setType("image/*")
                    .addCategory(Intent.CATEGORY_OPENABLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }

            startActivityForResult(Intent.createChooser(intent, "Selecciona una foto"), CAMERA_REQUEST_CODE);
        }
    }

    private void mostrarDatos() {
        String[] claves = getResources().getStringArray(R.array.etiquetas_perfil);
        Map<String, String> lista = new LinkedHashMap();
        Estudiante valores = new Estudiante().convertirPreferencias(this);

        mDialogSexo.setSingleChoiceItems(R.array.sexos,
                new Estudiante()
                        .convertirPreferencias(PerfilActivity.this)
                        .getSexo().getSexoCodigo(), null);

        mTextNombre.setText(valores.getNombre());
        mTextTipo.setText(valores.getTipo());

        new DownloadImageTask(mImagePerfil).execute(valores.getFotoUrl());

        if (valores.getAnioIngreso() != null)
            mTextIngreso.setText(valores.getAnioIngreso().toString());
        if (valores.getUltimaMatricula() != null)
            mTextMatricula.setText(valores.getUltimaMatricula().toString());
        if (valores.getCarrerasCursadas() != null)
            mTextCarreras.setText(valores.getCarrerasCursadas().toString());
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
        else
            lista.put(claves[5], null);
        if (valores.getTelefonoMovil() != null)
            lista.put(claves[6], valores.getTelefonoMovil().toString());
        else
            lista.put(claves[6], null);
        if (valores.getTelefonoFijo() != null)
            lista.put(claves[7], valores.getTelefonoFijo().toString());
        else
            lista.put(claves[7], null);
        /*lista.put(claves[8], null);
        lista.put(claves[9], null);
        lista.put(claves[10], null);
        lista.put(claves[11], null);*/
        if (valores.getDireccion() != null)
            lista.put(claves[12], valores.getDireccion());
        else
            lista.put(claves[12], null);

        mListDatos.setAdapter(new DatosAdapter(this, lista));

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

        Call<Estudiante> call = restClient.getPerfil(credenciales.get("rut"), credenciales.get("token"));

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
                mSwipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Estudiante> call, Throwable t) {
                mSwipeContainer.setRefreshing(false);
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

    private void cambiarFoto(Uri imagen, String rut) {
        File archivo = new File(imagen.getPath());

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sgu.utem.cl/")
                .client(client)
                .build();

        ApiUtem restClient = retrofit.create(ApiUtem.class);
        Call<ResponseBody> call = restClient.cambiarFoto(
                RequestBody.create(MediaType.parse("multipart/form-data"), rut),
                RequestBody.create(MediaType.parse("multipart/form-data"), "1"),
                RequestBody.create(MediaType.parse("image/*"), archivo));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                switch (response.code()) {
                    case 200:
                        mostrarDatos();
                        Toast.makeText(PerfilActivity.this, "Se cambió la foto correctamente", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(PerfilActivity.this, "No se pudo: cayó en el default" , Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(PerfilActivity.this, "¡Ups! Parece que la conexión está algo lenta. Por favor inténtalo nuevamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PerfilActivity.this, "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*

    private Chile obtenerDivisionPolitica() {
        final Chile lugares = new Chile();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiDpa restClient = retrofit.create(ApiDpa.class);

        Call<ArrayList<Chile.Comuna>> call = restClient.getComunas(null);

        call.enqueue(new Callback<ArrayList<Chile.Comuna>>() {
            @Override
            public void onResponse(Call<ArrayList<Chile.Comuna>> call, Response<ArrayList<Chile.Comuna>> response) {
                switch (response.code()) {
                    case 200:
                        lugares.setComunas(response.body());
                        break;
                    default:
                        return null;
                        Toast.makeText(PerfilActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Chile.Comuna>> call, Throwable t) {
                return null;
            }
        });
    }

    */

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

            if (item.getKey().trim().equals(claves[0]) || item.getKey().trim().equals(claves[1]) || item.getKey().trim().equals(claves[3])) {
                buttonEditar.setVisibility(View.GONE);
                view.setClickable(false);
            } else {
                buttonEditar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final EditText editInfo = mDialogEditar.getWindow().getDecorView().findViewById(R.id.edit_info);
                        final Button buttonGuardar = mDialogEditar.getWindow().getDecorView().findViewById(R.id.button_guardar);

                        if (item.getKey().trim().equals(claves[2]) ||
                                item.getKey().trim().equals(claves[6]) ||
                                item.getKey().trim().equals(claves[7]) ||
                                item.getKey().trim().equals(claves[12])) {
                            editInfo.setText(item.getValue().trim());
                            editInfo.setSelectAllOnFocus(true);
                        }
                        if (item.getKey().trim().equals(claves[2])) {
                            editInfo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            buttonGuardar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Estudiante nuevo = new Estudiante();
                                    nuevo.setCorreoPersonal(editInfo.getText().toString());
                                    actualizarPerfil(nuevo);
                                    mDialogEditar.dismiss();
                                }
                            });
                            mDialogEditar.show();
                        } else if (item.getKey().trim().equals(claves[4])) {
                            mDialogSexo.show();
                        } else if (item.getKey().trim().equals(claves[5])) {
                            if (item.getValue() == null) {
                                mDialogFecha = new DatePickerDialog(PerfilActivity.this, dateListener,
                                        mFechaActual.get(Calendar.YEAR) - 17, 0, 1);
                            } else {
                                mDialogFecha = new DatePickerDialog(PerfilActivity.this, dateListener,
                                        mFechaActual.get(Calendar.YEAR) - Integer.valueOf(item.getValue()), 0, 1);
                            }
                            mDialogFecha.setTitle("Selecciona tu fecha de nacimiento");

                            mDialogFecha.show();
                        } else if (item.getKey().trim().equals(claves[6])) {
                            editInfo.setInputType(InputType.TYPE_CLASS_PHONE);
                            buttonGuardar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Estudiante nuevo = new Estudiante();
                                    nuevo.setTelefonoMovil(Long.parseLong(editInfo.getText().toString()));
                                    actualizarPerfil(nuevo);
                                    mDialogEditar.dismiss();
                                }
                            });
                            mDialogEditar.show();
                        } else if (item.getKey().trim().equals(claves[7])) {
                            editInfo.setInputType(InputType.TYPE_CLASS_PHONE);
                            buttonGuardar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Estudiante nuevo = new Estudiante();
                                    nuevo.setTelefonoFijo(Long.parseLong(editInfo.getText().toString()));
                                    actualizarPerfil(nuevo);
                                    mDialogEditar.dismiss();
                                }
                            });
                            mDialogEditar.show();
                        } else if (item.getKey().trim().equals(claves[12])) {
                            editInfo.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
                            buttonGuardar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Estudiante nuevo = new Estudiante();
                                    nuevo.setDireccion(editInfo.getText().toString());
                                    actualizarPerfil(nuevo);
                                    mDialogEditar.dismiss();
                                }
                            });
                            mDialogEditar.show();
                        }

                        Button buttonCancelar = mDialogEditar.getWindow().getDecorView().findViewById(R.id.button_cancelar);
                        buttonCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mDialogEditar.dismiss();
                            }
                        });
                    }
                });
            }

            textEtiqueta.setText(item.getKey());
            textValor.setText(item.getValue());
            return view;
        }
    }
}
