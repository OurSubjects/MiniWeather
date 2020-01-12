package com.example.myapplication.utils;

import com.example.myapplication.bean.HFForecastWeatherBean;
import com.example.myapplication.bean.HFNowWeatherBean;
import com.example.myapplication.bean.TQWeatherBean;
import com.google.gson.Gson;

public class JsonUtil {
    /*
    将返回的Json数据解析成WeatherBean类,利用GSON
     */
    public static TQWeatherBean handleTQWeatherResponse(String responseData){
        try {
            return new Gson().fromJson(responseData, TQWeatherBean.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static HFNowWeatherBean handleHFNowWeatherResponse(String responseData){
        try {
            return new Gson().fromJson(responseData, HFNowWeatherBean.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static HFForecastWeatherBean handleHFForecastWeatherResponse(String responseData){
        try {
            return new Gson().fromJson(responseData, HFForecastWeatherBean.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
