package com.example.jori.myapplication;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Jori on 2015-01-19.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancedState){
        return inflater.inflate(R.layout.activity_exam,container,false);


    }

    public void setText (String item){
        TextView tv = (TextView)getView().findViewById(R.id.exam_text);
        tv.setText(item);
    }
}

