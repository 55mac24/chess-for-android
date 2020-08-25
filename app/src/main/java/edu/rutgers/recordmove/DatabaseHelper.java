package edu.rutgers.recordmove;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="database";
    private static final String TABLE0 = "table0";
    private static final String TABLE1 = "table1";
    private static final String TABLE2 = "table2";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String table0="create table " + TABLE0 + "(id INTEGER PRIMARY KEY, name TEXT)";
        String table1="create table " + TABLE1 + "(id INTEGER PRIMARY KEY, time TEXT)";
        String table2="create table " + TABLE2 + "(id INTEGER PRIMARY KEY, instruction TEXT)";
        db.execSQL(table0);
        db.execSQL(table1);
        db.execSQL(table2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE0);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE1);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE2);
        onCreate(db);
    }

    public boolean insert(String name,String instruction){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //name implement
        ContentValues values0 = new ContentValues();
        values0.put("name",name);
        sqLiteDatabase.insert(TABLE0,null,values0);
        //time implement
        ContentValues values1 = new ContentValues();
        values1.put("time", LocalDateTime.now().toString());
        sqLiteDatabase.insert(TABLE1,null,values1);
        //instruction insert
        ContentValues values2 = new ContentValues();
        values2.put("instruction",instruction);
        sqLiteDatabase.insert(TABLE2,null,values2);
        System.out.println("4"+name+instruction);
        return true;
     }
     public ArrayList getName(){
        SQLiteDatabase sqLiteDatabase = this .getReadableDatabase();
         ArrayList<String> arrayList = new ArrayList<String>();
         Cursor cursor = sqLiteDatabase.rawQuery("select * from "+TABLE0,null);
         cursor.moveToFirst();
         while(!cursor.isAfterLast()){
             arrayList.add(cursor.getString(cursor.getColumnIndex("name")));
             cursor.moveToNext();
         }
         return arrayList;
     }
    public ArrayList getTime(){
        SQLiteDatabase sqLiteDatabase = this .getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<String>();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from "+TABLE1,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            arrayList.add(cursor.getString(cursor.getColumnIndex("time")));
            cursor.moveToNext();
        }
        return arrayList;
    }
    public ArrayList getinstruction(){
        SQLiteDatabase sqLiteDatabase = this .getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<String>();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from "+TABLE2,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            arrayList.add(cursor.getString(cursor.getColumnIndex("instruction")));
            cursor.moveToNext();
        }
        return arrayList;
    }
}
