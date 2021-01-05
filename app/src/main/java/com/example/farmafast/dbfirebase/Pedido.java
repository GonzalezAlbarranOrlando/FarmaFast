package com.example.farmafast.dbfirebase;

public class Pedido {
    private String Id;
    private String Id_usuario;
    private String Id_establecimiento;
    private String Id_repartidor;
    private String Fecha;
    private String Hora;
    private String Estado;
    /*
    Estado 1: Pedido Carrito
    Estado 2: Pedido Realizado
    Estado 3: Pedido Aceptado por Repartidor
    Estado 4: Pedido Entregado
     */

    public Pedido() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getId_usuario() {
        return Id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        Id_usuario = id_usuario;
    }


    public String getId_establecimiento() {
        return Id_establecimiento;
    }

    public void setId_establecimiento(String id_establecimiento) {
        Id_establecimiento = id_establecimiento;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getHora() {
        return Hora;
    }

    public void setHora(String hora) {
        Hora = hora;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public String getId_repartidor() {
        return Id_repartidor;
    }

    public void setId_repartidor(String id_repartidor) {
        Id_repartidor = id_repartidor;
    }

    public String toString() {
        return Hora+" - "+Fecha;
    }
}
