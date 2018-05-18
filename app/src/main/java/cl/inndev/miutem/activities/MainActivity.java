package cl.inndev.miutem.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.InputStream;

import cl.inndev.miutem.fragments.CarrerasFragment;
import cl.inndev.miutem.fragments.CertificadosFragment;
import cl.inndev.miutem.fragments.HorarioFragment;
import cl.inndev.miutem.fragments.InicioFragment;
import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.Estudiante;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private NavigationView mNavigationView;
    private CircleImageView mImagePerfil;
    private TextView mTextNombre;
    private TextView mTextCorreo;
    private Toolbar mToolbar;
    private ToggleButton mToggleDropdown;
    private long mContadorVida = 0;
    private int mContadorAtras = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        mToggleDropdown = headerView.findViewById(R.id.toggle_dropdown);

        mToggleDropdown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mNavigationView.getMenu().clear();
                if (b) {
                    mNavigationView.inflateMenu(R.menu.activity_main_drawer_secondary);
                    mNavigationView.getMenu().getItem(0).setChecked(false);
                } else
                    mNavigationView.inflateMenu(R.menu.activity_main_drawer);
            }
        });

        mNavigationView.setNavigationItemSelectedListener(this);

        mImagePerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PerfilActivity.class));
            }
        });
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
        mostrarInicio();
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
        } else if (id == R.id.nav_aranceles) {
            Toast.makeText(this, "Pronto estará disponible esta función", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_horario) {
            //mFragmentManager.beginTransaction().replace(R.id.mainlayout, new HorarioFragment()).commit();
            Toast.makeText(this, "Pronto estará disponible esta función", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_certificados) {
            //mFragmentManager.beginTransaction().replace(R.id.mainlayout, new CertificadosFragment()).commit();
            Toast.makeText(this, "Pronto estará disponible esta función", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_asignaturas) {
            //mFragmentManager.beginTransaction().replace(R.id.mainlayout, new AsignaturasFragment()).commit();
            Toast.makeText(this, "Pronto estará disponible esta función", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_carreras) {
            //mFragmentManager.beginTransaction().replace(R.id.mainlayout, new CarrerasFragment()).commit();
            Toast.makeText(this, "Pronto estará disponible esta función", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_sesaes) {
            //mFragmentManager.beginTransaction().replace(R.id.mainlayout, new SesaesFragment()).commit();
            Toast.makeText(this, "Pronto estará disponible esta función", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_configuracion) {
            //startActivity(new Intent(MainActivity.this, AjustesActivity.class));
            Toast.makeText(this, "Pronto estará disponible esta función", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_ayuda) {
            Toast.makeText(this, "Pronto estará disponible esta función", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_salir) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
