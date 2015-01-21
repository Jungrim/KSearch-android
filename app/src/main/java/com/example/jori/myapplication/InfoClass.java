package com.example.jori.myapplication;

import android.graphics.drawable.Drawable;

public class InfoClass {
    public String title;
    public Drawable image;
    public String button;

    public InfoClass() { }

    public InfoClass(String title, Drawable image, String button){
        this.title = title;
        this.image = image;
        this.button = button;
    }
}
