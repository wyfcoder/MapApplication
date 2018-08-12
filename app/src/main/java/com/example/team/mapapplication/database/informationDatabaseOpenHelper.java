package com.example.team.mapapplication.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class informationDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_DATA="create table Information("
            +"name text,"
            +"time text)";
    public static final String DADA="create table SavedData("
            +"name text,"
            +"latitude real,"
            +"longitude real,"
            +"signalStrength real)";
    private Context context;

    public informationDatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        this.context=context;
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_DATA);
        db.execSQL(DADA);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}