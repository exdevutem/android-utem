package cl.inndev.utem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nombre = (TextView) headerView.findViewById(R.id.nombreText);
        TextView correo = (TextView) headerView.findViewById(R.id.correoText);
        SharedPreferences preferences = getSharedPreferences("alumno", Context.MODE_PRIVATE);
        nombre.setText(preferences.getString("nombre", "No existe"));
        correo.setText(preferences.getString("rut", "No configurado"));
        fm.beginTransaction().replace(R.id.mainlayout, new InicioFragment()).commit();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragment instanceof InicioFragment) {
                super.onBackPressed();
            } else {
                mostrarInicio();
            }
        }
    }

    private void mostrarInicio() {
        fragment = new InicioFragment();
        fm.beginTransaction().replace(R.id.mainlayout, fragment, fragment.getTag()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setActionBarTitle(String titulo){
        getSupportActionBar().setTitle(titulo);
    }

    Fragment fragment = null;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_inicio) {
            mostrarInicio();
        } else if (id == R.id.nav_perfil) {
            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_horario) {
            fm.beginTransaction().replace(R.id.mainlayout, new HorarioFragment()).commit();
        } else if (id == R.id.nav_carreras) {
            fm.beginTransaction().replace(R.id.mainlayout, new MallaFragment()).commit();
        } else if (id == R.id.nav_asignaturas) {
            fm.beginTransaction().replace(R.id.mainlayout, new AsignaturasFragment()).commit();
        } else if (id == R.id.nav_sesaes) {
            fm.beginTransaction().replace(R.id.mainlayout, new SesaesFragment()).commit();
        } else if (id == R.id.nav_salir) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_ajustes) {
            //Intent intent = new Intent(MainActivity.this, AjustesActivity.class);
            //startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
