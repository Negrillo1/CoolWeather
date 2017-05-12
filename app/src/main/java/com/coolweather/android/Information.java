package com.coolweather.android;

/**
 * Created by Administrator on 2017/4/25.
 */
public class Information {
    private String name;
    private String number;
    public Information(String name, String number){
        this.name=name;
        this.number=number;
    }
    public String getName(){
        return name;
    }
    public String getNumber(){
        return number;
    }
}
