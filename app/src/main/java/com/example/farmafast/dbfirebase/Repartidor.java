package com.example.farmafast.dbfirebase;

public class Repartidor {
    private String Uid;
    private String Nombre;
    private String ApellidoPaterno;
    private String ApellidoMaterno;
    private String Correo;
    private String Contrasenia;

    public Repartidor() {
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellidoPaterno() {
        return ApellidoPaterno;
    }

    public void setApellidoPaterno(String apellido) {
        ApellidoPaterno = apellido;
    }

    public String getApellidoMaterno() {
        return ApellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        ApellidoMaterno = apellidoMaterno;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getContrasenia() {
        return Contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        Contrasenia = contrasenia;
    }

    public String toString() {
        return Nombre + " " + ApellidoPaterno + " " + ApellidoMaterno;
    }
}
