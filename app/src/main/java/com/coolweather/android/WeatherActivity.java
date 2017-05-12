package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.dreams.DreamService;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.LitePal;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private  String id;
    int x=1;
    private ImageView bingPicImg;
    public DrawerLayout drawerLayout;
    private Button navButtom;
    private Button preButton;
    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;
    //private String weatherId;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private Button remind;
    private TextView dateText;
    private TextView infoText;
    private TextView maxText;
    private TextView minText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        titleCity = (TextView) findViewById(R.id.title_city);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButtom = (Button) findViewById(R.id.nav_button);
        preButton = (Button) findViewById(R.id.pre);
        remind = (Button) findViewById(R.id.remind);


        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WeatherActivity.this, Preview.class);
                startActivity(intent);


            }
        });
        remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = 6;
                Uri send = Uri.parse("smsto:");
                Intent intent = new Intent(Intent.ACTION_SENDTO, send);

                if (weatherInfoText.getText().toString().equals("强阵雨")) {
                    intent.putExtra("sms_body", "今天有强阵雨,记得带伞哦！");
                }
                if (weatherInfoText.getText().toString().equals("雷阵雨")) {
                    intent.putExtra("sms_body", "今天有雷阵雨,记得带伞哦！");
                }
                if (weatherInfoText.getText().toString().equals("强雷阵雨")) {
                    intent.putExtra("sms_body", "今天有强雷阵雨,记得带伞哦！");
                }
                if (weatherInfoText.getText().toString().equals("雷阵雨伴有冰雹")) {
                    intent.putExtra("sms_body", "今天有强雷阵雨,出门要注意安全！");
                }
                if (weatherInfoText.getText().toString().equals("小雨")) {
                    intent.putExtra("sms_body", "今天有小雨,出门要记得带伞哦！");
                }
                if (weatherInfoText.getText().toString().equals("中雨")) {
                    intent.putExtra("sms_body", "今天有中雨,出门要记得带伞哦！");
                }
                if (weatherInfoText.getText().toString().equals("大雨")) {
                    intent.putExtra("sms_body", "今天有大雨，出门要记得带伞哦！");
                }
                if (weatherInfoText.getText().toString().equals("暴雨")) {
                    intent.putExtra("sms_body", "今天有暴雨，出门要记得带伞哦！");
                }
                if (weatherInfoText.getText().toString().equals("大暴雨")) {
                    intent.putExtra("sms_body", "今天有大暴雨，出门要记得带伞哦！");
                }
                if (weatherInfoText.getText().toString().equals("特大暴雨")) {
                    intent.putExtra("sms_body", "今天有特大暴雨，出门要记得带伞哦！");
                }
                if (weatherInfoText.getText().toString().equals("阵雨")) {
                    intent.putExtra("sms_body", "今天有阵雨,记得带伞哦！");
                }
                String n = degreeText.getText().toString();
                n = n.replace("℃", "");
                int N = Integer.parseInt(n);
                if (N > 30) {
                    intent.putExtra("sms_body", "今天气温高，注意防暑哦");
                }
                startActivity(intent);
            }
        });
        navButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic !=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }


    protected void onResume(){
        super.onResume();
        SharedPreferences pre=getSharedPreferences("id",MODE_PRIVATE);
        String id=pre.getString("weatherid"," ");

        if(id!=null){
            requestWeather(id);
        }

    }

    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=083f80b78fd249729e49e0a21b4a33d6";
        //this.weatherId=weatherId;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            mWeatherId=weather.basic.weatherId;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();

        if(weather!=null&&"ok".equals(weather.status)){
            Intent intent=new Intent(this, AutoUpdateService.class);
            startService(intent);
        }
        else{
            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
        }
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            dateText = (TextView) view.findViewById(R.id.date_text);
            infoText = (TextView) view.findViewById(R.id.info_text);
            maxText = (TextView) view.findViewById(R.id.max_text);
            minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);

        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort= "舒适度:" + weather.suggestion.comfort.info;
        String carWash= "洗车指数:" + weather.suggestion.carWash.info;
        String sport= "运动建议:" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
