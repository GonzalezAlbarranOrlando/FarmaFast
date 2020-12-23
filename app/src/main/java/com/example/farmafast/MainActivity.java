package com.example.farmafast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etCorreo, etContrasenia;
    private Button btnIngresar, btnRegistroUsuario, btnRegistroRepartidor, btnRegistroEstablecimiento;
    private TextView tvRestablecerContrasenia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Componentes();
    }

    private void Componentes(){
        etCorreo = findViewById(R.id.tietCorreoMain);
        etContrasenia = findViewById(R.id.tietContraseniaMain);

        btnIngresar = findViewById(R.id.bIngresarMain);
        btnRegistroUsuario = findViewById(R.id.bRegistroUsuarioMain);
        btnRegistroRepartidor = findViewById(R.id.bRegistroRepartidorMain);
        btnRegistroEstablecimiento = findViewById(R.id.bRegistroEstablecimientoMain);

        tvRestablecerContrasenia = findViewById(R.id.tvRestablecerContraseniaMain);

        btnIngresar.setOnClickListener(this);
        btnRegistroUsuario.setOnClickListener(this);
        btnRegistroRepartidor.setOnClickListener(this);
        btnRegistroEstablecimiento.setOnClickListener(this);
        tvRestablecerContrasenia.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bIngresarMain:
                Toast.makeText(this, "bIngresarMain", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bRegistroUsuarioMain:
                Toast.makeText(this, "bRegistroUsuarioMain", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bRegistroRepartidorMain:
                Toast.makeText(this, "bRegistroRepartidorMain", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bRegistroEstablecimientoMain:
                Toast.makeText(this, "bRegistroEstablecimientoMain", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tvRestablecerContraseniaMain:
                Toast.makeText(this, "tvRestablecerContraseniaMain", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}