package com.example.farmafast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmafast.dbsql.SQLite;
import com.example.farmafast.dbfirebase.User;
import com.example.farmafast.registro.RegistroEstablecimientoActivity;
import com.example.farmafast.registro.RegistroRepartidorActivity;
import com.example.farmafast.registro.RegistroUsuarioActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Alan Burgos commit
    private EditText etCorreo, etContrasenia;
    private Button btnIngresar, btnRegistroUsuario, btnRegistroRepartidor, btnRegistroEstablecimiento;
    private TextView tvRestablecerContrasenia;

    String correo, contrasenia;

    private FirebaseAuth mAuth;

    AlertDialog dialog;

    FirebaseDatabase firebaseDataBase;
    DatabaseReference databaseReference;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Componentes();
        validaSesion();
        iniciarFirebase();
    }

    private void Componentes() {
        //EditText de interfaz
        etCorreo = findViewById(R.id.tietCorreoMain);
        etContrasenia = findViewById(R.id.tietContraseniaMain);
        //Button de interfaz
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
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        //Inicializar AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.loading_dialog);
        dialog = builder.create();
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDataBase = FirebaseDatabase.getInstance();
        //firebaseDataBase.setPersistenceEnabled(true);
        databaseReference = firebaseDataBase.getReference();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bIngresarMain: {
                if (validacion()) {
                    dialog.setMessage("Iniciando sesion");
                    dialog.show();
                    //Iniciar sesion con correo y contraseña
                    mAuth.signInWithEmailAndPassword(correo, contrasenia)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //checking if success
                                    if (task.isSuccessful()) {
                                        if (mAuth.getCurrentUser().isEmailVerified()) {
                                            //Correo ya se encuentra verificado
                                            // Read from the database
                                            databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    // This method is called once with the initial value and again
                                                    // whenever data at this location is updated.
                                                    user = dataSnapshot.getValue(User.class);
                                                    /*
                                                    Tipo 1: Usuario
                                                    Tipo 2: Repartidor
                                                    Tipo 3: Establecimiento
                                                    */
                                                    switch (user.getTipo_usuario()) {
                                                        case "1": {
                                                            Toast.makeText(getApplicationContext(), "Usuario:" + user.toString(), Toast.LENGTH_LONG).show();
                                                            //asignarSesion(user.getUid(), user.getTipo_usuario());
                                                            Intent intent = new Intent(getApplication(), UsuarioActivity.class);
                                                            startActivity(intent);
                                                            dialog.dismiss();
                                                            break;
                                                        }
                                                        case "2": {
                                                            Toast.makeText(getApplicationContext(), "Repartidor:" + user.toString(), Toast.LENGTH_LONG).show();
                                                            //asignarSesion(user.getUid(), user.getTipo_usuario());
                                                            Intent intent = new Intent(getApplication(), RepartidorActivity.class);
                                                            startActivity(intent);
                                                            dialog.dismiss();
                                                            break;
                                                        }
                                                        case "3": {
                                                            Toast.makeText(getApplicationContext(), "Establecimiento:" + user.toString(), Toast.LENGTH_LONG).show();
                                                            //asignarSesion(user.getUid(), user.getTipo_usuario());
                                                            Intent intent = new Intent(getApplication(), EstablecimientoActivity.class);
                                                            startActivity(intent);
                                                            dialog.dismiss();
                                                            break;
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError error) {
                                                    // Failed to read value
                                                    dialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Failed to read value.", Toast.LENGTH_LONG).show();
                                                    Log.w(TAG, "Failed to read value.", error.toException());
                                                }
                                            });
                                        } else {
                                            //Correo sin verificar
                                            dialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Correo sin verificar", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        //Correo o contraseña incorrectos, etc.
                                        dialog.dismiss();
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
        if (cursor.getCount() == 0) {
            //No se encotró el registro en la base de datos SQL
            return;
        }
        cursor.moveToFirst();
        String str_tipousuario = cursor.getString(2);
        sqLite.cerrar();
        /*
        Realizar cambio a interfaz dependiendo el tipo de usuario
        Tipo 0: Sesion vacia
        Tipo 1: Usuario
        Tipo 2: Repartidor
        Tipo 3: Establecimiento
        */
        switch (str_tipousuario) {
            case "0":
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

    private void asignarSesion(String id, String tipousuario) {
        SQLite sqLite = new SQLite(this);
        sqLite.abrir();
        sqLite.updateRegistroSesion(1, id, tipousuario);
        sqLite.cerrar();
    }
}