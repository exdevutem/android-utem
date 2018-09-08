package cl.inndev.miutem.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cl.inndev.miutem.models.AuthPreferences;
import cl.inndev.miutem.fragments.InicioFragment;
import cl.inndev.miutem.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private NavigationView mNavigationView;
    private ImageView mImagePerfil;
    // private TextView mTextNombre;
    // private TextView mTextCorreo;
    private Toolbar mToolbar;
    private ImageButton mButtonMenu;
    private int mContadorAtras = 0;
    // private List<Horario> mHorarios;
    // private List<Carrera> mCarreras;
    // private List<Asignatura> mAsignaturas;
    private Boolean mToogle = false;
    // private AccountManager mAccountManager;
    // private String mToken;
    private static final int AUTHORIZATION_CODE = 1993;
    private static final int ACCOUNT_CODE = 1601;
    private AuthPreferences mAuthPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mAccountManager = AccountManager.get(this);

        mAuthPreferences = new AuthPreferences(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);

        View headerView = mNavigationView.getHeaderView(0);

        /*
        mHorarios = new ArrayList<>();
        mCarreras = new ArrayList<>();
        mAsignaturas = new ArrayList<>();
        */

        // mTextNombre = headerView.findViewById(R.id.text_nombre);
        // mTextCorreo = headerView.findViewById(R.id.text_correo);
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


        //setDrawerHeader(getEstudiante(mToken, getRut()));
    }

    @Override
    public void onStop() {
        super.onStop();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        mNavigationView.setCheckedItem(R.id.nav_inicio);
        setMenuCounter(id, 0);
        if (id == R.id.nav_inicio) {
            mostrarInicio();
        } else if (id == R.id.nav_perfil) {
            startActivity(new Intent(MainActivity.this, PerfilActivity.class));
        } else if (id == R.id.nav_aranceles) {
            Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_sesaes) {
            //mFragmentManager.beginTransaction().replace(R.id.mainlayout, new SesaesFragment()).commit();
            Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_alimentacion) {
            //mFragmentManager.beginTransaction().replace(R.id.mainlayout, new AlimentacionFragment()).commit();
            Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_horario) {
            Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
            //getHorarios(mToken, getRut());
            /*
            if (mHorarios != null) {
                if (mHorarios.size() < 1) {
                    Toast.makeText(this, "No hay horarios asignados a su cuenta", Toast.LENGTH_SHORT).show();
                } else if (mHorarios.size() == 1) {
                    Intent intent = new Intent(MainActivity.this, HorarioActivity.class);
                    intent.putExtra("HORARIO_INDEX", 0);
                    startActivity(intent);
                } else {
                    mFragmentManager.beginTransaction().replace(R.id.mainlayout, new HorariosFragment()).commit();
                }
            } else {
                mFragmentManager.beginTransaction().replace(R.id.mainlayout, new HorariosFragment()).commit();
            }
            */

        } else if (id == R.id.nav_certificados) {
            //mFragmentManager.beginTransaction().replace(R.id.mainlayout, new CertificadosFragment()).commit();
            Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_asignaturas) {
            Toast.makeText(this, "HOLA?", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_carreras) {
            Toast.makeText(this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
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

    /*
    private String getRut() {
        return mAccountManager.getUserData(mAccountManager.getAccounts()[0], LoginActivity.PARAM_USER_RUT);
    }

    private Estudiante getEstudiante(String token, String rut) {
        BarCode usuario = new BarCode(Estudiante.class.getSimpleName(), rut);
        try {
            Store<Estudiante, BarCode> store = StoreUtils.provideEstudianteStore(MainActivity.this, token);
            return store.get(usuario)
                    .subscribeOn(Schedulers.io())
                    .blockingGet();
        } catch (IOException e) {
            Toast.makeText(this, "ERROR OBTENIENDO ESTUDIANTE", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return null;
        }
    }


    private Disposable getCarreras(String token, String rut) {
        BarCode carrerasBarCode = new BarCode(Carrera.class.getSimpleName() + "sasdsadsa", rut);
        try {
            return StoreUtils.provideCarrerasStore(this, token)
                    .get(carrerasBarCode)
                    .subscribeOn(Schedulers.io())
                    .subscribe(carreras -> {
                        mCarreras = carreras;
                    });
        } catch (IOException e) {
            Toast.makeText(this, "ERROR OBTENIENDO CARRERAS", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }


    private Disposable getHorarios(String token, String rut) {
        BarCode horariosBarCode = new BarCode(Horario.class.getSimpleName() + "s", rut);
        try {
            return StoreUtils.provideHorariosStore(this, token)
                    .get(horariosBarCode)
                    .subscribeOn(Schedulers.io())
                    .subscribe(horarios -> {
                        mHorarios = horarios;
                    });
        } catch (IOException e) {
            Toast.makeText(this, "ERROR OBTENIENDO HORARIOS", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }

    private void setDrawerHeader(Estudiante usuario) {
        Picasso.get().load(usuario.getFotoUrl()).into(mImagePerfil);
        mTextNombre.setText(usuario.getNombre().getNombre());
        mTextCorreo.setText(usuario.getCorreoUtem());
    }

    private void doCoolAuthenticatedStuff() {
        Log.d("POLLO", "hola");
        //getCarreras(mAuthPreferences.getToken(), getRut());
        //getEstudiante(mAuthPreferences.getToken(), getRut());
        //getHorarios(mAuthPreferences.getToken(), getRut());
    }

    private void chooseAccount() {
        // use https://github.com/frakbot/Android-AccountChooser for
        // compatibility with older devices
        Intent intent = AccountManager.newChooseAccountIntent(null, null,
                new String[] { AuthPreferences.ACCOUNT_TYPE }, false, null, null, null, null);
        startActivityForResult(intent, ACCOUNT_CODE);
    }

    @SuppressLint("StaticFieldLeak")
    private void requestToken() {
        Account account = mAccountManager.getAccountsByType(AuthPreferences.ACCOUNT_TYPE)[0];

        String user = mAuthPreferences.getUser();
        for (Account account : mAccountManager.getAccountsByType(AuthPreferences.ACCOUNT_TYPE)) {
            if (account.name.equals(user)) {
                userAccount = account;
                break;
            }
        }

        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                mAccountManager.getAuthToken(account, "Bearer", null, MainActivity.this,
                        new OnTokenAcquired(), null);
                return null;
            }

        }.execute();

    }

    private void invalidateToken() {
        AccountManager accountManager = AccountManager.get(this);
        accountManager.invalidateAuthToken(AuthPreferences.ACCOUNT_TYPE, mAuthPreferences.getToken());
        mAuthPreferences.setToken(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == AUTHORIZATION_CODE) {
                requestToken();
            } else if (requestCode == ACCOUNT_CODE) {
                String accountName = data
                        .getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                mAuthPreferences.setUser(accountName);

                // invalidate old tokens which might be cached. we want a fresh
                // one, which is guaranteed to work
                invalidateToken();

                requestToken();
            }
        }
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {

        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                Bundle bundle = result.getResult();
                Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (launch != null) {
                    startActivityForResult(launch, AUTHORIZATION_CODE);
                } else {
                    String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    mAuthPreferences.setToken(token);
                    Toast.makeText(MainActivity.this, mAuthPreferences.getToken(), Toast.LENGTH_SHORT).show();

                    doCoolAuthenticatedStuff();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    */

}
