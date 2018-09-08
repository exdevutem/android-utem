package cl.inndev.miutem.activities;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import cl.inndev.miutem.R;
import cl.inndev.miutem.models.Carrera;
import cl.inndev.miutem.fragments.BoletinFragment;
import cl.inndev.miutem.fragments.CarreraFragment;
import cl.inndev.miutem.fragments.MallaFragment;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.utils.Log;

public class CarreraActivity extends AppCompatActivity  {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private long mContadorVida = 0;
    private ViewPager mViewPager;
    public Integer mId;
    public Carrera mCarrera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrera);
        FlexibleAdapter.enableLogs(Log.Level.DEBUG);

        mId = getIntent().getIntExtra("CARRERA_ID", -1);

        if (mId == -1) {
            finish();
        }

        mViewPager = findViewById(R.id.container);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        Toolbar toolbar = findViewById(R.id.toolbar);
        TabLayout tabLayout = findViewById(R.id.tabs);

        Type resultType = new TypeToken<List<Carrera>>() {}.getType();
        /*
        try {
            List<Carrera> carreras = Reservoir.get("carreras", resultType);
            mCarrera = carreras.get(getIntent().getIntExtra("CARRERA_INDEX", 0));
            toolbar.setTitle(mCarrera.getNombre());
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

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
            Bundle bundle = new Bundle();
            bundle.putInt("id", mId);
            switch (position) {
                case 0:
                    CarreraFragment f0 = new CarreraFragment();
                    f0.setArguments(bundle);
                    return f0;
                case 1:
                    MallaFragment f1 = new MallaFragment();
                    f1.setArguments(bundle);
                    return f1;
                case 2:
                    BoletinFragment f2 = new BoletinFragment();
                    f2.setArguments(bundle);
                    return f2;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
