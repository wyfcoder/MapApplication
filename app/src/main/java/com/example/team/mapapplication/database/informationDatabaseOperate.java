package com.example.team.mapapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class informationDatabaseOperate
{
   private informationDatabaseOpenHelper informationDatabaseOpenHelper;
   private SQLiteDatabase sqLiteDatabase;
   private Context context;
   private static final String T1="Information";
   private static final String T2="SavedData";

   public informationDatabaseOperate(Context context)
   {
       this.context=context;
       informationDatabaseOpenHelper=new informationDatabaseOpenHelper(this.context,"InformationDatabase.db",null,1);;
       sqLiteDatabase=informationDatabaseOpenHelper.getWritableDatabase();
   }

   public void getAllInformation()
   {
       Cursor cursor=sqLiteDatabase.query(T1,null,null,null,null,null,null);
       if(cursor.moveToFirst())
       {
           do
           {

           }
           while (cursor.moveToNext());
       }

   }

   public long getDataSize()
   {
       String sql = "SELECT COUNT(*) FROM " + T2;
       SQLiteStatement sqLiteStatement=sqLiteDatabase.compileStatement(sql);
       long count=sqLiteStatement.simpleQueryForLong();
       return count;
   }

   public long getInformationSize()
   {
       String sql = "SELECT COUNT(*) FROM " + T1;
       SQLiteStatement sqLiteStatement=sqLiteDatabase.compileStatement(sql);
       long count=sqLiteStatement.simpleQueryForLong();
       return count;
   }

   public void insertData(String name, double latitude, double longitude, double signalStrength)
   {
           ContentValues contentValues = new ContentValues();
           contentValues.put("name", name);
           contentValues.put("latitude", latitude);
           contentValues.put("longitude", longitude);
           contentValues.put("signalStrength", signalStrength);
           sqLiteDatabase.insert(T2, null, contentValues);
   }
   public void insertInformation(String name, String time) {
       if (!isExitInformation(name))
       {
           ContentValues contentValues = new ContentValues();
           contentValues.put("name", name);
           contentValues.put("time", time);
           sqLiteDatabase.insert(T1, null, contentValues);
       }
   }

    public void delete_Data(String informationName) {
        sqLiteDatabase.delete(T1,"name = ?",new String[]{informationName});
        sqLiteDatabase.delete(T2,"name = ?",new String[]{informationName});
    }

    public Cursor Query_Information(String informatonName) {
        return sqLiteDatabase.query(T1,null,"name = ?",new String[]{informatonName},null,null,null);
    }

    public Cursor Query_Data(String name) {
        return sqLiteDatabase.query(T2,null,"name = ?",new String[]{name},null,null,null);
    }

   public boolean isExitInformation(String name)
   {
       Cursor cursor=Query_Information(name);
       return  cursor.moveToFirst();
   }

   public void closeDatabase()
   {
       sqLiteDatabase.close();
   }

}
