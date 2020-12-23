package com.example.farmafast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etCorreo, etContrasenia;
    private Button btnIngresar, btnRegistroUsuario, btnRegistroRepartidor, btnRegistroEstablecimiento;
    private TextView tvRestablecerContrasenia;

    String correo, contrasenia;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Componentes();
        validaSesion();
    }

    private void Componentes() {
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

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bIngresarMain: {
                if(validacion()){
                    progressDialog.setMessage("Procesando...");
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(correo, contrasenia)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //checking if success
                                    if(task.isSuccessful()){
                                        if (mAuth.getCurrentUser().isEmailVerified()){
                                            /*
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this,"Bienvenido",Toast.LENGTH_SHORT).show();
                                            asignarPreferencias(correo);
                                            Intent intent = new Intent(getApplication(), CrudActivity.class);
                                            startActivity(intent);
                                             */
                                        }else{
                                            //correo sin verificar
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this,"Correo sin verificar",Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        //correo o contraseña incorrectos, etc.
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;
            }
            case R.id.bRegistroUsuarioMain: {
                Intent intent = new Intent(this, RegistroActivity.class);
                intent.putExtra("tipo_usuario", "1");
                startActivity(intent);
                break;
            }
            case R.id.bRegistroRepartidorMain: {
                Intent intent = new Intent(this, RegistroActivity.class);
                intent.putExtra("tipo_usuario", "2");
                startActivity(intent);
                break;
            }
            case R.id.bRegistroEstablecimientoMain: {
                Intent intent = new Intent(this, RegistroEstablecimientoActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.tvRestablecerContraseniaMain: {
                correo = etCorreo.getText().toString().trim();
                if (correo.equals("")) {
                    etCorreo.setError("Obligatorio");
                    return;
                }
                progressDialog.setMessage("Procesando...");
                progressDialog.show();
                mAuth.sendPasswordResetEmail(correo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Se te ha enviado un correo para reestablecer la contraseña", Toast.LENGTH_LONG).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                break;
            }
        }
    }

    private boolean validacion() {
        correo = etCorreo.getText().toString().trim();
        contrasenia = etContrasenia.getText().toString();
        boolean b = true;
        if (correo.equals("")) {
            etCorreo.setError("Obligatorio");
            b = false;
        }
        if (contrasenia.equals("")) {
            etContrasenia.setError("Obligatorio");
            b = false;
        }
        return b;
    }


    private void validaSesion() {
        String preferences_correo = preferences.getString("correo", null);
        if (preferences_correo != null) {
            String preferences_tipousu = preferences.getString("tipo_usuario", null);
            switch (preferences_tipousu) {
                case "1":
                    //Intent intent = new Intent(getApplication(), CrudActivity.class);
                    //startActivity(intent);
                    break;
                case "2":
                    break;
                case "3":
                    break;
            }
        }
    }

    private void asignarPreferencias(String str_cor) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("correo", str_cor);
        editor.commit();
    }
}