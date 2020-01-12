package com.example.myapplication.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetState {
    public static boolean isNetWorkCAvailable(Context context) {
        if(context != null){
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager !=null){
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(networkInfo.isConnected()){
                    return networkInfo.isAvailable();
                }
            }else
                return false;
        }
        return false;
    }
}
