package com.example.farmafast.ui.establecimiento_inicio;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.farmafast.R;
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
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EstablecimientoInicioFragment extends Fragment implements View.OnClickListener {

    ImageButton ibAdd, ibSave, ibDelete;
    Button bLimpiar;
    EditText etNombre, etPrecio;
    ListView lvProductos;
    private List<Producto> ListaProductos = new ArrayList<Producto>();
    ArrayAdapter<Producto> arrayAdapterProducto;
    Producto productoSelected;

    FirebaseDatabase firebaseDataBase;
    DatabaseReference databaseReference;
    private StorageReference mStorageRef;

    private ImageView ivFoto;

    private AlertDialog dialog;

    String str_nombre = "", str_precio = "";

    File localFile = null;

    private Uri photoURI;
    public static String currentPhotoPath = "", img = "";
    public static final int REQUEST_TAKE_PHOTO = 1;

    private EstablecimientoInicioViewModel mViewModel;

    public static EstablecimientoInicioFragment newInstance() {
        return new EstablecimientoInicioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_establecimiento_inicio, container, false);
        Componentes(root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(EstablecimientoInicioViewModel.class);
        // TODO: Use the ViewModel
    }

    private void Componentes(View root) {
        etNombre = root.findViewById(R.id.tietNombre_producto_establecimiento);
        etPrecio = root.findViewById(R.id.tietPrecio_producto_establecimiento);
        lvProductos = root.findViewById(R.id.lvLista_productos_establecimiento);
        //
        bLimpiar = root.findViewById(R.id.bLimpiar_producto_establecimiento);
        ivFoto = root.findViewById(R.id.iVFoto_producto_establecimiento);
        ibAdd = root.findViewById(R.id.ibAdd_producto_establecimiento);
        ibSave = root.findViewById(R.id.ibSave_producto_establecimiento);
        ibDelete = root.findViewById(R.id.ibDelete_producto_establecimiento);
        //
        bLimpiar.setOnClickListener(this);
        ivFoto.setOnClickListener(this);
        ibAdd.setOnClickListener(this);
        ibSave.setOnClickListener(this);
        ibDelete.setOnClickListener(this);
        //Inicializar AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.loading_dialog);
        dialog = builder.create();
        //
        iniciarFirebase();
        listarDatos();
        //
        lvProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                productoSelected = (Producto) parent.getItemAtPosition(position);
                etNombre.setText(productoSelected.getNombre());
                etPrecio.setText(productoSelected.getPrecio());
                img = productoSelected.getImagen();
                cargarImagen(productoSelected.getImagen());
            }
        });
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
                    ListaProductos.add(p);
                    arrayAdapterProducto = new ArrayAdapter<Producto>(getContext(), android.R.layout.simple_list_item_1, ListaProductos);
                    lvProductos.setAdapter(arrayAdapterProducto);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void limpiar(){
        etNombre.setText("");
        etPrecio.setText("");
        ivFoto.setImageResource(R.drawable.ic_menu_gallery);
        str_nombre = "";
        str_precio = "";
        currentPhotoPath = "";
        img = "";
    }

    private boolean validacion(){
        boolean b = true;
        str_nombre = etNombre.getText().toString().trim();
        str_precio = etPrecio.getText().toString().trim();
        if (str_nombre.equals("")){
            etNombre.setError("Obligatorio");
            b = false;
        }
        if (str_precio.equals("")){
            etPrecio.setError("Obligatorio");
            b = false;
        }
        if (img.equals("")){
            Toast.makeText(getContext(), "Foto obligatoria", Toast.LENGTH_SHORT).show();
            b = false;
        }
        return b;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibAdd_producto_establecimiento:{
                if (validacion()){
                    String str_idEstablecimiento = obtenerIdEstablecimiento();
                    Producto p = new Producto();
                    p.setId(UUID.randomUUID().toString());
                    p.setNombre(str_nombre);
                    p.setPrecio(str_precio);
                    p.setImagen(img);
                    p.setId_establecimiento(str_idEstablecimiento);
                    databaseReference.child("producto").child(p.getId()).setValue(p);
                    Toast.makeText(getContext(), "Agregado",Toast.LENGTH_SHORT).show();
                    limpiar();
                    listarDatos();
                }
                break;
            }
            case R.id.ibSave_producto_establecimiento:{
                Producto p = new Producto();
                p.setId(productoSelected.getId());
                p.setNombre(etNombre.getText().toString().trim());
                p.setPrecio(etPrecio.getText().toString().trim());
                p.setImagen(img);
                databaseReference.child("producto").child(p.getId()).setValue(p);
                Toast.makeText(getContext(), "Actualizado",Toast.LENGTH_LONG).show();
                limpiar();
                listarDatos();
                break;
            }
            case R.id.ibDelete_producto_establecimiento:{
                Producto p = new Producto();
                p.setId(productoSelected.getId());
                databaseReference.child("producto").child(p.getId()).removeValue();
                Toast.makeText(getContext(), "Eliminado",Toast.LENGTH_LONG).show();
                limpiar();
                listarDatos();
                break;
            }
            case R.id.iVFoto_producto_establecimiento:{
                Intent tomarFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(tomarFoto.resolveActivity(getActivity().getPackageManager()) != null){
                    File photoFile = null;
                    try {
                        photoFile = createImgFile();
                    }catch (IOException ie){
                        ie.printStackTrace();
                        Toast.makeText(getContext(), "Error en fotografía", Toast.LENGTH_SHORT).show();
                    }
                    if(photoFile != null){
                        photoURI = FileProvider.getUriForFile(getContext(),"com.example.farmafast",photoFile);
                        tomarFoto.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(tomarFoto, REQUEST_TAKE_PHOTO);
                    }
                }
                break;
            }
            case R.id.bLimpiar_producto_establecimiento:{
                limpiar();
                listarDatos();
                break;
            }
        }
    }

    private File createImgFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imgFileName = "img_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File img = File.createTempFile(imgFileName, ".jpg", storageDir);
        currentPhotoPath = img.getAbsolutePath();
        return img;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK){
            ivFoto.setImageURI(photoURI);
            //img = currentPhotoPath;
            img = photoURI.getPath();
            StorageReference riversRef = mStorageRef.child(photoURI.getPath());
            dialog.setMessage("Subiendo archivo");
            dialog.show();
            riversRef.putFile(photoURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(getContext(), "Proceso realizado", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            dialog.dismiss();
                            Toast.makeText(getContext(), ""+exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void cargarImagen(String imagen) {

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
                        if(localFile == null){
                            return;
                        }
                        ivFoto.setImageURI(Uri.fromFile(localFile));
                        localFile = null;
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
                Toast.makeText(getContext(), "Error al cargar la imagen", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

    private String obtenerIdEstablecimiento(){
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