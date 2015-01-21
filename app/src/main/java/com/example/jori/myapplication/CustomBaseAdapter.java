package com.example.jori.myapplication;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomBaseAdapter extends BaseAdapter {

    private LayoutInflater inflater = null;
    private ArrayList<InfoClass> infoList = null;
    private ViewHolder viewHolder = null;
    private Context mContext = null;

    public CustomBaseAdapter(Context c , ArrayList<InfoClass> arrays){
        this.mContext = c;
        this.inflater = LayoutInflater.from(c);
        this.infoList = arrays;
    }

    // Adapter가 관리할 Data의 개수를 설정 합니다.
    @Override
    public int getCount() {
        return infoList.size();
    }

    // Adapter가 관리하는 Data의 Item 의 Position을 <객체> 형태로 얻어 옵니다.
    @Override
    public InfoClass getItem(int position) {
        return infoList.get(position);
    }

    // Adapter가 관리하는 Data의 Item 의 position 값의 ID 를 얻어 옵니다.
    @Override
    public long getItemId(int position) {
        return position;
    }

    // ListView의 뿌려질 한줄의 Row를 설정 합니다.
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

        viewHolder.tv_title.setText(getItem(position).title);

        // image 나 button 등에 Tag를 사용해서 position 을 부여해 준다.
        // Tag란 View를 식별할 수 있게 바코드 처럼 Tag를 달아 주는 View의 기능
        // 이라고 생각 하시면 됩니다.
        viewHolder.iv_image.setTag(position);
        viewHolder.iv_image.setOnClickListener(buttonClickListener);

        viewHolder.btn_button.setTag(position);
        viewHolder.btn_button.setText(getItem(position).button);
        viewHolder.btn_button.setOnClickListener(buttonClickListener);

        viewHolder.cb_box.setTag(position);
        viewHolder.cb_box.setOnClickListener(buttonClickListener);

        return v;
    }

    // Adapter가 관리하는 Data List를 교체 한다.
    // 교체 후 Adapter.notifyDataSetChanged() 메서드로 변경 사실을
    // Adapter에 알려 주어 ListView에 적용 되도록 한다.
    public void setArrayList(ArrayList<InfoClass> arrays){
        this.infoList = arrays;
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
     * getView의 속도 향상을 위해 쓴다.
     * 한번의 findViewByID 로 재사용 하기 위해 viewHolder를 사용 한다.
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

