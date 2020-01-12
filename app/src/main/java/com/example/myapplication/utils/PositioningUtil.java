package com.example.myapplication.utils;

import android.content.Context;
import android.location.LocationManager;

public class PositioningUtil {
    private LocationManager locationManager;
    private Context context;
    private PositioningUtil(Context context){
        this.context=context;
        locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }
}
