package com.example.myapplication.myClass;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

import java.util.List;

public class MyLocationListerner extends BDAbstractLocationListener {
    public String cityName;
    public String cityCode;
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        String city = bdLocation.getCity(); //获取城市
        System.out.println("城市："+city);
        cityName=city.replace("市","");
        List<City> cityList= MyApplication.location_list;
        for (City city1:cityList){
            if(city1.getCity().equals(cityName)){
                cityCode=city1.getCityId();
            }
        }
    }
}
