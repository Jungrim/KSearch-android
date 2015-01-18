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
// 네비게이션 바에서 화면 전환을 위한 Fragment class이다.

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancedState){
        //layout.activity_exam 부분을 다른 것으로 변경한다. 왜저걸로 했지? 병신인가....
        return inflater.inflate(R.layout.activity_exam,container,false);


    }

    public void setText (String item){

    }
}

