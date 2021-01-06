package com.example.farmafast.ui.repartidor_pedido_actual;

import androidx.lifecycle.ViewModelProviders;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
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

import com.example.farmafast.EstablecimientoActivity;
import com.example.farmafast.R;
import com.example.farmafast.RepartidorActivity;
import com.example.farmafast.dbfirebase.Establecimiento;
import com.example.farmafast.dbfirebase.Pedido;
import com.example.farmafast.dbfirebase.PedidoProducto;
import com.example.farmafast.dbfirebase.Producto;
import com.example.farmafast.dbfirebase.Usuario;
import com.example.farmafast.dbsql.SQLite;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class RepartidorPedidoActualFragment extends Fragment implements View.OnClickListener {

    TextView tvInfoPedidoActual;
    Button bIrDomicilio;
    Button bIrEstablecimiento;
    Button bEntregaRealizada;

    private androidx.appcompat.app.AlertDialog loading_dialog;


    FirebaseDatabase firebaseDataBase;
    DatabaseReference databaseReference;

    String id_repartidor_actual;

    Boolean blnRealizarConsultas = true;

    String longitudEstablecimiento = "";
    String latitudEstablecimiento = "";

    String longitudCliente = "";
    String latitudCliente = "";

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
        bEntregaRealizada = root.findViewById(R.id.bEntregaRealizada_repartidor);
        //
        bIrDomicilio.setOnClickListener(this);
        bIrEstablecimiento.setOnClickListener(this);
        bEntregaRealizada.setOnClickListener(this);
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
                //Toast.makeText(getContext(), "bIraEstablecimiento_repartidor", Toast.LENGTH_SHORT).show();
                //databaseReference.child("pedido").child(str_id_pedido).child("estado").setValue("3");
                //blnRealizarConsultas=true;
                irDestino(longitudEstablecimiento,latitudEstablecimiento);
                break;
            }
            case R.id.bIraDomicilio_repartidor: {
                //Toast.makeText(getContext(), "bIraDomicilio_repartidor", Toast.LENGTH_SHORT).show();
                databaseReference.child("pedido").child(str_id_pedido).child("estado").setValue("4");
                irDestino(longitudCliente,latitudCliente);
                break;
            }
            case R.id.bEntregaRealizada_repartidor: {
                //Toast.makeText(getContext(), "bEntregaRealizada_repartidor", Toast.LENGTH_SHORT).show();
                tvInfoPedidoActual.setText("No hay pedido aceptado actualmente");
                bIrDomicilio.setEnabled(false);
                bIrEstablecimiento.setEnabled(false);
                bEntregaRealizada.setEnabled(false);
                databaseReference.child("pedido").child(str_id_pedido).child("estado").setValue("5");
                break;
            }
        }
    }

    String str_id_pedido;
    String str_id_establecimiento;
    String str_id_usuario;
    String str_estado_pedido;
    String str_info_completa_pedido;

    public void validarPedidoActual() {
        str_id_pedido = "";
        str_id_usuario = "";
        str_id_establecimiento = "";
        str_estado_pedido = "";
        str_info_completa_pedido = "";
        loading_dialog.setTitle("Obteniendo datos");
        loading_dialog.show();
        databaseReference.child("pedido").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                    if (!blnRealizarConsultas) {
                        return;
                    }
                    Pedido p = objSnapshot.getValue(Pedido.class);
                    if (p != null) {
                        if (p.getId_repartidor().equals(id_repartidor_actual)) {
                            if (p.getEstado().equals("3") || p.getEstado().equals("4")) {
                                str_id_pedido = p.getId();
                                str_id_usuario = p.getId_usuario();
                                str_estado_pedido = p.getEstado();
                                str_id_establecimiento = p.getId_establecimiento();
                                str_info_completa_pedido += p.getFecha() + " " + p.getHora() + "\n\n";
                                break;
                            }
                        }
                    }
                }
                if (str_id_pedido.equals("")) {
                    loading_dialog.dismiss();
                    tvInfoPedidoActual.setText("No hay pedido aceptado actualmente");
                    bIrDomicilio.setEnabled(false);
                    bIrEstablecimiento.setEnabled(false);
                    bEntregaRealizada.setEnabled(false);
                    return;
                }
                realizarConsultaProductosPedido();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                loading_dialog.dismiss();
                blnRealizarConsultas = false;
            }
        });
    }


    Stack pilaIdProducto = new Stack();
    Stack pilaCantidad = new Stack();

    private void realizarConsultaProductosPedido() {
        databaseReference.child("pedido_producto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!blnRealizarConsultas) {
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
                Toast.makeText(getContext(), "Failed to read value." + error.getMessage(), Toast.LENGTH_SHORT).show();
                loading_dialog.dismiss();
                blnRealizarConsultas = false;
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
                    if (!blnRealizarConsultas) {
                        return;
                    }
                    Producto productoTemporal = objSnapshot.getValue(Producto.class);
                    if (productoTemporal != null) {
                        if (productoTemporal.getId() != null) {
                            for (int i = 0; i < arrID.length; i++) {
                                if (productoTemporal.getId().equals(arrID[i])) {
                                    str_info_completa_pedido +=
                                            productoTemporal.getNombre() + "\n" + productoTemporal.getPrecio() + " $MXN c/u\n" + arrCantidad[i] + "pieza(s)\n\n";
                                    sumaTotal += Double.parseDouble(productoTemporal.getPrecio()) * Double.parseDouble(arrCantidad[i] + "");
                                    break;
                                }
                            }
                        }
                    }
                }
                DecimalFormat df2 = new DecimalFormat("#.##");
                str_info_completa_pedido += "Total a pagar: " + df2.format(sumaTotal) + " $MXN\n\n";
                realizarConsultaEstablecimiento();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(getContext(), "Failed to read value." + error.getMessage(), Toast.LENGTH_SHORT).show();
                loading_dialog.dismiss();
                blnRealizarConsultas = false;
            }
        });
    }


    private void realizarConsultaEstablecimiento() {
        databaseReference.child("establecimientos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!blnRealizarConsultas) {
                    return;
                }
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    Establecimiento establecimiento = objSnapshot.getValue(Establecimiento.class);
                    if (establecimiento != null) {
                        if (establecimiento.getUid() != null) {
                            if (establecimiento.getUid().equals(str_id_establecimiento)) {
                                longitudEstablecimiento = establecimiento.getLongitud();
                                latitudEstablecimiento = establecimiento.getLatitud();
                                str_info_completa_pedido += "Establecimiento:\n" + establecimiento.getNombre() + "\n" +
                                        "Longitud: " + longitudEstablecimiento + "\n" +
                                        "Latitud: " + latitudEstablecimiento + "\n\n";
                            }
                        }
                    }
                }
                //
                realizarConsultaUsuario();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(getContext(), "Failed to read value." + error.getMessage(), Toast.LENGTH_SHORT).show();
                loading_dialog.dismiss();
                blnRealizarConsultas = false;
            }
        });
    }

    private void realizarConsultaUsuario() {
        databaseReference.child("usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!blnRealizarConsultas) {
                    return;
                }
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    Usuario usu = objSnapshot.getValue(Usuario.class);
                    if (usu != null) {
                        if (usu.getUid() != null) {
                            if (usu.getUid().equals(str_id_usuario)) {
                                longitudCliente = usu.getLongitud();
                                latitudCliente = usu.getLatitud();
                                str_info_completa_pedido += "Cliente:\n" + usu.toString() + "\n" +
                                        "Longitud: " + longitudEstablecimiento + "\n" +
                                        "Latitud: " + latitudEstablecimiento + "\n\n";
                            }
                        }
                    }
                }
                //

                if (str_estado_pedido.equals("3")) {
                    //bIrDomicilio.setEnabled(false);
                    //bEntregaRealizada.setEnabled(false);
                } else if (str_estado_pedido.equals("4")) {
                    //bIrEstablecimiento.setEnabled(false);
                }
                tvInfoPedidoActual.setText(str_info_completa_pedido);
                blnRealizarConsultas = false;
                loading_dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(getContext(), "Failed to read value." + error.getMessage(), Toast.LENGTH_SHORT).show();
                loading_dialog.dismiss();
                blnRealizarConsultas = false;
            }
        });
    }




    public void irDestino(String longitud, String latitud) {
        String direc="";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> direccion = geocoder.getFromLocation(Double.parseDouble(latitud), Double.parseDouble(longitud),1);
            direc = direccion.get(0).getAddressLine(0);
            //txtUbicacion.setText(direc);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try
        {
            // Launch Waze to look for location
            String url = "https://waze.com/ul?q="+direc+"";
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
            startActivity( intent );
        }
        catch ( ActivityNotFoundException ex  )
        {
            // If Waze is not installed, open it in Google Play:
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
            startActivity(intent);
        }
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