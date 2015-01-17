package com.example.jori.myapplication;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONObject;
import org.json.JSONArray;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.content.Intent;
import android.widget.Toast;


public class CheckActivity extends ActionBarActivity {
    TextView tv;
    BigMiddleConnect[] partList;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        Intent intent = getIntent();
        String intent_name = intent.getStringExtra("intent_name");
        //tv = (TextView)findViewById(R.id.tv);
        //tv.setText(intent_name);
        spinner = (Spinner)findViewById(R.id.check_spinner);
        final String[] data = {"1","2","3","4","5"};
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id){
                Toast.makeText(getApplicationContext(),data[position],Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });
        new JsonLoadingTask().execute();
    }

    public void makeDataList() {
        //API URL로 부터 Data를 DataList를 만듬
        //추후에 StringBuffer가 아닌 Button에 append하는 식으로 수정

        String serviceUrl = "http://api.ibtk.kr/inspectionRecognize_api/";
        String serviceKey = "48744e4796989e49eef86081ab416116?";
        String query = "model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"bigname\",direction:1}]}&model_query_distinct=bigname";
        String strUrl = serviceUrl + serviceKey + query;

        try {
            String line = getDataFromUrl(strUrl);
            JSONObject json = new JSONObject(line);
            JSONArray jArr = json.getJSONArray("content");

            partList = new BigMiddleConnect[jArr.length()];
            for (int i = 0; i < jArr.length(); i++) {
                //JSONArray jARR로 부터 bigname과 bigid에 해당하는 data를 읽어옴
                json = jArr.getJSONObject(i);
                String bigName = json.getString("bigname");
                String bigId = json.getString("bigid");
                partList[i] = new BigMiddleConnect(bigName,Integer.parseInt(bigId));

                //읽어온 bigid를 통해서 세부분야(middlename)을 읽어와서 parkList에 입력
                serviceUrl = "http://www.ibtk.kr/inspectionAgencyDetail_api/";
                serviceKey = "3f7f0c56c14ad73f3a0534ba2999f4a2?";
                query = "model_query_pageable.enable=true&model_query_distinct=middlename&model_query={\"bigid\":\""+bigId+"\"}";

                String instrUrl = serviceUrl + serviceKey + query;

                String inline = getDataFromUrl(instrUrl);
                JSONObject injson = new JSONObject(inline);
                JSONArray injArr = injson.getJSONArray("content");

                for(int j=0;j<injArr.length();j++){
                    injson = injArr.getJSONObject(j);
                    String middlename = injson.getString("middlename");
                    if(middlename.length()==0){
                        continue;
                    }
                    partList[i].insertMiddle(middlename);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDataFromUrl(String url) throws IOException {
        // HttpURLConnection을 사용해서 주어진 URL에 대한 입력 스트림을 얻는다.
        //얻어진 입력스트림을 한줄씩 읽어서 page에 저장하고 return한다.
        HttpURLConnection conn = null;
        try {

            URL u = new URL(url);
            conn = (HttpURLConnection)u.openConnection();
            BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf,"utf-8"));

            String line = null;
            String page = "";
            while((line = bufreader.readLine()) != null){
                page += line;
            }
            return page;
        } finally{
            conn.disconnect();
        }
    } // getInputStreamFromUrl

    class JsonLoadingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            makeDataList();
            return "";
        } // doInBackground : 백그라운드 작업을 진행한다.
        @Override
        protected void onPostExecute(String result) {
            //makeDataList에서 만들어진 partList를 textView에 입력한다.
            for(int i=0;i<partList.length;i++){
                result += "인정분야 : " + partList[i].getBigname() + "\n";
                result += "세부분야 : " + partList[i].getMiddleList() + "\n\n";
            }
            tv.setText(result);
        }
    }

    public class BigMiddleConnect{
        int bigId;
        String bigname;
        StringBuffer middleList;

        public BigMiddleConnect(String bigname,int bigId){
            middleList = new StringBuffer();
            this.bigname = bigname;
            this.bigId = bigId;
        }

        public void insertMiddle(String middlename){
            middleList.append(middlename).append(",");
        }

        public String getBigname(){
            return bigname;
        }

        public String getMiddleList(){
            return middleList.toString();
        }
        public int getBigId(){
            return bigId;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check, menu);
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
}
