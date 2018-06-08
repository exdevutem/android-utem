package cl.inndev.miutem.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cl.inndev.miutem.R;

public class CarreraActivity extends AppCompatActivity {

    private long mContadorVida = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrera);
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
}
