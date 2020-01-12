package com.example.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_CITY= "create table City ("
            +"cityId text primary key,"
            +"city text,"
            +"cityParent text)";
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //如果数据库不存在会调用，否则不调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CITY);
        Log.d("database","创建City表成功");
    }

    //用于升级数据库(比如增加了表,因为从数据库存在不会调用onCreate方法),只需在构造方法中传递版本大于之前版本就会调用这个方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
