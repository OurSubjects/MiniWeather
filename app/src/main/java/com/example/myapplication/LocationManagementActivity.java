package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.adapter.CityManageAdapter;
import com.example.myapplication.myClass.City;
import com.example.myapplication.myClass.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class LocationManagementActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backBtn; //返回按钮
    private ImageView addBtn; //添加城市按钮
    private RecyclerView recyclerView;
    protected String location_name; //返回地点
    private CityManageAdapter cityManageAdapter;//适配器
    private MyApplication app; //应用程序MyApplication

    protected List<City> locationList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_management);
        Intent intent = getIntent();
        location_name = intent.getStringExtra("location_name_main");
        initResId();
        app = (MyApplication)getApplication();//获得我们的应用程序MyApplication
        backBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);//如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        cityManageAdapter = new CityManageAdapter(locationList);
        recyclerView.setAdapter(cityManageAdapter);
        cityManageAdapter.setOnItemClickListener(new CityManageAdapter.OnItemClickListener(){

            @Override
            public void onItemClick(View v, int position, City city) {
                app.setLocation_code(city.getCityId());
                Intent intent = new Intent();
                app.setLoc_name(city.getCity());
                intent.putExtra("location_name_manage",city.getCity());
                setResult(RESULT_OK,intent);
                finish();
            }

            //删除该item在数据库的数据
            @Override
            public void onImageClick(View v, int position, City city) {
                locationList.remove(position); //集合移除该条
                cityManageAdapter.notifyItemRemoved(position);//通知移除该条
                cityManageAdapter.notifyItemRangeRemoved(position,locationList.size()-position);//更新适配器这条后面列表的变化
//                cityManageAdapter.notifyDataSetChanged();
                app.getDb().delete("City","cityId = ?",new String[] {city.getCityId()});
                Log.d("database","删除数据成功");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        app.setActivity_name(getClass().getSimpleName());
        initLocation();
    }

    //从数据库读取用户保存的地点数据
    private void initLocation(){
        //查询City中所有数据
        Cursor cursor = app.getDb().query("City",null,null,null,null,null,null);
        if(cursor.moveToFirst()){ //将数据的指针移动到第一行位置
            do{
                //getColumnIndex获取某一列在表中对应的位置索引
                String cityId = cursor.getString(cursor.getColumnIndex("cityId"));
                String city = cursor.getString(cursor.getColumnIndex("city"));
                String cityParent = cursor.getString(cursor.getColumnIndex("cityParent"));
                if(location_name.equals(city)){
                    locationList.add(new City(city,cityParent,cityId,true));
                }else{
                    locationList.add(new City(city,cityParent,cityId));
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
    }
    //初始化控件
    private void initResId(){
        backBtn = (ImageView)findViewById(R.id.back_lm);
        addBtn = (ImageView)findViewById(R.id.location_add_lm);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_lm);
    }
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_lm:
                Intent intent_back = new Intent();
                intent_back.putExtra("location_name_manage",location_name);
                setResult(RESULT_OK,intent_back);
                finish();
                break;
            case R.id.location_add_lm:
                Intent intent_add = new Intent(LocationManagementActivity.this, LocationChooseActivity.class);
                startActivity(intent_add);
                break;
            default:
                break;
        }
    }
    // 如果用户不是通过点击返回按钮，而是通过按下Back键返回上一个活动，就会执行onBackPressed()方法
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("location_name_manage",location_name);
        setResult(RESULT_OK,intent);
        finish();
    }
}
