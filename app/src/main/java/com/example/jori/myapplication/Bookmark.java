package com.example.jori.myapplication;

/**
 * Created by Jori on 2015-02-21.
 */
public class Bookmark {
    private String comName;
    private String comAddr;
    private String activity;

    public Bookmark(String comName,String comAddr,String activity){
        super();
        this.comName = comName;
        this.comAddr = comAddr;
        this.activity = activity;
    }

    public String getComName(){ return comName; }
    public String getComAddr(){ return comAddr; }
    public String getActivity(){ return activity;}
}
