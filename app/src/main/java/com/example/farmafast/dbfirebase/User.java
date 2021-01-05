package com.example.farmafast.dbfirebase;

public class User {

    private String Uid;
    private String Tipo_usuario;

    /*
    Tipo 0: Sesion vacia
    Tipo 1: Usuario
    Tipo 2: Repartidor
    Tipo 3: Establecimiento
    */

    public User() {
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getTipo_usuario() {
        return Tipo_usuario;
    }

    public void setTipo_usuario(String tipo_usuario) {
        Tipo_usuario = tipo_usuario;
    }

    public String toString() {
        return Uid + " - " + Tipo_usuario;
    }
}
