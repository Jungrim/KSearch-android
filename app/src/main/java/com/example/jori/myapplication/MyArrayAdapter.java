package com.example.jori.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by admin on 2015-02-15.
 */
public class MyArrayAdapter extends ArrayAdapter<String> {
    //스피너에 붙는 어레이 어댑터를 만들때 사용되는 클래스로
    //어레이어댑터를 상속하고 TextView를 통해서 폰트의 설정을 변경할 수 있다.
    Context context;
    String[] items = new String[] {};

    public MyArrayAdapter(final Context context,
                     final int textViewResourceId, final String[] objects) {
        super(context, textViewResourceId, objects);
        this.items = objects;
        this.context = context;
    }
    @Override
    //스피너 클릭시 보여지는 View의 정의
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(
                    android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(items[position]);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(15);
        tv.setHeight(50);
        return convertView;
    }

    @Override
    //기본 스피터 View의 정의
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(
                    android.R.layout.simple_spinner_item, parent, false);
        }

        TextView tv = (TextView) convertView
                .findViewById(android.R.id.text1);
        tv.setText(items[position]);
        tv.setText(items[position]);
        tv.setTextColor(Color.BLACK);
//            tv.setTextSize(15);
        tv.setHeight(50);

//            tv.setTextSize(12);
        return convertView;
    }
}