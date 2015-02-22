package com.example.jori.myapplication;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomBaseAdapter extends BaseAdapter {

    private LayoutInflater inflater = null;
    private ArrayList<InfoClass> infoList = null;
    private ViewHolder[] viewHolder = null;
    private Context mContext = null;
    private boolean[] checkList;

    private NotesDbAdapter dbAdapter;
    private String comName;
    private String comAddr;
    private String comActivity;

    public CustomBaseAdapter(Context c , ArrayList<InfoClass> arrays, boolean[] checkList){
        this.mContext = c;
        this.inflater = LayoutInflater.from(c);
        this.infoList = arrays;
        this.checkList = checkList;
        viewHolder = new ViewHolder[arrays.size()];
        dbAdapter = new NotesDbAdapter(mContext);
        dbAdapter.open();

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

       // View v = convertview;
        viewHolder[position] = new ViewHolder();
        if(convertview == null){
            convertview = inflater.inflate(R.layout.list_row,null);

            viewHolder[position].tv_title = (TextView)convertview.findViewById(R.id.tv_title);
            viewHolder[position].cb_box = (CheckBox)convertview.findViewById(R.id.cb_box);

            convertview.setTag(viewHolder[position]);

        }else {

            viewHolder[position] = (ViewHolder)convertview.getTag();
        }

        viewHolder[position].tv_title.setTextColor(Color.BLACK);
//        viewHolder.tv_title.setTextSize(12);
        viewHolder[position].tv_title.setLines(3);
//        viewHolder.tv_title.setWidth(500);
        viewHolder[position].tv_title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        viewHolder[position].tv_title.setText(getItem(position).getAccreditNumber()+getItem(position).getCompany()+getItem(position).getAddr());

        // image 나 button 등에 Tag를 사용해서 position 을 부여해 준다.
        // Tag란 View를 식별할 수 있게 바코드 처럼 Tag를 달아 주는 View의 기능
        // 이라고 생각 하시면 됩니다.

        viewHolder[position].cb_box.setChecked(checkList[position]);
        viewHolder[position].cb_box.setTag(position);
//        viewHolder.cb_box.setChecked(false);
//        viewHolder.cb_box.setChecked(((ListView)parent).isItemChecked(position));
        viewHolder[position].cb_box.setOnClickListener(buttonClickListener);

        return convertview;
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
                // CheckBox
                case R.id.cb_box:
                    System.out.println(v.getTag());
                    checkList[Integer.parseInt(v.getTag().toString())] = !checkList[Integer.parseInt(v.getTag().toString())];
                    System.out.println(checkList[Integer.parseInt(v.getTag().toString())]);
                    comName = getItem(Integer.parseInt(v.getTag().toString())).getCompany();
                    comAddr = getItem(Integer.parseInt(v.getTag().toString())).getAddr();
                    comActivity = getItem(Integer.parseInt(v.getTag().toString())).getActivityName();
                    if (checkList[Integer.parseInt(v.getTag().toString())])
                        dbAdapter.createNote(comName,comAddr,comActivity);
                    else
                        dbAdapter.deleteNote(comName);

                    Cursor result = dbAdapter.fetchAllNotes();
                    result.moveToFirst();
                    while (!result.isAfterLast()) {

                        String title = result.getString(1);
                        String body = result.getString(2);
                        System.out.println(title);
                        System.out.println(body);
                        result.moveToNext();
                    }
                    result.close();

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
    public class ViewHolder{
        public TextView tv_title;
        public CheckBox cb_box;
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

