package com.example.jori.myapplication;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Vector;

public class CustomArrayAdapter extends ArrayAdapter<InfoClass> {

    private ViewHolder viewHolder = null;
    private LayoutInflater inflater = null;
    private ArrayList<InfoClass> infoList = null;
    private Context mContext = null;

    public CustomArrayAdapter(Context c, int textViewResourceId,
                              ArrayList<InfoClass> arrays) {
        super(c, textViewResourceId, arrays);
        this.inflater = LayoutInflater.from(c);
        this.mContext = c;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public InfoClass getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {

        View v = convertview;

        if(v == null){
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.list_row, null);
            viewHolder.tv_title = (TextView)v.findViewById(R.id.tv_title);
            viewHolder.iv_image = (ImageView)v.findViewById(R.id.iv_image);
            viewHolder.btn_button = (Button)v.findViewById(R.id.btn_button);
            viewHolder.cb_box = (CheckBox)v.findViewById(R.id.cb_box);

            v.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder)v.getTag();
        }

        viewHolder.tv_title.setText(getItem(position).getAccreditNumber()+getItem(position).getCompany()+getItem(position).getAddr());

        viewHolder.iv_image.setOnClickListener(buttonClickListener);
        viewHolder.iv_image.setTag(position);

        viewHolder.btn_button.setText(getItem(position).button);
        viewHolder.btn_button.setOnClickListener(buttonClickListener);
        viewHolder.btn_button.setTag(position);

        viewHolder.cb_box.setTag(position);
        viewHolder.cb_box.setOnClickListener(buttonClickListener);

        return v;
    }

    public void setArrayList(ArrayList<InfoClass> arrays){
        this.infoList  = arrays;
    }

    public ArrayList<InfoClass> getArrayList(){
        return infoList;
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                // 이미지 클릭
                case R.id.iv_image:
                    Toast.makeText(
                            mContext,
                            "이미지 Tag = " + v.getTag(),
                            Toast.LENGTH_SHORT
                    ).show();
                    break;

                // 버튼 클릭
                case R.id.btn_button:
                    Toast.makeText(
                            mContext,
                            "버튼 Tag = " + v.getTag(),
                            Toast.LENGTH_SHORT
                    ).show();
                    break;

                // CheckBox
                case R.id.cb_box:
                    Toast.makeText(
                            mContext,
                            "체크박스 Tag = " + v.getTag(),
                            Toast.LENGTH_SHORT
                    ).show();
                    break;

                default:
                    break;
            }
        }
    };


    /*
     * ViewHolder
     */
    class ViewHolder{
        public TextView tv_title = null;
        public ImageView iv_image = null;
        public Button btn_button = null;
        public CheckBox cb_box = null;

    }


    @Override
    protected void finalize() throws Throwable {
        free();
        super.finalize();
    }

    private void free(){
        inflater = null;
        infoList = null;
        viewHolder = null;
        mContext = null;
    }
}
