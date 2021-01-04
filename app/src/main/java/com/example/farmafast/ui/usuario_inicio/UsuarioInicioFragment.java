package com.example.farmafast.ui.usuario_inicio;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    ImageView imageView;
    File localFile;
    private androidx.appcompat.app.AlertDialog dialog;
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
        dialog = builder.create();
        //
        lvListaProductos = root.findViewById(R.id.lvListaProductosUsuario);
        lvListaProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                productoSelected = (Producto) parent.getItemAtPosition(position);
                //Toast.makeText(getContext(), "ID:" + pacienteSelected.getUid(), Toast.LENGTH_LONG).show();
                String str = "" +
                        productoSelected.getNombre() + "\n" +
                        "Precio: " + productoSelected.getPrecio() + "$\n" +
                        "Imagen: " + productoSelected.getImagen() + "" +
                        "";
                dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_producto, null);
                TextView textView = dialogView.findViewById(R.id.tVInfoProductoDialog);
                imageView = dialogView.findViewById(R.id.iVFotoDialog);
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
        dialog.setMessage("Obteniendo datos...");
        dialog.show();
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
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle("Producto");
                        dialog.setView(dialogView);
                        dialog.setPositiveButton("Aceptar",null);
                        dialog.show();
                        imageView.setImageURI(Uri.fromFile(localFile));
                        localFile = null;
                        UsuarioInicioFragment.this.dialog.dismiss();
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
                UsuarioInicioFragment.this.dialog.dismiss();
            }
        });
    }

}