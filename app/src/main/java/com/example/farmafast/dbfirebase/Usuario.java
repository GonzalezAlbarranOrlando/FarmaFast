package com.example.farmafast.dbfirebase;

public class Usuario {
    private String Uid;
    private String Nombre;
    private String ApellidoPaterno;
    private String ApellidoMaterno;
    private String Coordenadas;

    public Usuario() {
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

    public String getCoordenadas() {
        return Coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        Coordenadas = coordenadas;
    }

    public String toString() {
        return Nombre + " " + ApellidoPaterno + " " + ApellidoMaterno;
    }
}