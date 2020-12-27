package com.example.farmafast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmafast.dbfirebase.User;
import com.example.farmafast.dbfirebase.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistroUsuarioActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etNombre, etApellidoPaterno, etApellidoMaterno, etCorreo, etContrasenia, etConfirmarContrasenia, etLongitud, etLatittud;
    private Button btnCrearCuenta;
    private ImageButton btnCoordenadas;
    private TextView tvIngresar;

    private String id, nombre, apellidoPaterno, apellidoMaterno, correo, contrasenia, confirmarContrasenia,longitud, latitud;

    private AlertDialog dialog;

    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDataBase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);
        Componentes();
    }

    private void Componentes(){
        etNombre = findViewById(R.id.tietNombreUsuario);
        etApellidoPaterno = findViewById(R.id.tietApellidoPaternoUsuario);
        etApellidoMaterno = findViewById(R.id.tietApellidoMaternoUsuario);
        etCorreo = findViewById(R.id.tietCorreoUsuario);
        etContrasenia = findViewById(R.id.tietContraseniaUsuario);
        etConfirmarContrasenia = findViewById(R.id.tietConfirmarContraseniaUsuario);
        etLongitud = findViewById(R.id.tietLongitudUsuario);
        etLatittud = findViewById(R.id.tietLatitudUsuario);

        btnCrearCuenta = findViewById(R.id.bCrearCuentaUsuario);
        btnCoordenadas = findViewById(R.id.ibCoordenadasUsuario);
        tvIngresar = findViewById(R.id.tvIngresarUsuario);

        btnCrearCuenta.setOnClickListener(this);
        btnCoordenadas.setOnClickListener(this);
        tvIngresar.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        dialog = builder.create();

        FirebaseApp.initializeApp(this);
        firebaseDataBase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDataBase.getReference();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bCrearCuentaUsuario:{
                if(validacion()){
                    dialog.setMessage("Realizando registro...");
                    dialog.show();
                    //crear un nuevo usuario
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(correo, contrasenia)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        //enviar correo de verificacion
                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    //Crear objeto
                                                    id = mAuth.getCurrentUser().getUid();
                                                    Usuario usuario = new Usuario();
                                                    usuario.setUid(id);
                                                    usuario.setNombre(nombre);
                                                    usuario.setApellidoPaterno(apellidoPaterno);
                                                    usuario.setApellidoMaterno(apellidoMaterno);
                                                    usuario.setCorreo(correo);
                                                    usuario.setContrasenia(contrasenia);
                                                    usuario.setLongitud(longitud);
                                                    usuario.setLatitud(latitud);
                                                    //Realizar inssercion en base de datos
                                                    databaseReference.child("usuarios").child(usuario.getUid()).setValue(usuario)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        User u = new User();
                                                                        u.setUid(id);
                                                                        u.setTipo_usuario("1");
                                                                        databaseReference.child("users").child(u.getUid()).setValue(u)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()){
                                                                                            dialog.dismiss();
                                                                                            Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_LONG).show();
                                                                                            Intent i= new Intent(getApplication(), MainActivity.class);
                                                                                            startActivity(i);
                                                                                        }else{
                                                                                            dialog.dismiss();
                                                                                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }else{
                                                                        dialog.dismiss();
                                                                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                }else {
                                                    dialog.dismiss();
                                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }else{
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException){//Si el usuario ya existe
                                            dialog.dismiss();
                                            Toast.makeText(getApplicationContext(),"El usuario ya existe",Toast.LENGTH_LONG).show();
                                        }else{
                                            dialog.dismiss();
                                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                }
                break;
            }
            case R.id.ibCoordenadasUsuario:{
                break;
            }
            case R.id.tvIngresarUsuario:{
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    private boolean validacion(){
        nombre = etNombre.getText().toString().trim();
        apellidoPaterno = etApellidoPaterno.getText().toString().trim();
        apellidoMaterno = etApellidoMaterno.getText().toString().trim();
        correo = etCorreo.getText().toString().trim();
        contrasenia = etContrasenia.getText().toString();
        confirmarContrasenia = etConfirmarContrasenia.getText().toString();
        longitud = etLongitud.getText().toString().trim();
        latitud = etLatittud.getText().toString().trim();
        boolean b = true;
        if (nombre.equals("")){
            etNombre.setError("Obligatorio");
            b = false;
        }
        if (apellidoPaterno.equals("")){
            etApellidoPaterno.setError("Obligatorio");
            b = false;
        }
        if (apellidoMaterno.equals("")){
            etApellidoMaterno.setError("Obligatorio");
            b = false;
        }
        if (correo.equals("")){
            etCorreo.setError("Obligatorio");
            b = false;
        }
        if (contrasenia.equals("")){
            etContrasenia.setError("Obligatorio");
            b = false;
        }
        if (confirmarContrasenia.equals("")){
            etConfirmarContrasenia.setError("Obligatorio");
            b = false;
        }
        if (longitud.equals("")){
            etLongitud.setError("Obligatorio");
            b = false;
        }
        if (latitud.equals("")){
            etLatittud.setError("Obligatorio");
            b = false;
        }
        if (!contrasenia.equals("") &&  !confirmarContrasenia.equals("") && !contrasenia.equals(confirmarContrasenia)){
            Toast.makeText(this, "Las contraseñas no coindicen", Toast.LENGTH_SHORT).show();
            b = false;
        }
        return b;
    }
}