package com.example.pollo.utem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button horario;
    private Button notas;
    private Button salir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        horario = (Button)findViewById(R.id.horario);
        notas = (Button)findViewById(R.id.notas);
        salir = (Button)findViewById(R.id.logout);

        horario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                IngresarHorario();
            }
        });

        notas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                IngresarNotas();
            }
        });

        salir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Salir();
            }
        });
    }

    protected void IngresarHorario(){
        Intent intent = new Intent(MainActivity.this, Horario.class);
        startActivity(intent);
    }

    protected void IngresarNotas(){
        Intent intent = new Intent(MainActivity.this, Notas.class);
        startActivity(intent);
    }

    protected void Salir(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
