package com.example.farmafast.ui.usuario_inicio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.farmafast.R;
import com.example.farmafast.dbfirebase.Pedido;
import com.example.farmafast.dbfirebase.PedidoProducto;
import com.example.farmafast.dbfirebase.Producto;
import com.example.farmafast.dbsql.SQLite;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class UsuarioInicioFragment extends Fragment {

    ListView lvListaProductos;
    private List<Producto> ListaProductos = new ArrayList<Producto>();
    ArrayAdapter<Producto> arrayAdapterProductos;
    Producto productoSelected;

    FirebaseDatabase firebaseDataBase;
    DatabaseReference databaseReference;
    private StorageReference mStorageRef;

    EditText etCantidad;
    ImageButton ibMas;
    ImageButton ibMenos;
    int cantidad = 1;

    ImageView imageView;
    File localFile;
    private androidx.appcompat.app.AlertDialog loading_dialog;
    View dialogViewProducto;

    String str_pedidoId = "";
    String id_usuario_actual = "";

    boolean blnRealizarRegistro = false;

    private UsuarioInicioViewModel usuarioInicioViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        usuarioInicioViewModel =
                ViewModelProviders.of(this).get(UsuarioInicioViewModel.class);
        View root = inflater.inflate(R.layout.fragment_usuario_inicio, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        usuarioInicioViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        //
        iniciarFirebase();
        listarDatos();
        //Inicializar dialog_loading
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.dialog_loading);
        loading_dialog = builder.create();
        //
        lvListaProductos = root.findViewById(R.id.lvListaProductosUsuario);
        lvListaProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                productoSelected = (Producto) parent.getItemAtPosition(position);
                //Toast.makeText(getContext(), "ID:" + pacienteSelected.getUid(), Toast.LENGTH_LONG).show();
                String str = "" +
                        productoSelected.getNombre() + "\n" +
                        "Precio: " + productoSelected.getPrecio() + " $MXN" +
                        "";
                dialogViewProducto = LayoutInflater.from(getContext()).inflate(R.layout.dialog_producto, null);
                TextView textView = dialogViewProducto.findViewById(R.id.tVInfoProductoDialog);
                imageView = dialogViewProducto.findViewById(R.id.iVFotoDialog);
                //
                ibMas = dialogViewProducto.findViewById(R.id.ibMas_cantidad_Dialog);
                ibMenos = dialogViewProducto.findViewById(R.id.ibMenos_cantidad_Dialog);
                etCantidad = dialogViewProducto.findViewById(R.id.tietCantidad_producto_Dialog);
                etCantidad.setEnabled(false);
                cantidad = 1;
                etCantidad.setText("" + cantidad);
                ibMas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cantidad++;
                        etCantidad.setText("" + cantidad);
                    }
                });
                ibMenos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cantidad == 1) {
                            return;
                        }
                        cantidad--;
                        etCantidad.setText("" + cantidad);
                    }
                });
                textView.setText(str);
                cargarImagenes(productoSelected.getImagen());
            }
        });
        id_usuario_actual = obtenerIdUsuario();
        return root;
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(getContext());
        firebaseDataBase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDataBase.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public void listarDatos() {
        databaseReference.child("producto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListaProductos.clear();
                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                    Producto p = objSnapshot.getValue(Producto.class);
                    if (p != null) {
                        ListaProductos.add(p);
                        try {
                            arrayAdapterProductos =
                                    new ArrayAdapter<Producto>(getActivity(), android.R.layout.simple_list_item_1, ListaProductos);
                        } catch (Exception e) {
                            Log.e("ERROR", "Exception: " + e.getMessage());
                        }
                        lvListaProductos.setAdapter(arrayAdapterProductos);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void cargarImagenes(String imagen) {
        loading_dialog.setMessage("Obteniendo datos...");
        loading_dialog.show();
        localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mStorageRef.child(imagen).getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                        AlertDialog.Builder dialog_producto = new AlertDialog.Builder(getContext());
                        dialog_producto.setTitle("Producto");
                        dialog_producto.setView(dialogViewProducto);
                        dialog_producto.setCancelable(false);
                        dialog_producto.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //nada
                            }
                        });
                        dialog_producto.setPositiveButton("Carrito", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //insertar en firebase el pedido
                                blnRealizarRegistro = true;
                                realizarRegistro();
                            }
                        });
                        dialog_producto.show();
                        imageView.setImageURI(Uri.fromFile(localFile));
                        localFile = null;
                        UsuarioInicioFragment.this.loading_dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("Producto");
                dialog.setView(dialogViewProducto);
                dialog.setPositiveButton("Aceptar", null);
                dialog.show();
                Toast.makeText(getContext(), "Error al cargarla imagen", Toast.LENGTH_SHORT).show();
                UsuarioInicioFragment.this.loading_dialog.dismiss();
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

    Pedido pedidoTemporal;
    private void realizarRegistro() {
        str_pedidoId = "";
        databaseReference.child("pedido").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!blnRealizarRegistro){
                    return;
                }
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    pedidoTemporal = objSnapshot.getValue(Pedido.class);
                    if (pedidoTemporal != null) {
                        if (pedidoTemporal.getId() != null) {
                            Log.w("pedido", "" + pedidoTemporal.getId());
                            if (pedidoTemporal.getId_usuario().equals(id_usuario_actual) && pedidoTemporal.getEstado().equals("1")) {
                                str_pedidoId = pedidoTemporal.getId();
                                break;
                            }
                        }
                    }
                }
                if (blnRealizarRegistro){
                    if (str_pedidoId.equals("")) {
                        str_pedidoId = UUID.randomUUID().toString();
                        Pedido pe = new Pedido();
                        pe.setId(str_pedidoId);
                        pe.setId_usuario(id_usuario_actual);
                        pe.setId_establecimiento(productoSelected.getId_establecimiento());
                        pe.setEstado("1");
                        pe.setFecha(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                        pe.setHora(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        pe.setId_repartidor("");
                        databaseReference.child("pedido").child(pe.getId()).setValue(pe);
                        pedidoTemporal = pe;
                    }
                    PedidoProducto pp = new PedidoProducto();
                    pp.setId(UUID.randomUUID().toString());
                    pp.setId_pedido(str_pedidoId);
                    pp.setId_producto(productoSelected.getId());
                    pp.setCantidad_producto(cantidad + "");
                    databaseReference.child("pedido_producto").child(pp.getId()).setValue(pp);
                    blnRealizarRegistro = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}