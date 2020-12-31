package com.example.farmafast.registro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmafast.MainActivity;
import com.example.farmafast.R;
import com.example.farmafast.dbfirebase.Establecimiento;
import com.example.farmafast.dbfirebase.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistroEstablecimientoActivity extends AppCompatActivity implements View.OnClickListener  {

    private EditText etNombre, etCorreo, etContrasenia, etConfirmarContrasenia, etLongitud, etLatittud;
    private Button btnCrearCuenta;
    private ImageButton btnCoordenadas;
    private TextView tvIngresar;

    private String id, nombre, correo, contrasenia, confirmarContrasenia, longitud, latitud;

    private AlertDialog dialog;

    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDataBase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_establecimiento);

        Componentes();
        VerificarPermiso();
    }

    private void Componentes() {
        //EditText de interfaz
        etNombre = findViewById(R.id.tietNombreEstablecimiento);
        etCorreo = findViewById(R.id.tietCorreoEstablecimiento);
        etContrasenia = findViewById(R.id.tietContraseniaEstablecimiento);
        etConfirmarContrasenia = findViewById(R.id.tietConfirmarContraseniaEstablecimiento);
        etLongitud = findViewById(R.id.tietLongitudEstablecimiento);
        etLatittud = findViewById(R.id.tietLatitudEstablecimiento);
        //Button de interfaz
        btnCrearCuenta = findViewById(R.id.bCrearCuentaEstablecimiento);
        btnCoordenadas = findViewById(R.id.ibCoordenadasEstablecimiento);
        tvIngresar = findViewById(R.id.tvIngresarEstablecimiento);
        btnCrearCuenta.setOnClickListener(this);
        btnCoordenadas.setOnClickListener(this);
        tvIngresar.setOnClickListener(this);
        //Inicializar AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.loading_dialog);
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
            case R.id.bCrearCuentaEstablecimiento: {
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
                                                    //Crear objeto para el usuario
                                                    id = mAuth.getCurrentUser().getUid();
                                                    Establecimiento usuario = new Establecimiento();
                                                    usuario.setUid(id);
                                                    usuario.setNombre(nombre);
                                                    usuario.setCorreo(correo);
                                                    usuario.setContrasenia(contrasenia);
                                                    usuario.setLongitud(longitud);
                                                    usuario.setLatitud(latitud);
                                                    //Realizar insercion del establecimiento en base de datos
                                                    databaseReference.child("establecimientos").child(usuario.getUid()).setValue(usuario)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        /*
                                                                        Realizar insercion del user en base de datos  (control de sesiones con el tipo de usuario)
                                                                        Tipo 1: Usuario
                                                                        Tipo 2: Repartidor
                                                                        Tipo 3: Establecimiento
                                                                         */
                                                                        User u = new User();
                                                                        u.setUid(id);
                                                                        u.setTipo_usuario("3");
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
                                                                        //Error en la creacion del establecimiento
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
                                            //El establecimiento autenticado ya existe
                                            dialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "El establecimiento ya existe", Toast.LENGTH_LONG).show();
                                        } else {
                                            //Error en la creacion del usuario autenticado mediante el correo y contraseña
                                            dialog.dismiss();
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                }
                break;
            }
            case R.id.ibCoordenadasEstablecimiento: {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "No se han definido los permisos necesarios", Toast.LENGTH_LONG).show();
                } else {
                    // Acquire a reference to the system Location Manager
                    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    String locationProvider = LocationManager.NETWORK_PROVIDER;
                    // Or use LocationManager.GPS_PROVIDER
                    Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                    etLongitud.setText(""+lastKnownLocation.getLongitude());
                    etLatittud.setText(""+lastKnownLocation.getLatitude());
                }
                break;
            }
            case R.id.tvIngresarEstablecimiento: {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    private boolean validacion() {
        //leer los valores de los EditText
        nombre = etNombre.getText().toString().trim();
        correo = etCorreo.getText().toString().trim();
        contrasenia = etContrasenia.getText().toString();
        confirmarContrasenia = etConfirmarContrasenia.getText().toString();
        longitud = etLongitud.getText().toString().trim();
        latitud = etLatittud.getText().toString().trim();
        boolean b = true;
        //validar para cada uno que no este vacio
        if (nombre.equals("")) {
            etNombre.setError("Obligatorio");
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
        if (longitud.equals("")) {
            etLongitud.setError("Obligatorio");
            b = false;
        }
        if (latitud.equals("")) {
            etLatittud.setError("Obligatorio");
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