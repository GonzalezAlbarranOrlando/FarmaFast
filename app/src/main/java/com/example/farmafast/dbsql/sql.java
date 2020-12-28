package com.example.farmafast.dbsql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class sql extends SQLiteOpenHelper {

    private static final String database = "sesion";
    private static final int VERSION = 1;

    private static final String tableSesion = "CREATE TABLE SESION(" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "IDFIREBASE TEXT NOT NULL, " +
            "TIPOUSUARIO TEXT NOT NULL " +
            ");";

    private static final String insertSesion = "INSERT INTO SESION (ID,  IDFIREBASE, TIPOUSUARIO) " +
            "VALUES (1, '0', '0');";

    public sql(Context context){
        super(context, database, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tableSesion);
        db.execSQL(insertSesion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){
            db.execSQL("DROP TABLE IF EXISTS SESION");
            db.execSQL(tableSesion);
        }
    }
}
