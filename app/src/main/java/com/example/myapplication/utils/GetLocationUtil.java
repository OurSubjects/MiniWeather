package com.example.myapplication.utils;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.myClass.City;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetLocationUtil{
    private Context context;
    private String[] province;  //省
    private Map<String,String[]> cites = new HashMap<String, String[]>(); //省，市联动
    private Map<String, String[]> area = new HashMap<String, String[]>();  //市，区联动
    private void initData(){
        try {
            JSONArray jsonArray= new JSONArray(LoadJsonFileUtil.getJson(context, "location.json"));
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                province[i] = jsonObject.getString("name");
            }
        }catch (Exception e){
            Log.d("error",e.toString());
        }

    }
    public static List<City> getProvinces(Context context){
        List<City> cities = new ArrayList<City>();
        try {
            JSONArray jsonArray= new JSONArray(LoadJsonFileUtil.getJson(context, "location.json"));
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                City city = new City(jsonObject.getString("City"),jsonObject.getString("CityParent"),jsonObject.getString("CityId"));
                cities.add(city);
            }
        }catch (Exception e){
            Log.d("error",e.toString());
        }
        return cities;
    }

}
