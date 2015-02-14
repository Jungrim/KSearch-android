package com.example.jori.myapplication;

/**
 * Created by admin on 2015-02-14.
 */
public class SmallData {
    String smallName;
    String smallId;

    public SmallData(String smallName,String smallId){
        this.smallId = smallId;
        this.smallName = smallName;
    }

    public String getSmallName(){
        return smallName;
    }

    public String getSmallId(){
        return smallId;
    }
}
