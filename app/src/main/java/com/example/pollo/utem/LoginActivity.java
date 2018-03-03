package com.example.pollo.utem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.*;

public class LoginActivity extends AppCompatActivity {

    private EditText Rut;
    private EditText Contrasenia;
    private Button Entrar;
    private TextView Mensaje;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Rut = (EditText)findViewById(R.id.rutEntry);
        Contrasenia = (EditText)findViewById(R.id.passEntry);
        Entrar = (Button)findViewById(R.id.login);

        Entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validar(Rut.getText().toString(), Contrasenia.getText().toString());
            }
        });
    }

    private void validar(String rut, String pass) {
        Mensaje = (TextView)findViewById(R.id.textView2);
        if (rut.equals("19841526") && pass.equals("6c834")){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USERNAME", rut);
            startActivity(intent);
        } else if (rut == null || rut.isEmpty() || pass == null || pass.isEmpty()) {
            Mensaje.setText("No pueden haber campos vacíos");
        } else {
            Mensaje.setText("Los datos son incorrectos");
        }
    }


}
