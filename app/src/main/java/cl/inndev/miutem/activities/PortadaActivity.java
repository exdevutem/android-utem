package cl.inndev.miutem.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import cl.inndev.miutem.R;

public class PortadaActivity extends AppCompatActivity {
    private final int DURACION_SPLASH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_portada);

        new Handler().postDelayed(new Runnable() {
            public void run(){
                Intent intent = new Intent(PortadaActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            };
        }, DURACION_SPLASH);

    }
}
