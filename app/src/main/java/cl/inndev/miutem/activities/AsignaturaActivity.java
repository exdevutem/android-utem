package cl.inndev.miutem.activities;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.anupcowkur.reservoir.Reservoir;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Carrera;
import cl.inndev.miutem.fragments.AsignaturaFragment;
import cl.inndev.miutem.fragments.BitacoraFragment;
import cl.inndev.miutem.fragments.NotasFragment;

public class AsignaturaActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private long mContadorVida = 0;
    private ViewPager mViewPager;
    private Asignatura mAsignatura;
    public Integer mId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignatura);

        mId = getIntent().getIntExtra("ASIGNATURA_ID", -1);

        if (mId == -1 || getIntent().getIntExtra("ASIGNATURA_INDEX", -1) == -1) {
            finish();
        }

        mViewPager = findViewById(R.id.container);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        Toolbar toolbar = findViewById(R.id.toolbar);
        TabLayout tabLayout = findViewById(R.id.tabs);

        Type resultType = new TypeToken<List<Asignatura>>() {}.getType();
        try {
            List<Asignatura> asignaturas = Reservoir.get("asignaturas", resultType);
            mAsignatura = asignaturas.get(getIntent().getIntExtra("ASIGNATURA_INDEX", 0));
            toolbar.setTitle(mAsignatura.getNombre());
        } catch (IOException e) {
            e.printStackTrace();
        }

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (System.currentTimeMillis() - mContadorVida >= 60 * 1000 && mContadorVida != 0)
            finish();
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new NotasFragment();
                case 1:
                    return new BitacoraFragment();
                case 2:
                    return new AsignaturaFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
