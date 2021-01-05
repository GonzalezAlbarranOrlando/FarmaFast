package com.example.farmafast.ui.usuario_carrito;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.farmafast.R;
import com.example.farmafast.dbfirebase.Pedido;
import com.example.farmafast.dbfirebase.PedidoProducto;
import com.example.farmafast.dbfirebase.Producto;
import com.example.farmafast.dbsql.SQLite;
import com.example.farmafast.ui.usuario_inicio.UsuarioInicioFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Stack;

import static android.content.ContentValues.TAG;

public class UsuarioCarritoFragment extends Fragment implements View.OnClickListener {

    TextView tvInfoCarrito;
    Button bComprarCarrito;

    FirebaseDatabase firebaseDataBase;
    DatabaseReference databaseReference;

    String id_usuario_actual = "";
    String str_pedido_info = "";
    String str_id_pedido = "";

    boolean realizarConsultas = true;

    private androidx.appcompat.app.AlertDialog loading_dialog;

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
        realizarConsultas=true;
        obtenerInfoPedido();
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
        //Inicializar AlertDialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.dialog_loading);
        loading_dialog = builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bComprarCarrito_usuario: {
                realizarConsultas = false;
                str_pedido_info="";
                id_usuario_actual="";
                String aux = str_id_pedido;
                str_id_pedido = "";
                tvInfoCarrito.setText("Agrega productos al carrito");
                bComprarCarrito.setEnabled(false);
                databaseReference.child("pedido").child(aux).child("estado").setValue("2");
                Toast.makeText(getContext(), "Los productos serán enviados de inmediato",Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    Pedido pedidoTemporal;

    private void obtenerInfoPedido() {
        loading_dialog.setMessage("Obteniendo datos...");
        loading_dialog.show();
        str_pedido_info = "";
        str_id_pedido = "";
        databaseReference.child("pedido").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    pedidoTemporal = objSnapshot.getValue(Pedido.class);
                    if (pedidoTemporal != null && realizarConsultas) {
                        if (pedidoTemporal.getId() != null) {
                            if (pedidoTemporal.getId_usuario().equals(id_usuario_actual) && pedidoTemporal.getEstado().equals("1")) {
                                str_pedido_info += pedidoTemporal.getFecha() + " " + pedidoTemporal.getHora() + "\n\n";
                                str_id_pedido = pedidoTemporal.getId();
                                break;
                            }
                        }
                    }
                }
                realizarConsultaProductosPedido();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(getContext(), "Failed to read value."+error.getMessage(), Toast.LENGTH_SHORT).show();
                loading_dialog.dismiss();
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

    PedidoProducto PedidoProductoTemporal;
    Stack pilaIdProducto = new Stack();
    Stack pilaCantidad = new Stack();

    private void realizarConsultaProductosPedido() {
        databaseReference.child("pedido_producto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    PedidoProductoTemporal = objSnapshot.getValue(PedidoProducto.class);
                    if (PedidoProductoTemporal != null && realizarConsultas) {
                        if (PedidoProductoTemporal.getId() != null) {
                            if (PedidoProductoTemporal.getId_pedido().equals(str_id_pedido)) {
                                pilaIdProducto.push(PedidoProductoTemporal.getId_producto() + "");
                                pilaCantidad.push(PedidoProductoTemporal.getCantidad_producto() + "");
                            }
                        }
                    }
                }
                realizarConsultaProductos();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(getContext(), "Failed to read value."+error.getMessage(), Toast.LENGTH_SHORT).show();
                loading_dialog.dismiss();
            }
        });
    }

    Producto productoTemporal;
    String idProductoTemporal = "";
    String cantidadTemporal = "";
    double sumaTotal = 0;

    private void realizarConsultaProductos() {
        sumaTotal = 0;
        idProductoTemporal = "";
        cantidadTemporal = "";
        final Object[] arrID = pilaIdProducto.toArray();
        final Object[] arrCantidad = pilaCantidad.toArray();
        databaseReference.child("producto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    productoTemporal = objSnapshot.getValue(Producto.class);
                    if (productoTemporal != null && realizarConsultas) {
                        if (productoTemporal.getId() != null) {
                            for (int i = 0; i < arrID.length; i++) {
                                if (productoTemporal.getId().equals(arrID[i])) {
                                    str_pedido_info +=
                                            productoTemporal.getNombre() + "\n"+productoTemporal.getPrecio()+" $MXN c/u\n" + arrCantidad[i] + "pieza(s)\n\n";
                                    sumaTotal+=Double.parseDouble(productoTemporal.getPrecio())*Double.parseDouble(arrCantidad[i]+"");
                                    break;
                                }
                            }
                        }
                    }
                }
                if (str_pedido_info.equals("")) {
                    tvInfoCarrito.setText("Agrega productos al carrito");
                    bComprarCarrito.setEnabled(false);
                } else {
                    DecimalFormat df2 = new DecimalFormat("#.##");
                    str_pedido_info += "Total a pagar: "+df2.format(sumaTotal)+" $MXN";
                    tvInfoCarrito.setText(str_pedido_info);
                }
                loading_dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(getContext(), "Failed to read value."+error.getMessage(), Toast.LENGTH_SHORT).show();
                loading_dialog.dismiss();
            }
        });
    }
}