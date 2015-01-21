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
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    //BigMiddleConnect[] partList;
    String[] bigList;
    Spinner bigSpinner;
    Spinner middleSpinner;
    Button searchButton;
    String serviceUrl;
    String serviceKey;
    BigMiddleConnect[] dataList;
    String selectData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        bigSpinner = (Spinner)findViewById(R.id.big_spinner);
        middleSpinner = (Spinner)findViewById(R.id.middle_spinner);
        searchButton = (Button)findViewById(R.id.search_button);
        searchButton.setOnClickListener(new searchButtonListener());
        new JsonLoadingTask().execute();
    }
    public class searchButtonListener implements View.OnClickListener {
        public void onClick(View v){
            Toast.makeText(getApplicationContext(), selectData, Toast.LENGTH_SHORT).show();
        }
    }
    public void makeDataList() {
        //API URL로 부터 bigname을 가져와서 bigList에 입력
        serviceUrl = "http://api.ibtk.kr/inspectionRecognize_api/";
        serviceKey = "48744e4796989e49eef86081ab416116?";
        String query = "model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"bigname\",direction:1}]}&model_query_distinct=bigid";
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
                int bigId = Integer.parseInt(json.getString("bigid"));
                bigList[i] = new String(bigName);
                dataList[i] = new BigMiddleConnect(bigName,bigId);
                dataList[i].setMiddleList();
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

    class JsonLoadingTask extends AsyncTask<String, Void, String> {
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
//            ArrayAdapter bigAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,bigList);
//            bigAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            bigSpinner.setAdapter(bigAdapter);
            bigSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                    for(int i=0;i<dataList.length;i++){
                        if(bigList[position].equals(dataList[i].getBigname())) {
//                          Toast.makeText(getApplicationContext(), dataList[i].getMiddleList(), Toast.LENGTH_SHORT).show();
                            selectData += bigList[position];
                            ArrayAdapter middleAdapter = new myAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, dataList[i].getMiddleList());
                            middleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                            middleSpinner.setAdapter(middleAdapter);
                            middleSpinner.setOnItemSelectedListener(new MiddleAdapterListener(i));
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            return;
        }

    }

    public class MiddleAdapterListener implements AdapterView.OnItemSelectedListener{
        int id;

        public MiddleAdapterListener(int id){
            this.id = id;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int position, long Id){
            selectData += dataList[this.id].getMiddleName(position);
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
    public ArrayAdapter setBigAdapter() {
        ArrayAdapter tmpAdapter = new myAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, bigList);
        tmpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return tmpAdapter;
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


    public class BigMiddleConnect{
        int bigId;
        String bigname;
        String[] middleList;

        public BigMiddleConnect(String bigname,int bigId){
            this.bigname = bigname;
            this.bigId = bigId;
        }

        public void setMiddleList() {
            String inserviceUrl = "http://www.ibtk.kr/inspectionAgencyDetail_api/";
            String inserviceKey = "3f7f0c56c14ad73f3a0534ba2999f4a2?";
            String query = "model_query_pageable.enable=true&model_query_distinct=middlename&model_query={\"bigid\":\"0" + this.bigId + "\"}";
            String instrUrl = inserviceUrl + inserviceKey + query;

            try {
                String inline = getDataFromUrl(instrUrl);
                JSONObject injson = new JSONObject(inline);
                JSONArray injArr = injson.getJSONArray("content");
                middleList = new String[injArr.length()];

                for (int j = 0; j < injArr.length(); j++) {
                    injson = injArr.getJSONObject(j);
                    String middleName = injson.getString("middlename");
                    System.out.println(middleName);
                    middleList[j] = new String(middleName);
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