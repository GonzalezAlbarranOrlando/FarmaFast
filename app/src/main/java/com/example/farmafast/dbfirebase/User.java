package com.example.farmafast.dbfirebase;

public class User {

    private String Uid;
    private String Correo;
    private String Contrasenia;
    private String Tipo_usuario;

    public User() {
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
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

    public String getTipo_usuario() {
        return Tipo_usuario;
    }

    public void setTipo_usuario(String tipo_usuario) {
        Tipo_usuario = tipo_usuario;
    }

    public String toString() {
        return Uid + " " + Correo + " " + Tipo_usuario;
    }
}
