package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.myapplication.adapter.CityChooseAdapter;
import com.example.myapplication.myClass.City;
import com.example.myapplication.myClass.ClearEditTextView;
import com.example.myapplication.myClass.MyApplication;
import com.example.myapplication.utils.GetLocationUtil;

import java.util.ArrayList;
import java.util.List;

public class LocationChooseActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backBtn; //返回按钮
    private ListView listView;
    private List<City> location_data; //地点数据
    private List<City> filter_data=new ArrayList<>(); //地点数据
    private MyApplication app; //应用程序MyApplication
    private ClearEditTextView clearEditText;
    private CityChooseAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_choose);
        app = (MyApplication)getApplication();//获得我们的应用程序MyApplication
        initResId();
        //返回键监听
        backBtn.setOnClickListener(this);
        adapter = new CityChooseAdapter(LocationChooseActivity.this, R.layout.list_item_1,filter_data);
        listView.setAdapter(adapter);
        //添加listView点击监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = location_data.get(position);
                ContentValues values = new ContentValues(); //ContentValues对要添加的数据进行组装
                values.put("cityId",city.getCityId());
                values.put("city",city.getCity());
                values.put("cityParent",city.getCityParent());
                app.getDb().replace("City",null,values); //插入数据
                app.setLocation_code(city.getCityId()); //更新当前城市代号
                Log.d("database","插入数据成功");
                app.setLoc_name(city.getCity());
                MainActivity.actionStart(LocationChooseActivity.this, city.getCity());

            }
        });
        //根据输入框的值改变来过滤搜索
        clearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框的值为空时，更新为原来的列表
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loc_back:
                finish();
                break;
            default:
                break;
        }
    }


    private void initResId(){
        backBtn = (ImageView)findViewById(R.id.loc_back);
        listView = (ListView)findViewById(R.id.list_view);
        clearEditText=(ClearEditTextView)findViewById(R.id.search_city);
        location_data = app.getLocation_data();
        for (City city: location_data){
            filter_data.add(city);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        app.setActivity_name(getClass().getSimpleName());
    }
    private void filterData(String value){
        filter_data.clear();
        if(TextUtils.isEmpty(value)){
            for (City city: location_data){
                filter_data.add(city);
            }
        }else {
            for (City city: location_data){
                if(city.getCity().indexOf(value.toString())!=-1){
                    filter_data.add(city);
                }
            }
        }
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
