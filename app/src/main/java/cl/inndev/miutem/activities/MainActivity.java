package cl.inndev.miutem.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirPutCallback;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import cl.inndev.miutem.classes.Carrera;
import cl.inndev.miutem.classes.PreferencesManager;
import cl.inndev.miutem.deserializers.CarrerasDeserializer;
import cl.inndev.miutem.fragments.AsignaturasFragment;
import cl.inndev.miutem.fragments.CarrerasFragment;
import cl.inndev.miutem.fragments.CertificadosFragment;
import cl.inndev.miutem.fragments.HorarioFragment;
import cl.inndev.miutem.fragments.InicioFragment;
import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.Estudiante;
import cl.inndev.miutem.fragments.MallaFragment;
import cl.inndev.miutem.interfaces.ApiUtem;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private NavigationView mNavigationView;
    private CircleImageView mImagePerfil;
    private TextView mTextNombre;
    private TextView mTextCorreo;
    private Toolbar mToolbar;
    private ImageButton mButtonMenu;
    private long mContadorVida = 0;
    private int mContadorAtras = 0;
    private Boolean mToogle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);

        View headerView = mNavigationView.getHeaderView(0);

        mTextNombre = headerView.findViewById(R.id.text_nombre);
        mTextCorreo = headerView.findViewById(R.id.text_correo);
        mImagePerfil = headerView.findViewById(R.id.image_perfil);
        mButtonMenu = headerView.findViewById(R.id.button_menu);

        mButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mToogle) {
                    mToogle = true;
                    mButtonMenu.setImageResource(R.drawable.ic_arrow_drop_up);
                    mNavigationView.getMenu().clear();
                    mNavigationView.inflateMenu(R.menu.activity_main_drawer_secondary);
                    mNavigationView.getMenu().getItem(0).setChecked(false);
                } else {
                    mToogle = false;
                    mNavigationView.getMenu().clear();
                    mButtonMenu.setImageResource(R.drawable.ic_arrow_drop_down);
                    mNavigationView.inflateMenu(R.menu.activity_main_drawer);
                }
            }
        });

        mNavigationView.setNavigationItemSelectedListener(this);

        mImagePerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PerfilActivity.class));
            }
        });
        getCarreras();

        mostrarInicio();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (System.currentTimeMillis() - mContadorVida >= 5 * 60 * 1000 && mContadorVida != 0) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        mContadorVida = 0;
        Estudiante datos = new Estudiante().convertirPreferencias(this);
        mTextNombre.setText(datos.getNombre());
        mTextCorreo.setText(datos.getCorreoUtem());
        new MainActivity.DownloadImageTask(mImagePerfil).execute(datos.getFotoUrl());
    }

    @Override
    public void onStop() {
        super.onStop();
        mContadorVida = System.currentTimeMillis();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragment instanceof InicioFragment) {
                if (mContadorAtras > 0) {
                    super.onBackPressed();
                    mContadorAtras = 0;
                } else {
                    Toast.makeText(this, "Presiona una vez más para salir", Toast.LENGTH_SHORT).show();
                    mContadorAtras++;
                }

            } else {
                mostrarInicio();
            }
        }
    }

    private void mostrarInicio() {
        fragment = new InicioFragment();
        mNavigationView.setCheckedItem(R.id.nav_inicio);
        mFragmentManager.beginTransaction().replace(R.id.mainlayout, fragment, fragment.getTag()).commit();
    }

    private void setMenuCounter(@IdRes int itemId, int count) {
        TextView counter = mNavigationView.getMenu().findItem(itemId).getActionView().findViewById(R.id.text_contador);
        if (count > 0) {
            counter.setText(String.valueOf(count));
            counter.setVisibility(View.VISIBLE);
        } else {
            counter.setText(null);
            counter.setVisibility(View.INVISIBLE);
        }
        if (count > 99)
            counter.setText("+99");
    }

    public void setActionBarTitle(String titulo){
        getSupportActionBar().setTitle(titulo);
    }

    Fragment fragment = null;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        mNavigationView.setCheckedItem(R.id.nav_inicio);
        setMenuCounter(id, 0);
        if (id == R.id.nav_inicio) {
            mostrarInicio();
        } else if (id == R.id.nav_perfil) {
            startActivity(new Intent(MainActivity.this, PerfilActivity.class));
            mostrarInicio();
        } else if (id == R.id.nav_aranceles) {
            Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_sesaes) {
            //mFragmentManager.beginTransaction().replace(R.id.mainlayout, new SesaesFragment()).commit();
            Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_alimentacion) {
            //mFragmentManager.beginTransaction().replace(R.id.mainlayout, new SesaesFragment()).commit();
            Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_horario) {
            mFragmentManager.beginTransaction().replace(R.id.mainlayout, new HorarioFragment()).commit();
        } else if (id == R.id.nav_certificados) {
            mFragmentManager.beginTransaction().replace(R.id.mainlayout, new CertificadosFragment()).commit();
            Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_asignaturas) {
            mFragmentManager.beginTransaction().replace(R.id.mainlayout, new AsignaturasFragment()).commit();
            //Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_carreras) {
            mFragmentManager.beginTransaction().replace(R.id.mainlayout, new CarrerasFragment()).commit();
            //startActivity(new Intent(MainActivity.this, CarreraActivity.class));
            //Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_titulos) {
            //mFragmentManager.beginTransaction().replace(R.id.mainlayout, new SesaesFragment()).commit();
            Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_configuracion) {
            //startActivity(new Intent(MainActivity.this, AjustesActivity.class));
            Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_ayuda) {
            Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_salir) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getCarreras() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(List.class, new CarrerasDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem client = retrofit.create(ApiUtem.class);

        Map<String, String> credenciales = Estudiante.getCredenciales(MainActivity.this);
        Call<List<Carrera>> call = client.getCarreras(credenciales.get("rut"), credenciales.get("token"));

        call.enqueue(new Callback<List<Carrera>>() {
            @Override
            public void onResponse(Call<List<Carrera>> call, Response<List<Carrera>> response) {
                switch (response.code()) {
                    case 200:
                        try {
                            Reservoir.put("carreras", response.body());
                        } catch (IOException e) {
                            Toast.makeText(MainActivity.this, "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Carrera>> call, Throwable t) {
                if (t instanceof TimeoutException) {
                    Toast.makeText(MainActivity.this, "¡Ups! Parece que la conexión está algo lenta. Por favor inténtalo nuevamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
                }
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
