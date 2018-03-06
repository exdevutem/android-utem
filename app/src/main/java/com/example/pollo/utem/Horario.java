package com.example.pollo.utem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Horario extends AppCompatActivity {
    private Button salir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario);

        salir = (Button)findViewById(R.id.logout);

        salir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Salir();
            }
        });
    }

    protected void Salir(){
        Intent intent = new Intent(Horario.this, LoginActivity.class);
        startActivity(intent);
    }
}
