package com.example.farmafast.bdsql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.util.Log;

import java.util.ArrayList;

public class SQLite {
    private sql sql;
    private SQLiteDatabase db;

    public SQLite (Context context){
        sql = new sql(context);
    }

    public void abrir(){
        Log.i("SQLite", "Se abre conexion con BD" + sql.getDatabaseName());
        db = sql.getWritableDatabase();
    }

    public void cerrar(){
        Log.i("SQLite", "Se cierra conexion con BD" + sql.getDatabaseName());
        sql.close();
    }

    public boolean addRegistroSesion(int id, String idfirebase, String tipousuario, String correo){
        ContentValues cv = new ContentValues();
        cv.put("ID", id);
        cv.put("IDFIREBASE", idfirebase);
        cv.put("TIPOUSUARIO", tipousuario);
        cv.put("CORREO", correo);
        return (db.insert("SESION", null, cv) != -1 ? true : false);
    }

    public Cursor getRegistro(){
        return db.rawQuery("SELECT * FROM SESION", null);
    }

    public ArrayList<String> getSesion(Cursor cursor){
        ArrayList<String> ListData = new ArrayList<>();
        String item = "";
        if (cursor.moveToFirst()){
            do {
                item += "ID: [" + cursor.getString(0) + "]\r\n";
                item += "IDFIREBASE: [" + cursor.getString(1) + "]\r\n";
                item += "TIPOUSUARIO: [" + cursor.getString(2) + "]\r\n";
                item += "CORREO: [" + cursor.getString(3) + "]\r\n";
                ListData.add(item);
                item = "";
            }while (cursor.moveToNext());
        }
        return ListData;
    }

    public String updateRegistroSesion(int id, String idfirebase, String tipousuario, String correo){
        ContentValues cv = new ContentValues();
        cv.put("ID", id);
        cv.put("IDFIREBASE", idfirebase);
        cv.put("TIPOUSUARIO", tipousuario);
        cv.put("CORREO", correo);
        int  valor = db.update("SESION", cv, "ID = " + id, null);
        if (valor == 1){
            return "SESION modificada";
        }else{
            return "Error en actualización de SESION";
        }
    }

    public Cursor getValor(int id){
        return db.rawQuery("SELECT * FROM SESION WHERE ID = " + id, null);
    }

    public int Eliminar(Editable id){
        return db.delete("SESION", "ID = " + id, null);
    }

    public ArrayList<String> getID(Cursor cursor){
        ArrayList<String> ListData = new ArrayList<>();
        String item = "";
        if (cursor.moveToFirst()){
            do {
                item += "ID: [" + cursor.getString(0) + "]\r\n";
                ListData.add(item);
                item = "";
            }while (cursor.moveToNext());
        }
        return ListData;
    }

    public ArrayList<String> getIDFIREBASE(Cursor cursor){
        ArrayList<String> ListData = new ArrayList<>();
        String item = "";
        if (cursor.moveToFirst()){
            do {
                item += "IDFIREBASE: [" + cursor.getString(1) + "]\r\n";
                ListData.add(item);
                item = "";
            }while (cursor.moveToNext());
        }
        return ListData;
    }

    public ArrayList<String> getTIPOUSUARIO(Cursor cursor){
        ArrayList<String> ListData = new ArrayList<>();
        String item = "";
        if (cursor.moveToFirst()){
            do {
                item += "TIPOUSUARIO: [" + cursor.getString(2) + "]\r\n";
                ListData.add(item);
                item = "";
            }while (cursor.moveToNext());
        }
        return ListData;
    }

    public ArrayList<String> getCORREO(Cursor cursor){
        ArrayList<String> ListData = new ArrayList<>();
        String item = "";
        if (cursor.moveToFirst()){
            do {
                item += "CORREO: [" + cursor.getString(3) + "]\r\n";
                ListData.add(item);
                item = "";
            }while (cursor.moveToNext());
        }
        return ListData;
    }

}
