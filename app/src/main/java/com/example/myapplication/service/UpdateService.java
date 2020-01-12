package com.example.myapplication.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.myapplication.bean.HFForecastWeatherBean;
import com.example.myapplication.bean.HFNowWeatherBean;
import com.example.myapplication.myClass.MyApplication;
import com.example.myapplication.utils.DateUtil;
import com.example.myapplication.utils.HttpUtil;
import com.example.myapplication.utils.JsonUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UpdateService extends Service {
    private MyApplication app; //应用程序MyApplication
    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //在服务创建的时候调用
    @Override
    public void onCreate() {
        super.onCreate();
        app = (MyApplication)getApplication();//获得我们的应用程序MyApplication
        Log.d("UpdateService","UpdateService Create");
    }

    //在服务每次启动的时候调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int halfHour = 30*60*1000; //半小时
        long triggerAtTime = SystemClock.elapsedRealtime()+halfHour;
        Intent i = new Intent(this,UpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi); //系统设定一个alarm，等这个alarm结束之后，再设定下一个,需要取消上一个
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        Log.d("UpdateService","UpdateService Start");
        return super.onStartCommand(intent, flags, startId);
    }

    //在服务销毁的时候调用
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("UpdateService","UpdateService Stop");
    }
    /** 判断程序是否在前台运行且某个活动是否在栈顶（当前运行的程序） */
    public boolean isRunForeground(String activityName) {
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(100);
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return app.getActivity_name().equals(activityName);// 程序运行在前台
            }
        }
        return false;
    }
    /*
     *更新天气信息
     */
    private void updateWeather(){
        String cityCode = app.getLocation_code();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(UpdateService.this).edit();
        editor.putString("cityCode",cityCode);
        editor.apply();
        final String hf_nowWeatherUrl = "https://free-api.heweather.net/s6/weather/now?location="+cityCode+"&key=eba7a23ca1154d268627eb892eb15527";
        final String hf_forecastWeatherUrl = "https://free-api.heweather.net/s6/weather/forecast?location="+cityCode+"&key=eba7a23ca1154d268627eb892eb15527";
        HttpUtil.sendOkHttpRequest(hf_nowWeatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("sendOkHttpRequest",e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                HFNowWeatherBean nowWeatherObj = JsonUtil.handleHFNowWeatherResponse(responseData);
                if(nowWeatherObj != null && "ok".equals(nowWeatherObj.getHeWeather6().get(0).getStatus())){
                    if(isRunForeground("MainActivity")){
                        Intent intent = new Intent("com.example.myapplication.UPDATE_WEATHER");
                        intent.putExtra("weather_now",responseData);
                        sendBroadcast(intent);
                    }else{
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(UpdateService.this).edit();
                        editor.putString("weather_now",responseData);
                        editor.putLong("weather_time", DateUtil.getRealTime());
                        editor.apply();
                    }
                }
            }
        });
        HttpUtil.sendOkHttpRequest(hf_forecastWeatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("sendOkHttpRequest",e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                HFForecastWeatherBean forecastWeatherObj = JsonUtil.handleHFForecastWeatherResponse(responseData);
                if(forecastWeatherObj != null && "ok".equals(forecastWeatherObj.getHeWeather6().get(0).getStatus())){
                    if(isRunForeground("MainActivity")){
                        Intent intent = new Intent("com.example.myapplication.UPDATE_WEATHER");
                        intent.putExtra("weather_forecast",responseData);
                        sendBroadcast(intent);
                    }else{
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(UpdateService.this).edit();
                        editor.putString("weather_forecast",responseData);
                        editor.putLong("weather_time", DateUtil.getRealTime());
                        editor.apply();
                    }
                }
            }
        });
    }
}
