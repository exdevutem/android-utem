package cl.inndev.miutem.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
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
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Carrera;
import cl.inndev.miutem.classes.Horario;
import cl.inndev.miutem.classes.Noticia;
import cl.inndev.miutem.fragments.AsignaturasFragment;
import cl.inndev.miutem.fragments.CarrerasFragment;
import cl.inndev.miutem.fragments.CertificadosFragment;
import cl.inndev.miutem.fragments.HorariosFragment;
import cl.inndev.miutem.fragments.InicioFragment;
import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.Estudiante;
import cl.inndev.miutem.interfaces.ApiNoticiasUtem;
import cl.inndev.miutem.interfaces.ApiUtem;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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
    private Boolean mCargoHorario = false;
    private Boolean mCargoAsignaturas = false;
    private Boolean mCargoCarreras = false;
    private Horario mHorario;
    private List<Carrera> mCarreras;
    private List<Asignatura> mAsignaturas;
    private Boolean mToogle = false;
    private AccountManager mAccountManager;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAccountManager = AccountManager.get(this);
        mAccountManager.getAuthToken(mAccountManager.getAccounts()[0],
                "Bearer", null, this,
                future -> {
                    try {
                        mToken = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                        BarCode usuario = new BarCode(Estudiante.class.getSimpleName(), "19649846");
                        try {
                            provideEstudianteStore()
                                    .get(usuario)
                                    .
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                        e.printStackTrace();
                    }
                }, null);


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

        mHorario = new Horario();
        mCarreras = new ArrayList<>();
        mAsignaturas = new ArrayList<>();

        mTextNombre = headerView.findViewById(R.id.text_nombre);
        mTextCorreo = headerView.findViewById(R.id.text_correo);
        mImagePerfil = headerView.findViewById(R.id.image_perfil);
        mButtonMenu = headerView.findViewById(R.id.button_menu);

        mButtonMenu.setOnClickListener(view -> {
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
        });

        mostrarInicio();

        mNavigationView.setNavigationItemSelectedListener(this);

        mImagePerfil.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PerfilActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (System.currentTimeMillis() - mContadorVida >= 5 * 60 * 1000 && mContadorVida != 0) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        mContadorVida = 0;

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
        mToolbar.setTitle(titulo);
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
            //mFragmentManager.beginTransaction().replace(R.id.mainlayout, new AlimentacionFragment()).commit();
            Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_horario) {
            if (mCargoHorario) {
                Intent intent = new Intent(MainActivity.this, HorarioActivity.class);
                intent.putExtra("HORARIO_INDEX", 0);
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
                //mFragmentManager.beginTransaction().replace(R.id.mainlayout, new HorariosFragment()).commit();
            }
        } else if (id == R.id.nav_certificados) {
            //mFragmentManager.beginTransaction().replace(R.id.mainlayout, new CertificadosFragment()).commit();
            Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_asignaturas) {
            if (mCargoAsignaturas && mAsignaturas.size() == 1) {
                startActivity(new Intent(MainActivity.this, AsignaturaActivity.class));
            } else {
                mFragmentManager.beginTransaction().replace(R.id.mainlayout, new AsignaturasFragment()).commit();
            }
        } else if (id == R.id.nav_carreras) {
            if (mCargoCarreras && mCarreras.size() == 1) {
                Intent intent = new Intent(MainActivity.this, CarreraActivity.class);
                intent.putExtra("CARRERA_ID", mCarreras.get(0).getmId());
                startActivity(intent);
            } else {
                mFragmentManager.beginTransaction().replace(R.id.mainlayout, new CarrerasFragment()).commit();
            }
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

    private Store<Estudiante, BarCode> provideEstudianteStore() throws IOException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        ApiUtem apiUtem = new Retrofit.Builder()
                .baseUrl(ApiUtem.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiUtem.class);

        return StoreBuilder.<Estudiante>barcode()
                .fetcher(barCode -> apiUtem.getPerfil(barCode.getKey(), "Bearer " + mToken))
                .open();
    }

}
