package com.example.farmafast.ui.usuario_carrito;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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
import com.example.farmafast.dbfirebase.Pedido;
import com.example.farmafast.dbfirebase.Producto;
import com.example.farmafast.dbsql.SQLite;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import static android.content.ContentValues.TAG;

public class UsuarioCarritoFragment extends Fragment implements View.OnClickListener {

    TextView tvInfoCarrito;
    Button bComprarCarrito;

    FirebaseDatabase firebaseDataBase;
    DatabaseReference databaseReference;

    String id_usuario_actual = "";
    String str_pedido_info = "";

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

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(getContext());
        firebaseDataBase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDataBase.getReference();
    }

    private void componentes(View root) {
        tvInfoCarrito = root.findViewById(R.id.tvInfoCarrito_usuario);
        bComprarCarrito = root.findViewById(R.id.bComprarCarrito_usuario);
        //
        bComprarCarrito.setOnClickListener(this);
        id_usuario_actual = obtenerIdUsuario();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bComprarCarrito_usuario: {
                obtenerInfoPedido();
                break;
            }
        }
    }

    Pedido pedidoTemporal;
    private void obtenerInfoPedido() {
        databaseReference.child("pedido").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    pedidoTemporal = objSnapshot.getValue(Pedido.class);
                    if (pedidoTemporal != null) {
                        if (pedidoTemporal.getId() != null) {
                            if (pedidoTemporal.getId_usuario().equals(id_usuario_actual) && pedidoTemporal.getEstado().equals("1")) {
                                tvInfoCarrito.setText(pedidoTemporal.getFecha() + " " + pedidoTemporal.getHora());
                                return;
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private String obtenerIdUsuario() {
        SQLite sqLite = new SQLite(getContext());
        sqLite.abrir();
        Cursor cursor = sqLite.getValor(1);
        if (cursor.getCount() == 0) {
            //No se encotró el registro en la base de datos SQL
            return "";
        }
        cursor.moveToFirst();
        String string_column1 = cursor.getString(1);
        sqLite.cerrar();
        return string_column1;
    }
}