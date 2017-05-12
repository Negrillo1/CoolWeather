package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/5/1.
 */
public class preview extends DataSupport{
    private int id;
    private String cityname;
    private String degree;
    private String weatherId;
    public String getCityname(){
        return cityname;
    }
    public String getDegree(){
        return degree;
    }
    public String getWeatherId(){
        return weatherId;
    }
    public void setCityname(String cityname){
        this.cityname=cityname;
    }
    public void setDegree(String degree){
        this.degree=degree;
    }
    public void setWeatherId(String weatherId){
        this.weatherId=weatherId;
    }
    public void setId(int id){
        this.id=id;
    }
}
