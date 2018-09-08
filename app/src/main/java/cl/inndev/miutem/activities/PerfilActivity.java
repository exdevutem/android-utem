package cl.inndev.miutem.activities;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import cl.inndev.miutem.adapters.CamposAdapter;
import cl.inndev.miutem.adapters.CarrerasAdapter;
import cl.inndev.miutem.models.Carrera;
import cl.inndev.miutem.interfaces.ApiUtem;
import cl.inndev.miutem.utils.StoreUtils;
import cl.inndev.miutem.views.NonScrollListView;
import cl.inndev.miutem.R;
import cl.inndev.miutem.models.Estudiante;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;

public class PerfilActivity extends AppCompatActivity {

    private SwipeRefreshLayout mSwipeContainer;
    private TextView mTextNombre;
    private TextView mTextTipo;
    private TextView mTextIngreso;
    private TextView mTextMatricula;
    private TextView mTextCarreras;
    private ImageView mImagePerfil;
    private FloatingActionButton mFabCambiarFoto;
    private NonScrollListView mListCampos;
    private NonScrollListView mListCarreras;
    private AccountManager mAccountManager;
    private AdapterView.OnItemClickListener mListener;
    private String mToken;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAccountManager = AccountManager.get(this);
        mSwipeContainer = findViewById(R.id.swipe_container);
        mTextNombre = findViewById(R.id.text_nombre);
        mTextTipo = findViewById(R.id.text_tipo);
        mTextIngreso = findViewById(R.id.text_ingreso);
        mTextMatricula = findViewById(R.id.text_matricula);
        mTextCarreras = findViewById(R.id.text_carreras);
        mImagePerfil = findViewById(R.id.image_perfil);
        mFabCambiarFoto = findViewById(R.id.fab_cambiar_foto);
        mListCampos = findViewById(R.id.list_campos);
        mListCarreras = findViewById(R.id.list_carreras);
        mListener = (adapter, v, position, arg3) -> {
            Carrera carrera = (Carrera) adapter.getItemAtPosition(position);
            Intent intent = new Intent(PerfilActivity.this, CarreraActivity.class);
            intent.putExtra("CARRERA_ID", carrera.getmId());
            intent.putExtra("CARRERA_INDEX", position);
            startActivity(intent);
        };

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAccountManager.getAuthToken(mAccountManager.getAccounts()[0],
                "Bearer", null, this,
                future -> {
                    try {
                        mToken = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                    } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                        e.printStackTrace();
                    }
                }, null);

        mSwipeContainer.setOnRefreshListener(() -> {
            refreshEstudiante(mToken, getRut());
            refreshCarreras(mToken, getRut());
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setCampos();
        setCarreras();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setCampos() {
        Estudiante usuario = getEstudiante(mToken, getRut());
        LinkedHashMap<Integer, String> lista = new LinkedHashMap<>();
        Picasso.get().load(usuario.getFotoUrl()).into(mImagePerfil);
        mTextNombre.setText(usuario.getNombre().getCompleto());
        mTextTipo.setText(usuario.getTipos().get(0));
        mTextMatricula.setText(usuario.getStringUltimaMatricula());
        mTextCarreras.setText(usuario.getStringCarrerasCursadas());
        mTextIngreso.setText(usuario.getStringAnioIngreso());

        if (usuario.getRut() != null)
            lista.put(0, "" + usuario.getRut());
        if (usuario.getCorreoUtem() != null)
            lista.put(1, usuario.getCorreoUtem());
        if (usuario.getPuntajePsu() != null)
            lista.put(3, usuario.getStringPuntajePsu());

        lista.put(2, usuario.getCorreoPersonal());
        lista.put(4, usuario.getStringSexoId());
        lista.put(5, usuario.getStringNacimiento());
        lista.put(6, usuario.getStringTelefonoMovil());
        lista.put(7, usuario.getStringTelefonoFijo());
        lista.put(8, usuario.getStringNacionalidad());
        // lista.put(9, null);
        // lista.put(10, null);
        // lista.put(11, null);
        lista.put(12, usuario.getStringDireccion());

        CamposAdapter adapter = new CamposAdapter(this, lista);
        adapter.setOnEditListener(nuevo -> actualizarPerfil(nuevo));

        mListCampos.setAdapter(adapter);

        mSwipeContainer.setVisibility(View.VISIBLE);
    }

    private Estudiante getEstudiante(String token, String rut) {
        BarCode usuario = new BarCode(Estudiante.class.getSimpleName(), rut);
        try {
            Store<Estudiante, BarCode> store = StoreUtils.provideEstudianteStore(PerfilActivity.this, token);
            return store.get(usuario)
                    .subscribeOn(Schedulers.io())
                    .blockingGet();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void refreshEstudiante(String token, String rut) {
        BarCode usuario = new BarCode(Estudiante.class.getSimpleName(), rut);
        try {
            Store<Estudiante, BarCode> store = StoreUtils.provideEstudianteStore(PerfilActivity.this, token);
            store.fetch(usuario)
                    .subscribeOn(Schedulers.io())
                    .subscribe(estudiante -> {
                        setCampos();
                        mSwipeContainer.setRefreshing(false);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Carrera> getCarreras(String token, String rut) {

        BarCode usuario = new BarCode(Estudiante.class.getSimpleName(), rut);
        try {
            Store<List<Carrera>, BarCode> store = StoreUtils.provideCarrerasStore(PerfilActivity.this, token);
            return store.get(usuario)
                    .subscribeOn(Schedulers.io())
                    .blockingGet();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void refreshCarreras(String token, String rut) {
        BarCode usuario = new BarCode(Estudiante.class.getSimpleName(), rut);
        try {
            Store<List<Carrera>, BarCode> store = StoreUtils.provideCarrerasStore(PerfilActivity.this, token);
            store.get(usuario)
                    .subscribeOn(Schedulers.io())
                    .subscribe(estudiante -> {
                        setCarreras();
                        mSwipeContainer.setRefreshing(false);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCarreras() {
        List<Carrera> carreras = getCarreras(mToken, getRut());
        mListCarreras.setAdapter(new CarrerasAdapter(this, carreras));
        mListCarreras.setOnItemClickListener(mListener);
    }

    public void actualizarPerfil(Estudiante usuario) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem restClient = retrofit.create(ApiUtem.class);

        Call<Estudiante> call = restClient.actualizarPerfil(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null),
                usuario.getCorreoPersonal(),
                usuario.getTelefonoMovil(),
                usuario.getTelefonoFijo(),
                usuario.getSexo() != null ? usuario.getSexo().getId() : null,
                null,
                usuario.getNacionalidad() != null ? usuario.getNacionalidad().getId() : null,
                usuario.getStringDireccion(),
                usuario.getNacimiento());

        call.enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(@NonNull Call<Estudiante> call, @NonNull Response<Estudiante> response) {
                switch (response.code()) {
                    case 200:
                        Toast.makeText(PerfilActivity.this, "Se actualizaron los datos correctamente", Toast.LENGTH_SHORT).show();
                        //getPerfil();
                        break;
                    default:
                        Toast.makeText(PerfilActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            @Override
            public void onFailure(@NonNull Call<Estudiante> call, @NonNull Throwable t) {
                t.printStackTrace();
                //getPerfil();
                Toast.makeText(PerfilActivity.this, "Parece que hubo un error al actualizar tus datos" + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRut() {
        return mAccountManager.getUserData(mAccountManager.getAccounts()[0], LoginActivity.PARAM_USER_RUT);
    }

    /*

    private void getCarreras() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(List.class, new CarrerasDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem client = retrofit.create(ApiUtem.class);

        Call<List<Carrera>> call = client.getCarreras(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null));

        call.enqueue(new Callback<List<Carrera>>() {
            @Override
            public void onResponse(@NonNull Call<List<Carrera>> call, @NonNull Response<List<Carrera>> response) {
                switch (response.code()) {
                    case 200:
                        try {
                            Reservoir.put("carreras", response.body());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setCarreras();
                        break;
                    default:
                        Toast.makeText(PerfilActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Carrera>> call, @NonNull Throwable t) {
                if (t instanceof TimeoutException) {
                    Toast.makeText(PerfilActivity.this,
                            "¡Ups! Parece que la conexión está algo lenta. " +
                                    "Por favor inténtalo nuevamente",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PerfilActivity.this,
                            "Error: " + t.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // STORE


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

     */
}