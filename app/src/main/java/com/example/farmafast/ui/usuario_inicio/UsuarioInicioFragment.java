package com.example.farmafast.ui.usuario_inicio;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.farmafast.dbfirebase.Producto;
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
import java.util.ArrayList;
import java.util.List;

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
    View dialogView;

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
        //Inicializar AlertDialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.loading_dialog);
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
                dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_producto, null);
                TextView textView = dialogView.findViewById(R.id.tVInfoProductoDialog);
                imageView = dialogView.findViewById(R.id.iVFotoDialog);

                ibMas = dialogView.findViewById(R.id.ibMas_cantidad_Dialog);
                ibMenos = dialogView.findViewById(R.id.ibMenos_cantidad_Dialog);
                etCantidad = dialogView.findViewById(R.id.tietCantidad_producto_Dialog);
                etCantidad.setEnabled(false);
                cantidad = 1;
                etCantidad.setText(""+cantidad);
                ibMas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cantidad++;
                        etCantidad.setText(""+cantidad);
                    }
                });

                ibMenos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cantidad==1){
                            return;
                        }
                        cantidad--;
                        etCantidad.setText(""+cantidad);
                    }
                });
                textView.setText(str);
                cargarImagenes(productoSelected.getImagen());
            }
        });
        return root;
    }

    private void iniciarFirebase(){
        FirebaseApp.initializeApp(getContext());
        firebaseDataBase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDataBase.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public void listarDatos(){
        databaseReference.child("producto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListaProductos.clear();
                for (DataSnapshot objSnapshot: snapshot.getChildren()){
                    Producto p = objSnapshot.getValue(Producto.class);
                    if(p!=null){
                        ListaProductos.add(p);
                        try {
                            arrayAdapterProductos =
                                    new ArrayAdapter<Producto>(getActivity(), android.R.layout.simple_list_item_1, ListaProductos);
                        }catch (Exception e){
                            Log.e("ERROR","Exception: "+e.getMessage());
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

    public void cargarImagenes(String imagen){
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
                        dialog_producto.setView(dialogView);
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
                dialog.setView(dialogView);
                dialog.setPositiveButton("Aceptar",null);
                dialog.show();
                Toast.makeText(getContext(),"Error al cargarla imagen",Toast.LENGTH_LONG).show();
                UsuarioInicioFragment.this.loading_dialog.dismiss();
            }
        });
    }

}