package com.coolweather.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.coolweather.android.db.preview;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Preview extends AppCompatActivity {

    private PreviewAdapter previewAdapter;
    private ListView prelist;
    public DrawerLayout drawerLayout;
    private Button addcity;
    private Button back;
    private List<PreviewItem> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        prelist=(ListView)findViewById(R.id.prelist);
        previewAdapter=new PreviewAdapter(Preview.this,R.layout.preview_item,list);
        prelist.setAdapter(previewAdapter);
        addcity=(Button)findViewById(R.id.add);
        back = (Button) findViewById(R.id.back);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout2);
   ;
        prelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PreviewItem v=list.get(position);
                String city=v.getCityName();
                List<preview>n=DataSupport.where("cityname=?",city).find(preview.class);
                for(preview p:n){

                    String weatherId=p.getWeatherId();
                    SharedPreferences pre=Preview.this.getSharedPreferences("id",MODE_PRIVATE);
                    SharedPreferences.Editor editor=pre.edit();
                    editor.putString("weatherid",weatherId);
                    editor.apply();
                    Preview.this.finish();
                }

            }
        });
        prelist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                view.setBackgroundResource(R.color.lightyellow);
                AlertDialog.Builder builder=new AlertDialog.Builder(Preview.this);
                builder.setMessage("确认删除吗");
                builder.setTitle("提示");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.setBackgroundResource(R.color.white);
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PreviewItem pre=list.get(position);
                        String cityname=pre.getCityName();
                        DataSupport.deleteAll(preview.class,"cityname=?",cityname);
                        list.remove(position);
                        previewAdapter.notifyDataSetChanged();

                    }
                });
                builder.create().show();
                return true;
            }
        });
        addcity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Preview.this,WeatherActivity.class);
                startActivity(intent);
            }
        });
        List<preview>ps=DataSupport.findAll(preview.class);
        list.clear();
        for (preview p:ps){
            requestWeather2(p.getWeatherId());
            String name=p.getCityname();
            String degree=p.getDegree();
            PreviewItem n=new PreviewItem(name,degree);
            list.add(n);
        }
        previewAdapter.notifyDataSetChanged();
    }
    public void requestWeather(final String weatherId){
        String weatherUrl="http://guolin.tech/api/weather?cityid=" + weatherId + "&key=083f80b78fd249729e49e0a21b4a33d6";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Preview.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseText=response.body().string();
                final Weather weather= Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(Preview.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    public void requestWeather2(final String weatherId){
        String weatherUrl="http://guolin.tech/api/weather?cityid=" + weatherId + "&key=083f80b78fd249729e49e0a21b4a33d6";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Preview.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseText=response.body().string();
                final Weather weather= Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            setupdegree(weather);
                           // String degree;
                            //preview p=new preview();
                            //degree=weather.now.temperature+"℃";
                            //p.setDegree(degree);
                        }else{
                            Toast.makeText(Preview.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    private void showWeatherInfo(Weather weather){
        int i=0;
        String weatherid=weather.basic.weatherId;
        String cityName=weather.basic.cityName;
        String degree=weather.now.temperature+"℃";
        PreviewItem n=new PreviewItem(cityName,degree);
        List<preview>ps=DataSupport.findAll(preview.class);
        for(preview s:ps){
            if(s.getCityname().equals(cityName)){
                    i=1;
            }
            else{
                i=0;
            }
        }
        if(i==0)
        { preview p=new preview();
            p.setCityname(cityName);
            p.setDegree(degree);
            p.setWeatherId(weatherid);
            p.save();
            list.add(n);
        }
        if(i==1){
            Toast.makeText(Preview.this,"请勿添加相同的城市",Toast.LENGTH_SHORT).show();
        }

        previewAdapter.notifyDataSetChanged();
    }
    private void setupdegree(Weather weather){
        String degree=weather.now.temperature+"℃";
        String cityname=weather.basic.cityName;
        preview p=new preview();
        p.setDegree(degree);
        p.updateAll("cityname=?",cityname);
    }

}
