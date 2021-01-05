package com.example.farmafast.ui.repartidor_pedido_actual;

import androidx.lifecycle.ViewModelProviders;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmafast.R;
import com.example.farmafast.dbfirebase.Pedido;
import com.example.farmafast.dbfirebase.PedidoProducto;
import com.example.farmafast.dbfirebase.Producto;
import com.example.farmafast.dbsql.SQLite;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Stack;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class RepartidorPedidoActualFragment extends Fragment implements View.OnClickListener {

    TextView tvInfoPedidoActual;
    Button bIrDomicilio;
    Button bIrEstablecimiento;

    private androidx.appcompat.app.AlertDialog loading_dialog;


    FirebaseDatabase firebaseDataBase;
    DatabaseReference databaseReference;

    String id_repartidor_actual;

    Boolean blnRealizarConsultas = true;

    private RepartidorPedidoActualViewModel mViewModel;

    public static RepartidorPedidoActualFragment newInstance() {
        return new RepartidorPedidoActualFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_repartidor_pedido_actual, container, false);
        //
        iniciarFirebase();
        componentes(root);
        blnRealizarConsultas = true;
        validarPedidoActual();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RepartidorPedidoActualViewModel.class);
        // TODO: Use the ViewModel
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(getContext());
        firebaseDataBase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDataBase.getReference();
    }

    private void componentes(View root) {
        tvInfoPedidoActual = root.findViewById(R.id.tvInfoPedidoActual_repartidor);
        bIrDomicilio = root.findViewById(R.id.bIraDomicilio_repartidor);
        bIrEstablecimiento = root.findViewById(R.id.bIraEstablecimiento_repartidor);
        //
        bIrDomicilio.setOnClickListener(this);
        bIrEstablecimiento.setOnClickListener(this);
        //Inicializar AlertDialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.dialog_loading);
        loading_dialog = builder.create();
        //
        id_repartidor_actual = obtenerIdRepartidor();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bIraEstablecimiento_repartidor: {
                Toast.makeText(getContext(), "bIraEstablecimiento_repartidor",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.bIraDomicilio_repartidor: {
                Toast.makeText(getContext(), "bIraDomicilio_repartidor",Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    String str_id_pedido;
    String str_id_establecimiento;
    String str_estado_pedido;
    String str_info_completa_pedido;

    public void validarPedidoActual(){
        str_id_pedido = "";
        str_id_establecimiento = "";
        str_estado_pedido = "";
        str_info_completa_pedido = "";
        loading_dialog.setTitle("Obteniendo datos");
        loading_dialog.show();
        databaseReference.child("pedido").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                    if (!blnRealizarConsultas){
                        return;
                    }
                    Pedido p = objSnapshot.getValue(Pedido.class);
                    if (p != null) {
                        if (p.getId_repartidor().equals(id_repartidor_actual)){
                            if (p.getEstado().equals("3")||p.getEstado().equals("4")){
                                str_id_pedido=p.getId();
                                str_estado_pedido=p.getEstado();
                                str_id_establecimiento=p.getId_establecimiento();
                                str_info_completa_pedido+=p.getFecha()+" "+p.getHora()+"\n";
                                break;
                            }
                        }
                    }
                }
                if (str_id_pedido.equals("")){
                    loading_dialog.dismiss();
                    tvInfoPedidoActual.setText("No hay pedido aceptado actualmente");
                    bIrDomicilio.setEnabled(false);
                    bIrEstablecimiento.setEnabled(false);
                    return;
                }
                realizarConsultaProductosPedido();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), ""+error.getMessage(),Toast.LENGTH_SHORT).show();
                loading_dialog.dismiss();
                blnRealizarConsultas=false;
            }
        });
    }


    Stack pilaIdProducto = new Stack();
    Stack pilaCantidad = new Stack();

    private void realizarConsultaProductosPedido() {
        databaseReference.child("pedido_producto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!blnRealizarConsultas){
                    return;
                }
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    PedidoProducto pedidoProductoTemporal = objSnapshot.getValue(PedidoProducto.class);
                    if (pedidoProductoTemporal != null) {
                        if (pedidoProductoTemporal.getId() != null) {
                            if (pedidoProductoTemporal.getId_pedido().equals(str_id_pedido)) {
                                pilaIdProducto.push(pedidoProductoTemporal.getId_producto() + "");
                                pilaCantidad.push(pedidoProductoTemporal.getCantidad_producto() + "");
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
                blnRealizarConsultas=false;
            }
        });
    }

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
                    if (!blnRealizarConsultas){
                        return;
                    }
                    Producto productoTemporal = objSnapshot.getValue(Producto.class);
                    if (productoTemporal != null) {
                        if (productoTemporal.getId() != null) {
                            for (int i = 0; i < arrID.length; i++) {
                                if (productoTemporal.getId().equals(arrID[i])) {
                                    str_info_completa_pedido +=
                                            productoTemporal.getNombre() + "\n"+productoTemporal.getPrecio()+" $MXN c/u\n" + arrCantidad[i] + "pieza(s)\n\n";
                                    sumaTotal+=Double.parseDouble(productoTemporal.getPrecio())*Double.parseDouble(arrCantidad[i]+"");
                                    break;
                                }
                            }
                        }
                    }
                }


                if (str_estado_pedido.equals("3")){
                    bIrDomicilio.setEnabled(false);
                }else if (str_estado_pedido.equals("4")){
                    bIrEstablecimiento.setEnabled(false);
                }
                DecimalFormat df2 = new DecimalFormat("#.##");
                str_info_completa_pedido += "Total a pagar: "+df2.format(sumaTotal)+" $MXN";
                tvInfoPedidoActual.setText(str_info_completa_pedido);
                blnRealizarConsultas=false;
                loading_dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(getContext(), "Failed to read value."+error.getMessage(), Toast.LENGTH_SHORT).show();
                loading_dialog.dismiss();
                blnRealizarConsultas=false;
            }
        });
    }


    private String obtenerIdRepartidor() {
        SQLite sqLite = new SQLite(getContext());
        sqLite.abrir();
        Cursor cursor = sqLite.getValor(1);
        if (cursor.getCount() == 0) {
            //No se encotró el registro en la base de datos SQL
            return null;
        }
        cursor.moveToFirst();
        String string_column1 = cursor.getString(1);
        sqLite.cerrar();
        return string_column1;
    }
}