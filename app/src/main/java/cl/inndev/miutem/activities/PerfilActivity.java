package cl.inndev.miutem.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import cl.inndev.miutem.adapters.CamposAdapter;
import cl.inndev.miutem.dialogs.ErrorDialog;
import cl.inndev.miutem.interfaces.ApiUtem;
import cl.inndev.miutem.views.NonScrollListView;
import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.Estudiante;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;

public class PerfilActivity extends AppCompatActivity {

    private long mContadorVida = 0;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeContainer;
    private TextView mTextNombre;
    private TextView mTextTipo;
    private TextView mTextIngreso;
    private TextView mTextMatricula;
    private TextView mTextCarreras;
    private CircleImageView mImagePerfil;
    private FloatingActionButton mFabCambiarFoto;
    private NonScrollListView mListCampos;
    private ErrorDialog mDialogError;

    private FirebaseAnalytics mFirebaseAnalytics;
    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mSwipeContainer = findViewById(R.id.swipe_container);
        mTextNombre = findViewById(R.id.text_nombre);
        mTextTipo = findViewById(R.id.text_tipo);
        mTextIngreso = findViewById(R.id.text_ingreso);
        mTextMatricula = findViewById(R.id.text_matricula);
        mTextCarreras = findViewById(R.id.text_carreras);
        mImagePerfil = findViewById(R.id.image_perfil);
        mFabCambiarFoto = findViewById(R.id.fab_cambiar_foto);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mListCampos = findViewById(R.id.list_campos);
        mToolbar = findViewById(R.id.toolbar);
        mDialogError = new ErrorDialog(this);
        mDialogError.show();

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                obtenerPerfil();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (System.currentTimeMillis() - mContadorVida >= 60 * 1000 && mContadorVida != 0)
            finish();
        else {
            mShimmerViewContainer.startShimmer();
            obtenerPerfil();
        }
        mContadorVida = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mContadorVida = System.currentTimeMillis();
        mShimmerViewContainer.stopShimmer();
    }

    // Cuando se presiona la flecha del toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void mostrarDatos() {
        String[] claves = getResources().getStringArray(R.array.etiquetas_perfil);
        Map<String, String> lista = new LinkedHashMap();
        Estudiante valores = new Estudiante().convertirPreferencias(this);

        /*
        mDialogSexo.setSingleChoiceItems(R.array.sexos,
                new Estudiante()
                        .convertirPreferencias(PerfilActivity.this)
                        .getSexo().getSexoCodigo(), null);

        new DownloadImageTask(mImagePerfil).execute(valores.getFotoUrl());
        */

        mTextNombre.setText(valores.getNombre());
        mTextTipo.setText(valores.getTipo());
        mTextMatricula.setText(valores.getStringUltimaMatricula());
        mTextCarreras.setText(valores.getStringCarrerasCursadas());
        mTextIngreso.setText(valores.getStringAnioIngreso());

        if (valores.getRut() != null)
            lista.put(claves[0], valores.getRut());
        if (valores.getCorreoUtem() != null)
            lista.put(claves[1], valores.getCorreoUtem());
        if (valores.getPuntajePsu() != null)
            lista.put(claves[3], valores.getStringPuntajePsu());

        lista.put(claves[2], valores.getCorreoPersonal());
        lista.put(claves[4], valores.getSexo().getSexoTexto());
        lista.put(claves[5], valores.getStringEdad());
        lista.put(claves[6], valores.getStringTelefonoMovil());
        lista.put(claves[7], valores.getStringTelefonoFijo());
        lista.put(claves[8], null);
        // lista.put(claves[9], null);
        // lista.put(claves[10], null);
        // lista.put(claves[11], null);
        lista.put(claves[12], valores.getDireccion());

        mListCampos.setAdapter(new CamposAdapter(this, lista));
        mSwipeContainer.setVisibility(View.VISIBLE);

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
            public void onResponse(@NonNull Call<Estudiante> call, @NonNull Response<Estudiante> response) {
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
                mShimmerViewContainer.stopShimmer();
            }

            @Override
            public void onFailure(@NonNull Call<Estudiante> call, @NonNull Throwable t) {
                mSwipeContainer.setRefreshing(false);
                mShimmerViewContainer.stopShimmer();
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
            public void onResponse(@NonNull Call<Estudiante> call, @NonNull Response<Estudiante> response) {
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
            public void onFailure(@NonNull Call<Estudiante> call, @NonNull Throwable t) {
                Toast.makeText(PerfilActivity.this, "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*




    private int CAMERA_REQUEST_CODE = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);

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