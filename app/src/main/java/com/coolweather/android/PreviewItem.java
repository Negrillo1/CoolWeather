package com.coolweather.android;

/**
 * Created by Administrator on 2017/4/30.
 */
public class PreviewItem {
    private String cityName;
    private String dregree;
    public PreviewItem(String cityName,String dregree){
        this.cityName=cityName;
        this.dregree=dregree;
    }
    public String getCityName(){
        return cityName;
    }
    public String getDregree(){
        return dregree;
    }
}
