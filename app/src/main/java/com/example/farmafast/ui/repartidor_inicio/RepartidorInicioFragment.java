package com.example.farmafast.ui.repartidor_inicio;

import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmafast.R;
import com.example.farmafast.dbfirebase.Pedido;
import com.example.farmafast.dbsql.SQLite;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class RepartidorInicioFragment extends Fragment {

    ListView lvListaPedidos;
    private List<Pedido> ListaPedidos = new ArrayList<Pedido>();
    ArrayAdapter<Pedido> arrayAdapterPedidos;
    Pedido pedidoSelected;

    FirebaseDatabase firebaseDataBase;
    DatabaseReference databaseReference;

    private androidx.appcompat.app.AlertDialog loading_dialog;
    View dialogViewPedido;

    SharedPreferences preferences;

    String id_repartidor_actual = "";

    boolean blnRepartidorTienePedidoActivo = false;

    private RepartidorInicioViewModel mViewModel;

    public static RepartidorInicioFragment newInstance() {
        return new RepartidorInicioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_repartidor_inicio, container, false);
        //
        iniciarFirebase();
        listarDatos();
        //Inicializar loading_dialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.dialog_loading);
        loading_dialog = builder.create();
        //
        lvListaPedidos = root.findViewById(R.id.lvListaPedidosDisponiblesRepartidor);
        lvListaPedidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pedidoSelected = (Pedido) parent.getItemAtPosition(position);
                String str = "" +
                        pedidoSelected.getFecha() + "\n" +
                        pedidoSelected.getHora() + "" +
                        "";
                dialogViewPedido = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pedido, null);
                TextView textView = dialogViewPedido.findViewById(R.id.tVInfoPedidoDialog);
                str += "\n\n¿Deseas aceptar el pedido?";
                textView.setText(str);

                AlertDialog.Builder dialog_producto = new AlertDialog.Builder(getContext());
                dialog_producto.setTitle("Pedido");
                dialog_producto.setView(dialogViewPedido);
                dialog_producto.setCancelable(false);
                dialog_producto.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nada
                    }
                });
                dialog_producto.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //validar si hay un pedido en proceso
                        if (blnRepartidorTienePedidoActivo){
                            Toast.makeText(getContext(), "No puedes aceptar más de un pedido a la vez", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //insertar actualizar el estado del pedido
                        databaseReference.child("pedido").child(pedidoSelected.getId()).child("estado").setValue("3");
                        databaseReference.child("pedido").child(pedidoSelected.getId()).child("id_repartidor").setValue(id_repartidor_actual);
                        asignarPreferenciasPedido(
                                pedidoSelected.getId(),
                                pedidoSelected.getId_usuario(),
                                pedidoSelected.getId_establecimiento(),
                                id_repartidor_actual,
                                pedidoSelected.getFecha(),
                                pedidoSelected.getHora(),
                                "3"
                        );
                    }
                });
                dialog_producto.show();
            }
        });
        //shared preferences
        preferences = this.getActivity().getSharedPreferences("pedidoActivo",MODE_PRIVATE);
        //
        id_repartidor_actual = obtenerIdRepartidor();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RepartidorInicioViewModel.class);
        // TODO: Use the ViewModel
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(getContext());
        firebaseDataBase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDataBase.getReference();
    }

    public void listarDatos() {
        databaseReference.child("pedido").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListaPedidos.clear();
                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                    Pedido p = objSnapshot.getValue(Pedido.class);
                    if (p != null) {
                        if (p.getEstado().equals("2")) {
                            ListaPedidos.add(p);
                            if (ListaPedidos!=null){
                                arrayAdapterPedidos = new ArrayAdapter<Pedido>(requireContext(), android.R.layout.simple_list_item_1, ListaPedidos);
                                lvListaPedidos.setAdapter(arrayAdapterPedidos);
                            }
                        }else if (p.getEstado().equals("3")||p.getEstado().equals("")) {
                            blnRepartidorTienePedidoActivo = true;
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void asignarPreferenciasPedido(String id_pedido, String id_usuario, String id_establecimiento, String id_repartidor, String fecha, String hora, String estado) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id_pedido",id_pedido);
        editor.putString("id_usuario",id_usuario);
        editor.putString("id_establecimiento",id_establecimiento);
        editor.putString("id_repartidor",id_repartidor);
        editor.putString("fecha",fecha);
        editor.putString("hora",hora);
        editor.putString("estado",estado);
        editor.commit();
    }

    private String obtenerIdRepartidor() {
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