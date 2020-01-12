package com.example.myapplication.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HttpUtil {
    //使用HttpURLConnection
    public static String HttpGet(final String request_url){
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(request_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            //获取响应状态
            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader reader =new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while((line=reader.readLine()) != null){
                    response.append(line);
                }
                if(reader != null){
                    try {
                        reader.close();
                    }catch (IOException e){
                        Log.d("HttpGet同步请求关闭reader异常",e.toString());
                    }
                }
            }
            if(connection != null)
                connection.disconnect();
        }catch (Exception e){
            Log.d("HttpGet同步请求异常",e.toString());
        }
        return response.toString();
    }
    //通过OkHttp方式
    public static String OkHttpGet(final String request_url){
        //创建OKHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        try {
            //创建一个Request
            Request request = new Request.Builder().url(request_url).build();
            Response response = okHttpClient.newCall(request).execute();
            String responseData = response.body().string();
            return responseData;
        }catch (IOException e){
            Log.d("OkHttpGet同步请求异常",e.toString());
        }
        return null;
    }
    //通过OkHttp的回调函数方式
    public static void sendOkHttpRequest(String request_url, Callback callback) {
        //创建OKHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建一个Request
        Request request = new Request.Builder().url(request_url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
