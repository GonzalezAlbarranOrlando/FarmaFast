package com.example.farmafast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class RegistroActivity extends AppCompatActivity {

    private String tipo_usuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Bundle b = getIntent().getExtras();
        tipo_usuario = b.getString("tipo_usuario");
    }

}