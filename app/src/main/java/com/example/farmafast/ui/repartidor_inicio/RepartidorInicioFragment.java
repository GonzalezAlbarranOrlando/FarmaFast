package com.example.farmafast.ui.repartidor_inicio;

import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.farmafast.R;
import com.example.farmafast.dbfirebase.Pedido;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RepartidorInicioFragment extends Fragment {

    ListView lvListaPedidos;
    private List<Pedido> ListaPedidos = new ArrayList<Pedido>();
    ArrayAdapter<Pedido> arrayAdapterPedidos;
    Pedido pedidoSelected;

    FirebaseDatabase firebaseDataBase;
    DatabaseReference databaseReference;

    private androidx.appcompat.app.AlertDialog loading_dialog;
    View dialogViewPedido;

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
                Log.d("pedidoSelected", ""+pedidoSelected.getHora());
                String str = "" +
                        pedidoSelected.getFecha() + "\n" +
                        pedidoSelected.getHora() + "" +
                        "";
                dialogViewPedido = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pedido, null);
                TextView textView = dialogViewPedido.findViewById(R.id.tVInfoPedidoDialog);
                str+="\n\n¿Deseas aceptar el pedido?";
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
                        //insertar en firebase el pedido
                    }
                });
                dialog_producto.show();

            }
        });
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
                        ListaPedidos.add(p);
                        try {
                            arrayAdapterPedidos =
                                    new ArrayAdapter<Pedido>(getActivity(), android.R.layout.simple_list_item_1, ListaPedidos);
                        } catch (Exception e) {
                            Log.e("ERROR", "Exception: " + e.getMessage());
                        }
                        lvListaPedidos.setAdapter(arrayAdapterPedidos);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}