package com.example.myapplication.myClass;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.database.MyDatabaseHelper;
import com.example.myapplication.utils.GetLocationUtil;

import java.util.List;

/*
    * 重写Application，主要重写里面的onCreate方法，就是创建的时候，
    * 启动Application，他就会创建一个PID，就是进程ID，所有的Activity就会在此进程上运行。
    * 那么我们在Application创建的时候初始化全局变量，所有的Activity都可以拿到这些
*/
public class MyApplication extends Application {
    private final MyDatabaseHelper dbHelper = new MyDatabaseHelper(this, "CityStore.db", null, 1);//不可变的对象
    private SQLiteDatabase db; //SQLiteDatabase对象
    private String location_code; //定义一个程序当前选择城市的代码的全局变量
    private String loc_name; //定义一个程序当前选择城市的代码的全局变量
    private String activity_name; //当前活动
    private List<City> location_data;
    public  static List<City> location_list;
    @Override
    public void onCreate() {
        super.onCreate();
        setDb(dbHelper.getWritableDatabase()); //用于创建和升级数据库
        setLocation_code("CN101010100");
        setLoc_name("北京");
        location_data = GetLocationUtil.getProvinces(getApplicationContext());
        location_list = GetLocationUtil.getProvinces(getApplicationContext());
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public List<City> getLocation_data() {
        return location_data;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    public MyDatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public String getLocation_code() {
        return location_code;
    }

    public void setLocation_code(String location_code) {
        this.location_code = location_code;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getLoc_name() {
        return loc_name;
    }

    public void setLoc_name(String loc_name) {
        this.loc_name = loc_name;
    }
}
