package com.example.jori.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;


public class ExamActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private String[] bigList;
    private Spinner bigSpinner;
    private Spinner middleSpinner;
    private Button searchButton;
    private BigMiddleConnect[] dataList;
    private String selectBigname;
    private String selectMiddlename;
    private ListView resultView;
    private resultData[] results;
    private BigAdapterTask bigAdapTask;
    private MiddleAdapterTask middleAdapTask;
    private ResultTask resultAdapTask;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_exam);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer_exam,
                (DrawerLayout) findViewById(R.id.drawer_layout_exam));
        bigSpinner = (Spinner)findViewById(R.id.big_spinner);
        middleSpinner = (Spinner)findViewById(R.id.middle_spinner);
        searchButton = (Button)findViewById(R.id.search_button);
        searchButton.setOnClickListener(new searchButtonListener());
        bigAdapTask = new BigAdapterTask();
        bigAdapTask.execute();
        resultView = (ListView) findViewById(R.id.result_view);
    }


    public class searchButtonListener implements View.OnClickListener {
        public void onClick(View v){
            //Toast.makeText(getApplicationContext(), selectBigname+selectMiddlename, Toast.LENGTH_SHORT).show();
            resultAdapTask = new ResultTask();
            resultAdapTask.execute();
        }
    }

    public class MiddleAdapterListener implements AdapterView.OnItemSelectedListener{
        int id;

        public MiddleAdapterListener(int id){
            this.id = id;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int position, long Id){
            selectMiddlename = new String(dataList[this.id].getMiddleName(position));
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class ResultTask extends AsyncTask<Void, Void, Void> {
        ArrayAdapter resultAdapter;
        protected Void doInBackground(Void... voids){
            makeSearchQuery();
            resultAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1);
            for(int i=0;i<results.length;i++){
                resultAdapter.add(results[i].getData());
            }
            this.cancel(true);
            return null;
        }
        protected void onPostExecute(Void id) {
            resultView.setAdapter(resultAdapter);
//            for(int i=0;i<results.length;i++){
//                resultAdapter.add(results[i].getData());
//            }
        }
    }

    private class MiddleAdapterTask extends AsyncTask<Integer, Void, Integer>{
        protected Integer doInBackground(Integer... ids){
            dataList[ids[0]].setMiddleList();
            return ids[0];
        }
        protected void onPostExecute(Integer id){

            ArrayAdapter middleAdapter = new myAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, dataList[id].getMiddleList());
            middleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            middleSpinner.setAdapter(middleAdapter);
            middleSpinner.setOnItemSelectedListener(new MiddleAdapterListener(id));
            this.cancel(true);
        }
    }

    private class BigAdapterTask extends AsyncTask<String, Void, String> {
        ArrayAdapter bigAdapter;
        @Override
        protected String doInBackground(String... str) {
            makeDataList();
            bigAdapter = setBigAdapter();
            return "";
        } // doInBackground : 백그라운드 작업을 진행한다.

        @Override
        protected void onPostExecute(String result) {
            //makeDataList에서 만들어진 bigList를 apdater를 통해 spinner에 입력
            bigSpinner.setAdapter(bigAdapter);
            bigSpinner.setOnItemSelectedListener(new bigItemSelectedListener());
            this.cancel(true);
//            bigSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
//                    for(int i=0;i<dataList.length;i++){
//                        if(bigList[position].equals(dataList[i].getBigname())) {
//
//                            middleAdapTask = new MiddleAdapterTask();
//                            middleAdapTask.execute(i);
//                            selectBigname = new String(bigList[position]);
//                        }
//                    }
//                }
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
            return;
        }

    }
    private class bigItemSelectedListener implements AdapterView.OnItemSelectedListener{
        public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
            for(int i=0;i<dataList.length;i++){
                if(bigList[position].equals(dataList[i].getBigname())) {

                    middleAdapTask = new MiddleAdapterTask();
                    middleAdapTask.execute(i);
                    selectBigname = new String(bigList[position]);
                }
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
    private class myAdapter extends ArrayAdapter<String> {
        Context context;
        String[] items = new String[] {};

        public myAdapter(final Context context,
                         final int textViewResourceId, final String[] objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
            this.context = context;
        }

        /**
         * 스피너 클릭시 보여지는 View의 정의
         */
        @Override
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
            tv.setTextSize(12);
            tv.setHeight(50);
            return convertView;
        }

        /**
         * 기본 스피너 View 정의
         */
        @Override
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
            tv.setTextSize(12);
            tv.setHeight(50);

            tv.setTextSize(12);
            return convertView;
        }
    }

    private class BigMiddleConnect{
        String bigId;
        String bigname;
        String[] middleList;
        String middleStr = "";

        public BigMiddleConnect(String bigname,String bigId){
            this.bigname = bigname;
            this.bigId = bigId;
        }

        public void setMiddleList() {
            String inserviceUrl = "http://ibtk.kr/examAdmitClassification_api/";
            String inserviceKey = "83fc4f9cdce366f892bb6cc993e4207d?";
            String query = "model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"middleid\",direction:1}]}&model_query_distinct=middlename&model_query={\"bigid\":\""+this.bigId+"\"}";
            String instrUrl = inserviceUrl + inserviceKey + query;

            try {
                String inline = getDataFromUrl(instrUrl);
                JSONObject injson = new JSONObject(inline);
                JSONArray injArr = injson.getJSONArray("content");
//                middleList = new String[injArr.length()];

                for (int j = 0; j < injArr.length(); j++) {
                    injson = injArr.getJSONObject(j);
                    String middleName = injson.getString("middlename");
                    //System.out.println(middleName);
                    if(middleName.length() ==0)
                        continue;
                    middleStr = middleStr + middleName + ",";
                }

                StringTokenizer st = new StringTokenizer(middleStr,",");
                middleList = new String[st.countTokens()];
                int i = 0;
                while(st.hasMoreTokens()){
                    String tmp = st.nextToken();
                    middleList[i++] = new String(tmp);
                }
            } catch (Exception e){
                return;
                //return "middleList" + e.toString();
            }
        }

        public String getBigname(){
            return bigname;
        }

        public String[] getMiddleList(){
            return middleList;
        }
        public String getMiddleName(int id){
            return middleList[id];
        }
        public String getMiddleListTostring(){
            String data = "";
            for(int i=0;i<middleList.length;i++)
                data += middleList[i];
            return data;
        }

        public String getBigId(){
            return bigId;
        }
    }

    private class resultData {
        String companyName;
        String addr;
        String range;

        public resultData(String companyName,String addr,String range){
            this.companyName = companyName;
            this.addr = addr;
            this.range = range;
        }

        public String getCompanyName(){
            return companyName;
        }
        public String getData(){
            return companyName + "\n" + range + "\n";
        }
        public String getAddr(){
            return addr;
        }
    }

    public void makeDataList() {
        //API URL로 부터 bigname을 가져와서 bigList에 입력
        String serviceUrl = "http://ibtk.kr/examAdmitClassification_api/";
        String serviceKey = "83fc4f9cdce366f892bb6cc993e4207d?";
        String query = "model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"bigid\",direction:1}]}&model_query_distinct=bigid";
        String strUrl = serviceUrl + serviceKey + query;

        try {
            String line = getDataFromUrl(strUrl);
            JSONObject json = new JSONObject(line);
            JSONArray jArr = json.getJSONArray("content");
            bigList = new String[jArr.length()];
            dataList = new BigMiddleConnect[jArr.length()];
            for (int i = 0; i < jArr.length(); i++) {
                //JSONArray jARR로 부터 bigname과 bigid에 해당하는 data를 읽어옴
                json = jArr.getJSONObject(i);
                String bigName = json.getString("bigname");
                String bigId = json.getString("bigid");
                bigList[i] = new String(bigName);
                dataList[i] = new BigMiddleConnect(bigName,bigId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDataFromUrl(String url) throws IOException {
        // HttpURLConnection을 사용해서 주어진 URL에 대한 입력 스트림을 얻는다.
        //얻어진 입력스트림을 한줄씩 읽어서 page에 저장하고 return한다.
        HttpURLConnection conn = null;
        System.out.println(url);
        try {
            URL u = new URL(url);
            conn = (HttpURLConnection)u.openConnection();
            conn.setDoOutput(false);
            BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf,"utf-8"));

            String line = null;
            String page = "";
            while((line = bufreader.readLine()) != null){
                page += line;
            }
            System.out.println(page);
            return page;
        } finally{

            conn.disconnect();
        }
    }

    public void makeSearchQuery(){
        String searchUrl = "http://ibtk.kr/examAdmitDetail_api/";
        String searchUrlKey = "790f112628bf15b344699602ef729cb1?";
        String searchquery = "model_query_pageable.enable=true&model_query_distinct=companyno&model_query={\"bigname\":\"" + selectBigname + "\",\"middlename\":\"" + selectMiddlename + "\"}";
        String instrUrl = searchUrl + searchUrlKey + searchquery;

        try {
            String inline = getDataFromUrl(instrUrl);
            System.out.println("line : "+inline);
            JSONObject injson = new JSONObject(inline);
            JSONArray injArr = injson.getJSONArray("content");
            results = new resultData[injArr.length()];

            for (int j = 0; j < injArr.length(); j++) {
                injson = injArr.getJSONObject(j);
                String companyName = injson.getString("company");
                //System.out.println(middleName);
                String addr = injson.getString("corporateaddr");
                String range = injson.getString("rangepower");
                results[j] = new resultData(companyName,addr,range);
                System.out.println(results[j].getCompanyName()+results[j].getAddr());
            }
        } catch (Exception e){
            System.out.println("makeQuery" + e.toString());
            return;
            //return "middleList" + e.toString();
        }
    }

    public ArrayAdapter setBigAdapter() {
        ArrayAdapter tmpAdapter = new myAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, bigList);
        tmpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return tmpAdapter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exam, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
