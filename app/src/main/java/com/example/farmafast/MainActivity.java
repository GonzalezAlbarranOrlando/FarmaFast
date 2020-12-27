package com.example.farmafast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmafast.bdsql.SQLite;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etCorreo, etContrasenia;
    private Button btnIngresar, btnRegistroUsuario, btnRegistroRepartidor, btnRegistroEstablecimiento;
    private TextView tvRestablecerContrasenia;

    String correo, contrasenia;

    private FirebaseAuth mAuth;

    private SharedPreferences preferences;
    AlertDialog dialog;


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
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        dialog = builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bIngresarMain: {
                if(validacion()){
                    dialog.setMessage("Procesando...");
                    dialog.show();
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
                                            dialog.dismiss();
                                            Toast.makeText(MainActivity.this,"Correo sin verificar",Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        //correo o contraseña incorrectos, etc.
                                        dialog.dismiss();
                                        Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;
            }
            case R.id.bRegistroUsuarioMain: {
                Intent intent = new Intent(this, RegistroUsuarioActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.bRegistroRepartidorMain: {
                Intent intent = new Intent(this, RegistroRepartidorActivity.class);
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
                dialog.setMessage("Procesando...");
                dialog.show();
                mAuth.sendPasswordResetEmail(correo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Se te ha enviado un correo para reestablecer la contraseña", Toast.LENGTH_LONG).show();
                        } else {
                            dialog.dismiss();
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
        SQLite sqLite = new SQLite(this);
        sqLite.abrir();
        Cursor cursor = sqLite.getValor(1);
        if (cursor.getCount() == 0){
            return;
        }
        cursor.moveToFirst();
        String str_tipousuario = cursor.getString(2);
        sqLite.cerrar();
            switch (str_tipousuario) {
                case"0":
                    //no hay sesion registrada
                    break;
                case "1":
                    //Intent intent = new Intent(getApplication(), );
                    //startActivity(intent);
                    break;
                case "2":
                    //Intent intent = new Intent(getApplication(), );
                    //startActivity(intent);
                    break;
                case "3":
                    //Intent intent = new Intent(getApplication(), );
                    //startActivity(intent);
                    break;
            }
    }

}