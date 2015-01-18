package com.example.jori.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class CorrectionActivity extends ActionBarActivity {
    TextView tvCorrect;
    BigMiddleConnect[] correctList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correction);
        System.out.println("ㅋㅋ");
        Intent intent = getIntent();
        String intent_name = intent.getStringExtra("intent_name");
        tvCorrect = (TextView)findViewById(R.id.tvCorrect);
        //tv.setText(intent_name);
        new JsonLoadingTask().execute();
    }

    public void makeDataList() {
        //API URL로 부터 Data를 DataList를 만듬
        //추후에 StringBuffer가 아닌 Button에 append하는 식으로 수정

        String serviceUrl = "http://www.ibtk.kr/correctionalClassification_api/";
        String serviceKey = "476febb189d2992a63feba63ba5691cb?";
        String query = "model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"bigid\",direction:1}]}&model_query_distinct=bigid";
        String strUrl = serviceUrl + serviceKey + query;

        try {
            String line = getDataFromUrl(strUrl);
            JSONObject json = new JSONObject(line);
            JSONArray jArr = json.getJSONArray("content");

            correctList = new BigMiddleConnect[jArr.length()];

            for (int i = 0; i < jArr.length(); i++) {
                //JSONArray jARR로 부터 bigname과 bigid에 해당하는 data를 읽어옴
                json = jArr.getJSONObject(i);
                //String bigName = json.getString("bigname");
                String bigId = json.getString("bigid");
                correctList[i] = new BigMiddleConnect(Integer.parseInt(bigId));
                //읽어온 bigid를 통해서 세부분야(middlename)을 읽어와서 partList에 입력
                correctList[i].setMiddleData();
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
    }

    public class BigMiddleConnect {
        int bigId;
        String bigname;
        StringBuffer middleList;
        String serviceUrl = "http://www.ibtk.kr/correctionalClassification_api/";
        String serviceKey = "476febb189d2992a63feba63ba5691cb?";

        public BigMiddleConnect(int bigId) {
            middleList = new StringBuffer();
            this.bigId = bigId;
        }

        public void setMiddleData() {
            //class의 bigid를 통해서 등록된 검사기관들의 middlename을 가져와서
            //StringBuffer인 middleList에 append한다.
            String query = "model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"middleid\",direction:1}]}&model_query_distinct=middleid&model_query={\"bigid\":\"" + bigId + "\"}";

            String strUrl = serviceUrl + serviceKey + query;

            try {
                String line = getDataFromUrl(strUrl);
                JSONObject json = new JSONObject(line);
                JSONArray jArr = json.getJSONArray("content");

                for (int i = 0; i < jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    String middleid = json.getString("middleid");

                    middleList.append(middleid).append(",");
                    //insertMiddle(middlename);
                }
            } catch (Exception e) {
                return;
            }
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

    class JsonLoadingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            makeDataList();
            return "";
        } // doInBackground : 백그라운드 작업을 진행한다.
        @Override
        protected void onPostExecute(String result) {
            //makeDataList에서 만들어진 partList를 textView에 입력한다.
            for(int i=0;i<correctList.length;i++){
                result += "인정분야 : " + correctList[i].getBigId() + "\n";
                result += "세부분야 : " + correctList[i].getMiddleList() + "\n\n";
            }
            tvCorrect.setText(result);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_correction, menu);
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
