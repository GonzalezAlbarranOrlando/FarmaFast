package com.example.farmafast.ui.usuario_carrito;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.farmafast.R;
import com.example.farmafast.dbfirebase.Producto;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class UsuarioCarritoFragment extends Fragment implements View.OnClickListener {

    TextView tvInfoCarrito;
    Button bComprarCarrito;

    FirebaseDatabase firebaseDataBase;
    DatabaseReference databaseReference;

    String id_usuario_actual = "";

    private UsuarioCarritoViewModel usuarioCarritoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        usuarioCarritoViewModel =
                ViewModelProviders.of(this).get(UsuarioCarritoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_usuario_carrito, container, false);
        //final TextView textView = root.findViewById(R.id.text_slideshow);
        usuarioCarritoViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        //
        iniciarFirebase();
        componentes(root);
        return root;
    }

    private void iniciarFirebase(){
        FirebaseApp.initializeApp(getContext());
        firebaseDataBase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDataBase.getReference();
    }

    private void componentes(View root) {
        tvInfoCarrito = root.findViewById(R.id.tvInfoCarrito_usuario);
        bComprarCarrito = root.findViewById(R.id.bComprarCarrito_usuario);
        //
        bComprarCarrito.setOnClickListener(this);
        tvInfoCarrito.setText("hola hola\nhola");
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(), "Comprar", Toast.LENGTH_SHORT).show();
    }
}