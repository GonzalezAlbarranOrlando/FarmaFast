package com.example.farmafast.dbfirebase;

public class PedidoProducto {
    private String Id;
    private String Id_pedido;
    private String Id_producto;
    private String Cantidad_producto;

    public PedidoProducto() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getId_pedido() {
        return Id_pedido;
    }

    public void setId_pedido(String id_pedido) {
        Id_pedido = id_pedido;
    }


    public String getId_producto() {
        return Id_producto;
    }

    public void setId_producto(String id_producto) {
        Id_producto = id_producto;
    }

    public String getCantidad_producto() {
        return Cantidad_producto;
    }

    public void setCantidad_producto(String cantidad_producto) {
        Cantidad_producto = cantidad_producto;
    }

    public String toString() {
        return Id_pedido+"-"+Cantidad_producto;
    }
}
