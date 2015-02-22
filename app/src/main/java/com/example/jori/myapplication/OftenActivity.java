package com.example.jori.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class OftenActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private NotesDbAdapter dbAdapter;
    private String activity;
    private ArrayList<Bookmark> bookList;
    private ResultData result;
    private HttpUrlConnect urlConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_often);

        dbAdapter = new NotesDbAdapter(this);
        dbAdapter.open();

        bookList = new ArrayList<Bookmark>();

        Cursor result = dbAdapter.fetchAllNotes();
        result.moveToFirst();
        while (!result.isAfterLast()) {

            String companyName = result.getString(1);
            String companyAddr = result.getString(2);
            activity = result.getString(3);
            bookList.add(new Bookmark(companyName, companyAddr,activity));

            result.moveToNext();
        }
        result.close();

        ListView listview = (ListView)findViewById(R.id.listView);

        listview.setAdapter(new ArrayAdapter<Bookmark>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                bookList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);

                Bookmark book = bookList.get(position);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(book.getComName());
                text2.setText(book.getComAddr());
                return view;
            }

        });
        listview.setOnItemClickListener(new oftenViewListener());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_often);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer_often,
                (DrawerLayout) findViewById(R.id.drawer_layout_often));

    }

    public class oftenViewListener implements ListView.OnItemClickListener {
        //ListView 클릭시의 이벤트
        String companyName;
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Bookmark selectBook = bookList.get(position);
            //선택된 Bookmark의 companyName UTF-8로 incoding
            try{
                companyName = URLEncoder.encode(selectBook.getComName().trim(), "UTF-8");
            } catch (Exception e){
                System.out.println(e.toString());
            }

            //switch문을 통해 어떤 activity인지 검사
            switch(selectBook.getActivity()){
                case "CheckActivity" :
                    makeResultData(1,companyName);
                    System.out.println("Check!");
                    break;
                case "CorrectionActivity" :
                    makeResultData(2,companyName);
                    System.out.println("Correction!");
                    break;
                case "ExamActivity" :
                    makeResultData(3,companyName);
                    System.out.println("Exam!");
                    break;
                case "MaterialActivity" :
                    makeResultData(4,companyName);
                    System.out.println("Material!");
                    break;
            }
        }
    }

    private class makeResultTask extends AsyncTask<String, Void, Void>{
        //ReesultData를 만드는 AsyncTask
        //ResultData가 만들어지면 해당 ResultData로 resultActivity를 호출
        private ProgressDialog dialog;
        private String data;
        @Override
        protected void onPreExecute() {
            //쿼리를 날려 데이터를 만들기 전에 미리 작동되는 부분으로
            //로딩 창을 만들어서 띄워줌
            dialog = new ProgressDialog(OftenActivity.this);
            //          dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("Loading");
            dialog.show();
            super.onPreExecute();
        }

        protected Void doInBackground(String... querys){
            //쿼리를 날려서 데이터를 얻어와서 resultData를 만듬
            urlConnector = new HttpUrlConnect();
            try {
                data = urlConnector.getDataFromUrl(querys[0]);
            }catch(Exception e){
                System.out.println("makeResultTask"+e.toString());
            }
            return null;
        }
        protected void onPostExecute(Void id) {
            //ResultData를 만든 다음 실행되는 부분으로 로딩창을 끝내고
            //ResultActivity를 호출한다.
            dialog.dismiss();
            makeResultClass(data);
            Intent intent = new Intent(OftenActivity.this,ResultActivity.class);
            intent.putExtra("Result",result);
            startActivity(intent);
        }
    }

    public void makeResultClass(String data) {
        //URL로 불러온 data를 파싱해서 ResultData를 만드는 함수
        try {
            JSONObject injson = new JSONObject(data);
            JSONArray injArr = injson.getJSONArray("content");
//            results = new resultData[injArr.length()];
            for (int j = 0; j < injArr.length(); j++) {
                //얻어온 데이터의 회사명, 인증번호, 주소를 얻어와서 ResultData객체를 생성하고
                //생성된 ResultData객체를 results벡터에 저장
                injson = injArr.getJSONObject(j);
                String companyName = injson.getString("company");
                Boolean check = false;

                //System.out.println(middleName);
                String addr = injson.getString("delegateaddr");
                String accreditNumber = injson.getString("accreditnumber");
                String phoneNumber = injson.getString("delegatephone");
                String chargeName = injson.getString("chargename");
                String rangePower = injson.getString("rangepower");

                result = new ResultData(companyName, addr, accreditNumber, phoneNumber, chargeName, rangePower, true, "OfenActivity");
            }
        } catch (Exception e) {
            System.out.println("makeResultClass" + e.toString());
        }
    }

    public void makeResultData(int id,String companyName){
        //id 1 : check, 2: correction, 3:exam, 4:material
        switch(id){
            case 1 ://check
                new makeResultTask().execute(makeReesultQuery(1,companyName));
                break;
            case 2://correction
                new makeResultTask().execute(makeReesultQuery(2,companyName));
                break;
            case 3://exam
                new makeResultTask().execute(makeReesultQuery(3,companyName));
                break;
            case 4://material
                new makeResultTask().execute(makeReesultQuery(4,companyName));
                break;

        }
    }

    public String makeReesultQuery(int id,String companyName){
        //id를 통해 받은 Activity명들을 활용해서 query문을 만들어서 리턴하는 함수
        String url = "";
        String key = "";
        String query = "";

        switch(id){
            case 1://checkactivity
                url = "http://ibtk.kr/inspectionAgencyDetail_api/";
                key = "f1609949fce00ad1504508074bc862c4?";
                query = "model_query_distinct=companyno&model_query={\"company\":\""+companyName+"\"}";
                break;
            case 2://correctionactivity
                url = "http://ibtk.kr/correctionalInstitutionsDetail_api/";
                key = "c40c2656fcdd06180708863c01a3921a?";
                query = "model_query_distinct=companyno&model_query={\"company\":\""+companyName+"\"}";
                break;
            case 3://examactivity
                url = "http://ibtk.kr/examAdmitDetail_api/";
                key = "18bb4a457120eb2a8367e6ecb3e6c540?";
                query = "model_query_distinct=companyno&model_query={\"company\":\""+companyName+"\"}";
                break;
            case 4://materialactivity
                url = "http://ibtk.kr/standardMaterialAgenciesDetail_api/";
                key = "efb8b2c1d5c397733701cfed51f77308?";
                query = "model_query_distinct=companyno&model_query={\"company\":\""+companyName+"\"}";
                break;

        }
        System.out.println(url+key+query);
        return url+key+query;
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if(position==0) {
            Intent i = new Intent(this,CheckActivity.class);
            Bundle b= new Bundle();
            b.putInt("position",position);

            i.putExtras(b);
            startActivity(i);

        }
        else if(position ==1){
            Intent i = new Intent(this,CorrectionActivity.class);
            Bundle b= new Bundle();
            b.putInt("position",position);

            i.putExtras(b);
            startActivity(i);

        }
        else if(position ==2){
            Intent i = new Intent(this,ExamActivity.class);
            Bundle b= new Bundle();
            b.putInt("position",position);

            i.putExtras(b);
            startActivity(i);
        }
        else if(position ==3){
            Intent i = new Intent(this,MaterialActivity.class);
            Bundle b= new Bundle();
            b.putInt("position",position);

            i.putExtras(b);
            startActivity(i);

        }else{
            Intent i = new Intent(this,OftenActivity.class);
            Bundle b = new Bundle();
            i.putExtras(b);
            startActivity(i);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            // do nothing
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_often, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home_button) {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
