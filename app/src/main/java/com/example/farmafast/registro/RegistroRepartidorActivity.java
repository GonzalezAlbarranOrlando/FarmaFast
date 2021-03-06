package com.example.farmafast.registro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmafast.MainActivity;
import com.example.farmafast.R;
import com.example.farmafast.dbfirebase.Repartidor;
import com.example.farmafast.dbfirebase.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistroRepartidorActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etNombre, etApellidoPaterno, etApellidoMaterno, etCorreo, etContrasenia, etConfirmarContrasenia;
    private Button btnCrearCuenta;
    private TextView tvIngresar;

    private String id, nombre, apellidoPaterno, apellidoMaterno, correo, contrasenia, confirmarContrasenia;

    private AlertDialog dialog;

    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDataBase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_repartidor);
        Componentes();
        VerificarPermiso();
    }

    private void Componentes() {
        //EditText de interfaz
        etNombre = findViewById(R.id.tietNombreRepartidor);
        etApellidoPaterno = findViewById(R.id.tietApellidoPaternoRepartidor);
        etApellidoMaterno = findViewById(R.id.tietApellidoMaternoRepartidor);
        etCorreo = findViewById(R.id.tietCorreoRepartidor);
        etContrasenia = findViewById(R.id.tietContraseniaRepartidor);
        etConfirmarContrasenia = findViewById(R.id.tietConfirmarContraseniaRepartidor);
        //Button de interfaz
        btnCrearCuenta = findViewById(R.id.bCrearCuentaRepartidor);
        tvIngresar = findViewById(R.id.tvIngresarRepartidor);
        btnCrearCuenta.setOnClickListener(this);
        tvIngresar.setOnClickListener(this);
        //Inicializar AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.dialog_loading);
        dialog = builder.create();
        //Inizializar Firebase
        FirebaseApp.initializeApp(this);
        firebaseDataBase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDataBase.getReference();
    }

    private void VerificarPermiso(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bCrearCuentaRepartidor: {
                if (validacion()) {//Validar que ningun campo este vacio
                    //Se muestra el dialogo mientras se realiza el proceso del registro
                    dialog.setMessage("Realizando registro");
                    dialog.show();
                    //crear un nuevo usuario autenticado mediante el correo y contraseña
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(correo, contrasenia)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //se envia correo de verificacion
                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //Crear objeto para el repartidor
                                                    id = mAuth.getCurrentUser().getUid();
                                                    Repartidor repartidor = new Repartidor();
                                                    repartidor.setUid(id);
                                                    repartidor.setNombre(nombre);
                                                    repartidor.setApellidoPaterno(apellidoPaterno);
                                                    repartidor.setApellidoMaterno(apellidoMaterno);
                                                    repartidor.setCorreo(correo);
                                                    repartidor.setContrasenia(contrasenia);
                                                    //Realizar insercion del repartidor en base de datos
                                                    databaseReference.child("repartidores").child(repartidor.getUid()).setValue(repartidor)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        /*
                                                                        Realizar insercion del user en base de datos  (control de sesiones con el tipo de repartidor)
                                                                        Tipo 1: Usuario
                                                                        Tipo 2: Repartidor
                                                                        Tipo 3: Establecimiento
                                                                         */
                                                                        User u = new User();
                                                                        u.setUid(id);
                                                                        u.setTipo_usuario("2");
                                                                        databaseReference.child("users").child(u.getUid()).setValue(u)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            dialog.dismiss();
                                                                                            Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_LONG).show();
                                                                                            Intent i = new Intent(getApplication(), MainActivity.class);
                                                                                            startActivity(i);
                                                                                        } else {
                                                                                            //Error en la creacion del user
                                                                                            dialog.dismiss();
                                                                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    } else {
                                                                        //Error en la creacion del repartidor
                                                                        dialog.dismiss();
                                                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    //Error al enviar correo de vrificacion
                                                    dialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            //El repartidor autenticado ya existe
                                            dialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "El repartidor ya existe", Toast.LENGTH_LONG).show();
                                        } else {
                                            //Error en la creacion del repartidor autenticado mediante el correo y contraseña
                                            dialog.dismiss();
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                }
                break;
            }
            case R.id.tvIngresarRepartidor: {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    private boolean validacion() {
        //leer los valores de los EditText
        nombre = etNombre.getText().toString().trim();
        apellidoPaterno = etApellidoPaterno.getText().toString().trim();
        apellidoMaterno = etApellidoMaterno.getText().toString().trim();
        correo = etCorreo.getText().toString().trim();
        contrasenia = etContrasenia.getText().toString();
        confirmarContrasenia = etConfirmarContrasenia.getText().toString();
        boolean b = true;
        //validar para cada uno que no este vacio
        if (nombre.equals("")) {
            etNombre.setError("Obligatorio");
            b = false;
        }
        if (apellidoPaterno.equals("")) {
            etApellidoPaterno.setError("Obligatorio");
            b = false;
        }
        if (apellidoMaterno.equals("")) {
            etApellidoMaterno.setError("Obligatorio");
            b = false;
        }
        if (correo.equals("")) {
            etCorreo.setError("Obligatorio");
            b = false;
        }
        if (contrasenia.equals("")) {
            etContrasenia.setError("Obligatorio");
            b = false;
        }
        if (confirmarContrasenia.equals("")) {
            etConfirmarContrasenia.setError("Obligatorio");
            b = false;
        }
        //verificar que las contraseñas coincidan
        if (!contrasenia.equals("") && !confirmarContrasenia.equals("") && !contrasenia.equals(confirmarContrasenia)) {
            Toast.makeText(this, "Las contraseñas no coindicen", Toast.LENGTH_SHORT).show();
            b = false;
        }
        return b;
    }
}