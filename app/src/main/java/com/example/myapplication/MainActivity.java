package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.myapplication.myClass.MyApplication;
import com.example.myapplication.myClass.MyLocationListerner;
import com.example.myapplication.net.*;

import com.example.myapplication.service.UpdateService;
import com.example.myapplication.utils.DateUtil;
import com.example.myapplication.utils.HttpUtil;
import com.example.myapplication.utils.JsonUtil;
import com.example.myapplication.bean.HFForecastWeatherBean;
import com.example.myapplication.bean.HFNowWeatherBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //界面数据id
    private TextView real_time, num_temperature, situation_weather, range_temperature, weather, wind,city_now;
    private ImageView pic_weather,location_list,menu_more;
    private String location_name; //当前城市地点
    private MyApplication app; //应用程序MyApplication
    private List<Intent> serviceListIntent=new ArrayList<>();  //存储服务列表Intent对象
    private SwipeRefreshLayout swipeRefresh; //下拉刷新
    private IntentFilter intentFilter;
    private WeatherUpdateReceiver weatherUpdateReceiver;  //广播接受
    private LinearLayout forecastLayout;

    public LocationClient locationClient=null;
    private MyLocationListerner myLocationListerner=new MyLocationListerner();
    private ImageView positioning;

    //private Handler handler = new Handler();
//    private Runnable task =new Runnable() {
//        public void run() {
//            handler.postDelayed(this,1800*1000);//设置延迟时间，此处是5秒
//            //需要执行的代码
//            getNowWeatherData(location_code);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= 21){  //判断系统版本号是否大于5.0
            View decorView = getWindow().getDecorView(); //获取当前活动的DecorView
            //改变系统UI的显示，下面参数表示活动的布局会显示在状态栏上面
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //将状态栏设置成透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.weather);
        initView();
        loadLastLocation();
        app = (MyApplication)getApplication();//获得我们的应用程序MyApplication
        //handler.postDelayed(task,5000);//延迟调用
//        handler.post(task);//立即调用
        location_list.setOnClickListener(this);
        positioning.setOnClickListener(this);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                getNowWeatherData(app.getLocation_code());
                swipeRefresh.setRefreshing(false);
            }
        });
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.myapplication.UPDATE_WEATHER"); //就收所有值为com.example.myapplication.UPDATE_WEATHER的广播
        weatherUpdateReceiver = new WeatherUpdateReceiver();
        registerReceiver(weatherUpdateReceiver,intentFilter);
        Intent updateService = new Intent(this, UpdateService.class);
        serviceListIntent.add(updateService);
        serviceStart();//启动服务
        locationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        locationClient.registerLocationListener(myLocationListerner);
        getPermissionMethod();
    }
    // 权限请求
    private void getPermissionMethod() {
        List<String> permissionList = new ArrayList<>();

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!permissionList.isEmpty()){ //权限列表不是空
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else{
            requestLocation();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for (int result:grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "必须统一所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else
                {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    //开启 start 定位，默认只启动一次，需要自己设置间隔次数
    private void requestLocation() {
        initLocation();//其他请求设置
    }
    //配置定位sdk参数
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);  // 定位模式是仅限设备模式，也就是仅允许GPS来定位。
        option.setScanSpan(2000);
        locationClient.setLocOption(option);
    }

    @Override
    protected void onStart() {
        super.onStart();
        app.setActivity_name(getClass().getSimpleName());
        location_name=app.getLoc_name();
        city_now.setText(location_name);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weather_now = sharedPreferences.getString("weather_now",null);
        String weather_forecast = sharedPreferences.getString("weather_forecast",null);
        Long weather_time = sharedPreferences.getLong("weather_time",0);
        String city_code = sharedPreferences.getString("cityCode",null);
        if(weather_now != null && weather_forecast!=null &&weather_time!=0 && city_code!=null && city_code.equals(app.getLocation_code())
                &&(DateUtil.getBetweenMinutes(weather_time,DateUtil.getRealTime()))<= 30){
            Log.d("UpdateWeather","加载天气数据缓存");
            HFNowWeatherBean nowWeatherObj = JsonUtil.handleHFNowWeatherResponse(weather_now);
            HFForecastWeatherBean forecastWeatherObj = JsonUtil.handleHFForecastWeatherResponse(weather_forecast);
            showWeatherInfoByROUT(nowWeatherObj);
            showWeatherInfoByROUT(forecastWeatherObj);
        }else {
            Log.d("UpdateWeather","网络获取天气数据");
            getNowWeatherData(app.getLocation_code());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Intent intent = getIntent();
//        System.out.println(111);
//        //如果从choose活动返回会用下面代码获取数据
//        System.out.println(intent != null );
//        System.out.println(intent.getStringExtra("location_name_choose"));
//        if(intent != null && intent.getStringExtra("location_name_choose") != null){
//            System.out.println(222);
//            location_name = intent.getStringExtra("location_name_choose");
//        }

    }

    //想要在 singleTask 模式下获取Intent,重写 onNewIntent() ，在 onStart() 中获取 getIntent()；
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 从这里传回来的 intent 就是新的 intent
        // 将 onNewIntent 接收的 intent 设置给 Activity
        setIntent(intent);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.location_list:
                Intent intent = new Intent(MainActivity.this, LocationManagementActivity.class);
                intent.putExtra("location_name_main",location_name);
                startActivityForResult(intent,1);//第二个参数请求码标识是哪个请求
                break;
            case R.id.positioning:
                if(locationClient.isStarted()){
                    locationClient.stop();
                }
                locationClient.start();
                final Handler BDHandler=new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        switch (msg.what){
                            case 1:
                                if(msg.obj != null){
                                    if(NetState.isNetWorkCAvailable(getApplicationContext())){
                                        location_name = myLocationListerner.cityName;
                                        city_now.setText(location_name);
                                        app.setLocation_code(myLocationListerner.cityCode);
                                        getNowWeatherData(app.getLocation_code());
                                    }else {
                                        Toast.makeText(MainActivity.this,"网络出错",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                myLocationListerner.cityCode=null;
                                locationClient.stop();
                                break;
                            default:
                                break;
                        }
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (myLocationListerner.cityCode==null){
                                Thread.sleep(2000);
                            }
                            Message msg=new Message();
                            msg.what=1;
                            msg.obj=myLocationListerner.cityCode;
                            BDHandler.sendMessage(msg);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    location_name = data.getStringExtra("location_name_manage");
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = getSharedPreferences("LastLocation",MODE_PRIVATE).edit();
        editor.putString("location_name",location_name);
        editor.putString("location_code",app.getLocation_code());
        editor.apply();
        app.getDb().close();//关闭数据库链接
        //serviceStop(); //服务依赖应用进程，应用进程被终止，服务也就销毁了，不必做这个
        unregisterReceiver(weatherUpdateReceiver); //动态注册的广播接收器必须取消注册
    }

    //初始化控件
    private void initView(){
        real_time = (TextView) findViewById(R.id.real_time);
        num_temperature = (TextView)findViewById(R.id.num_temperature);
        situation_weather = (TextView)findViewById(R.id.situation_weather);
        //range_temperature =(TextView)findViewById(R.id.range_temperature);
        //weather = (TextView)findViewById(R.id.weather);
        wind = (TextView)findViewById(R.id.wind);
        city_now = (TextView)findViewById(R.id.city_now);
        pic_weather =(ImageView) findViewById(R.id.pic_weather);
        location_list = (ImageView)findViewById(R.id.location_list);
        menu_more = (ImageView)findViewById(R.id.positioning);
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.refresh_am);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        positioning=(ImageView) findViewById(R.id.positioning);
    }
    //每当接收到Update服务的广播会调用onReceive方法更新UI
    class WeatherUpdateReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("weather_now")!=null){
                HFNowWeatherBean nowWeatherObj = JsonUtil.handleHFNowWeatherResponse(intent.getStringExtra("weather_now"));
                showWeatherInfoByROUT(nowWeatherObj);
            }
            if(intent.getStringExtra("weather_forecast")!=null){
                HFForecastWeatherBean forecastWeatherObj = JsonUtil.handleHFForecastWeatherResponse(intent.getStringExtra("weather_forecast"));
                showWeatherInfoByROUT(forecastWeatherObj);
            }
        }
    }

    //加载用户退出前访问的城市
    private void loadLastLocation(){
        SharedPreferences sharedPreferences = getSharedPreferences("LastLocation",MODE_PRIVATE);
        location_name = sharedPreferences.getString("location_name","北京");
        Log.d("LoadingLocation",location_name);
    }
    //好的启动活动的方式
    public static void actionStart(Context context,String location_name){
        Intent intent = new Intent(context,MainActivity.class);
       // intent.putExtra("location_name_choose",location_name);
        context.startActivity(intent);
    }

    //启动服务
    private void serviceStart(){
        for (Intent serviceIntent:serviceListIntent) {
            startService(serviceIntent);
        }
    }
    //停止服务
    private void serviceStop(){
        for (Intent serviceIntent:serviceListIntent) {
            stopService(serviceIntent);
        }
    }

    private void getNowWeatherData(final String cityCode){
        // 检查网络状态
        if(!NetState.isNetWorkCAvailable(this)){
            Log.d("net","网络不可用");
            return;
        }
        String tq_weatherUrl = "https://www.tianqiapi.com/api/?version=v1&cityid="+cityCode+"&appid=[64417545]&appsecret=[Tbh23JxX]";
        final String hf_nowWeatherUrl = "https://free-api.heweather.net/s6/weather/now?location="+cityCode+"&key=eba7a23ca1154d268627eb892eb15527";
        final String hf_forecastWeatherUrl = "https://free-api.heweather.net/s6/weather/forecast?location="+cityCode+"&key=eba7a23ca1154d268627eb892eb15527";
        new Thread(new Runnable() {
            @Override
            public void run() {
                String responseData = HttpUtil.HttpGet(hf_nowWeatherUrl);
                HFNowWeatherBean nowWeatherObj = JsonUtil.handleHFNowWeatherResponse(responseData);
                responseData = HttpUtil.HttpGet(hf_forecastWeatherUrl);
                HFForecastWeatherBean forecastWeatherObj = JsonUtil.handleHFForecastWeatherResponse(responseData);
                if(nowWeatherObj != null && "ok".equals(nowWeatherObj.getHeWeather6().get(0).getStatus())){
                    showWeatherInfoByROUT(nowWeatherObj);
                }
                if(forecastWeatherObj != null && "ok".equals(forecastWeatherObj.getHeWeather6().get(0).getStatus())){
                    showWeatherInfoByROUT(forecastWeatherObj);
                }
            }
        }).start();
    }


    /*
    使用runOnUiThread()方法将线程切换到主线程，然后更新UI界面
     */
    private void showWeatherInfoByROUT(final HFNowWeatherBean weatherBean){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(HFNowWeatherBean.HeWeather6Bean weatherList : weatherBean.getHeWeather6()){
                    real_time.setText(DateUtil.getTime());
                    num_temperature.setText(weatherList.getNow().getTmp());
                    situation_weather.setText(weatherList.getNow().getCond_txt());
                    wind.setText("      风力"+weatherList.getNow().getWind_sc()+"级");
                    String pic_name = "p"+weatherList.getNow().getCond_code();
                    int resID = getResources().getIdentifier(pic_name, "drawable", "com.example.myapplication");
                    pic_weather.setImageResource(resID);
                }
            }
        });
    }
//    private void showWeatherInfoByROUT(final HFForecastWeatherBean weatherBean){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                for(HFForecastWeatherBean.HeWeather6Bean weatherList : weatherBean.getHeWeather6()){
//                    range_temperature.setText(weatherList.getDaily_forecast().get(0).getTmp_min()+"～ "+weatherList.getDaily_forecast().get(0).getTmp_max()+"℃");
//                    wind.setText(weatherList.getDaily_forecast().get(0).getWind_dir()+weatherList.getDaily_forecast().get(0).getWind_sc()+"级");
//
//                }
//            }
//        });
//    }
    private void showWeatherInfoByROUT(final HFForecastWeatherBean weatherBean){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HFForecastWeatherBean.HeWeather6Bean weatherList=weatherBean.getHeWeather6().get(0);
                forecastLayout.removeAllViews();
                for(HFForecastWeatherBean.HeWeather6Bean.DailyForecastBean fwList : weatherList.getDaily_forecast()){
                    View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.forecast_item,forecastLayout,false);
                    TextView dateText= (TextView) view.findViewById(R.id.date_text);
                    TextView infoText=(TextView)view.findViewById(R.id.info_text);
                    ImageView picImage=(ImageView)view.findViewById(R.id.pic_image);
                    TextView temperText=(TextView)view.findViewById(R.id.temper_text);
                    dateText.setText(fwList.getDate());
                    infoText.setText(fwList.getCond_txt_d());
                    String pic_name = "p"+fwList.getCond_code_d();
                    int resID = getResources().getIdentifier(pic_name, "drawable", "com.example.myapplication");
                    picImage.setImageResource(resID);
                    temperText.setText(fwList.getTmp_min()+"℃～ "+fwList.getTmp_max()+"℃");
                    forecastLayout.addView(view);
                }
            }
        });
    }
}
