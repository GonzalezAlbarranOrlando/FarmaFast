package com.example.farmafast.dbfirebase;

public class Producto {
    private String Id;
    private String Nombre;
    private String Precio;
    private String Imagen;

    public Producto(){
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getPrecio() {
        return Precio;
    }

    public void setPrecio(String precio) {
        Precio = precio;
    }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String imagen) {
        Imagen = imagen;
    }

    public String toString(){
        return "[" + getId() + "] " + getNombre();
    }

}
