package com.example.pollo.utem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView Bienvenida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bienvenida = (TextView)findViewById(R.id.textView);
        Bienvenida.setText("¡Bienvenido " + getIntent().getStringExtra("USERNAME") + "!");
    }
}
