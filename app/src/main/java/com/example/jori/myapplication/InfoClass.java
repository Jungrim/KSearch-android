package com.example.jori.myapplication;

import android.graphics.drawable.Drawable;

public class InfoClass {
    private String company;
    private String accreditNumber;
    private String addr;
    private String activityName;
    public Drawable image;
    public String button;

    public InfoClass() { }

    public InfoClass(String accreditNumber, String company, String addr,String activityName){
        this.accreditNumber = accreditNumber;
        this.company = company;
        this.addr = addr;
        this.activityName = activityName;
    }

    public String getCompany(){
        return company;
    }

    public String getAccreditNumber(){
        return accreditNumber;
    }

    public String getAddr(){
        return addr;
    }

    public String getActivityName() {
        return activityName;
    }
}
