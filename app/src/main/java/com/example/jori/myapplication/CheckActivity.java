package com.example.jori.myapplication;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONObject;
import org.json.JSONArray;

import android.app.Activity;
import android.widget.TextView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.content.Intent;


public class CheckActivity extends ActionBarActivity {
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        Intent intent = getIntent();
        String intent_name = intent.getStringExtra("intent_name");
        tv = (TextView)findViewById(R.id.tv);
        //tv.setText(intent_name);
        new JsonLoadingTask().execute();
    }

    public String getJsonText() {
        //API URL로 부터 Data를 불러와서 StringBuffer data에 append
        //추후에 StringBuffer가 아닌 Button에 append하는 식으로 수정
        StringBuffer data = new StringBuffer();

        String serviceUrl = "http://api.ibtk.kr/inspectionRecognize_api/";
        String serviceKey = "48744e4796989e49eef86081ab416116?";
        String query = "?model_query_pageable.enable=false&model_query_distinct=bigname";
        String strUrl = serviceUrl + serviceKey + query;
        //data.append(strUrl);

        try {
            String line = getStringFromUrl(strUrl);
            JSONObject json = new JSONObject(line);
            JSONArray jArr = json.getJSONArray("content");

            for (int i = 0; i < jArr.length(); i++) {
                //JSONArray jARR로 부터 bigname에 해당하는 data를 가져와서 append하는 부분
                json = jArr.getJSONObject(i);
                //data.append(json.toString());
                String bigname = json.getString("bigname");
                data.append("인정분야 : ").append(bigname).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data.toString();
    }

    public String getStringFromUrl(String url) throws UnsupportedEncodingException {
        // 입력스트림을 "UTF-8" 를 사용해서 읽은 후, 라인 단위로 데이터를 읽을 수 있는 BufferedReader 를 생성한다.
        BufferedReader br = new BufferedReader(new InputStreamReader(getInputStreamFromUrl(url), "UTF-8"));

        // 읽은 데이터를 저장한 StringBuffer 를 생성한다.
        StringBuffer sb = new StringBuffer();

        try {
            // 라인 단위로 읽은 데이터를 임시 저장한 문자열 변수 line
            String line = null;
            // 라인 단위로 데이터를 읽어서 StringBuffer 에 저장한다.
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static InputStream getInputStreamFromUrl(String url) {
        InputStream contentStream = null;
        try {
            // HttpClient 를 사용해서 주어진 URL에 대한 입력 스트림을 얻는다.
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(url));
            contentStream = response.getEntity().getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentStream;
    } // getInputStreamFromUrl

    class JsonLoadingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            return getJsonText();
        } // doInBackground : 백그라운드 작업을 진행한다.
        @Override
        protected void onPostExecute(String result) {
            tv.setText(result);
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
