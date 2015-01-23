package com.example.jori.myapplication;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import android.app.ProgressDialog;
import android.view.KeyEvent;


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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.widget.Toast;

import javax.xml.transform.Result;


public class CheckActivity extends ActionBarActivity {
    //BigMiddleConnect[] partList;
    private String[] bigList;
    private Spinner bigSpinner;
    private Spinner middleSpinner;
    private Button searchButton;
    private BigMiddleConnect[] dataList;
    private String selectBigid;
    private String selectMiddleid;
    private ListView resultView;
    private ArrayList<ResultData> results;
//    resultData[] results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        bigSpinner = (Spinner)findViewById(R.id.big_spinner);
        middleSpinner = (Spinner)findViewById(R.id.middle_spinner);
        searchButton = (Button)findViewById(R.id.search_button);
        searchButton.setOnClickListener(new searchButtonListener());
        new BigAdapterTask().execute();
        resultView = (ListView) findViewById(R.id.result_view);
    }

    public class searchButtonListener implements View.OnClickListener {
        public void onClick(View v){
            //Toast.makeText(getApplicationContext(), selectBigname+selectMiddlename, Toast.LENGTH_SHORT).show();
            new ResultTask().execute();
        }
    }

    public class MiddleAdapterListener implements AdapterView.OnItemSelectedListener{
        int id;

        public MiddleAdapterListener(int id){
            this.id = id;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int position, long Id){
            selectMiddleid = new String(dataList[this.id].getMiddleId(position));
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class ResultTask extends AsyncTask<Void, Void, Void>{
        //검색 버튼 누를시에 작동하는 AsyncTask로 resultData를 만든다.
//        ArrayAdapter<String> resultAdapter;
        private ArrayList<InfoClass> infoList;
        CustomBaseAdapter resultAdapter;
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //쿼리를 날려 데이터를 만들기 전에 미리 작동되는 부분으로
            //로딩 창을 만들어서 띄워줌
            infoList = new ArrayList<InfoClass>();
            dialog = new ProgressDialog(CheckActivity.this);
  //          dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("Loading");
            dialog.show();
            super.onPreExecute();
        }

        protected Void doInBackground(Void... voids){
            //쿼리를 날려서 데이터를 얻어오고 어레이 어댑터를 만듬
            makeSearchQuery();

//            resultAdapter = new CustomBaseAdapter(getApplicationContext(),(InfoClass)results);
//            resultAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1);
            return null;
        }
        protected void onPostExecute(Void id) {
            //데이터를 모두 얻어온 다음 실행되는 부분으로 로딩창을 끝내고
            //얻어온 데이터를 뷰에 붙여서 보여줌
            //후에 정림이가 만든 ListView로 수정

            dialog.dismiss();
            for(ResultData tmpData : results){
                infoList.add(new InfoClass(tmpData.getData(),getResources().getDrawable(R.drawable.ic_launcher),""+ (results.indexOf(tmpData)+1)));
            }
            resultView.setAdapter(new CustomBaseAdapter(getApplicationContext(),infoList));

        }
    }

    private class MiddleAdapterTask extends AsyncTask<Integer, Void, Integer>{
        //대분류가 선택되었을때 대분류에 해당하는 세부분야를 불러오는 AsyncTask
        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            //쿼리를 만들어서 데이터를 불러오기전에 실행되는 부분
            //로딩창을 만들어서 띄워줌
            dialog = new ProgressDialog(CheckActivity.this);
            //          dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("Loading");
            dialog.show();

            super.onPreExecute();
        }

        protected Integer doInBackground(Integer... ids){
            //입력받은 대분류Id(bigid)를 통해서 해당되는 세부분야(middlename)를 불러옴
            dataList[ids[0]].setMiddleList();
            return ids[0];
        }
        protected void onPostExecute(Integer id){
            //불러와진 데이터를 통해 어레이어댑터를 만들고
            //미들스피너에 붙이고 로딩창 종료
            ArrayAdapter middleAdapter = new myAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, dataList[id].getMiddleList());
            middleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            middleSpinner.setAdapter(middleAdapter);
            middleSpinner.setOnItemSelectedListener(new MiddleAdapterListener(id));
            dialog.dismiss();
        }
    }

    private class BigAdapterTask extends AsyncTask<String, Void, String> {
        //최초에 체크액티비티가 켜졌을때 대분류를 불러오는 AsyncTask
        ArrayAdapter bigAdapter;
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //쿼리문을 만들어 데이터를 불러오기전에 실행되는 부분
            //로딩창을 만들어서 듸워줌
            dialog = new ProgressDialog(CheckActivity.this);
            dialog.setMessage("Loading");
            dialog.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... str) {
            //쿼리를 만들어서 대분류를 불러오고, 어레이리스트인 bigAdapter를 만든다.
            makeDataList();
            bigAdapter = setBigAdapter();

            return "";
        } // doInBackground : 백그라운드 작업을 진행한다.

        @Override
        protected void onPostExecute(String result) {
            //makeDataList에서 만들어진 bigList를 apdater를 통해 spinner에 입력
            //로딩창을 끝냄.
            bigSpinner.setAdapter(bigAdapter);
            dialog.dismiss();
            bigSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                    for(int i=0;i<dataList.length;i++){
                        if(bigList[position].equals(dataList[i].getBigname())) {
                            new MiddleAdapterTask().execute(i);
                            selectBigid = new String(dataList[i].getBigId());
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

    private class myAdapter extends ArrayAdapter<String> {
        //스피너에 붙는 어레이 어댑터를 만들때 사용되는 클래스로
        //어레이어댑터를 상속하고 TextView를 통해서 폰트의 설정을 변경할 수 있다.
        Context context;
        String[] items = new String[] {};

        public myAdapter(final Context context,
                         final int textViewResourceId, final String[] objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
            this.context = context;
        }
        @Override
        //스피너 클릭시 보여지는 View의 정의
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
            tv.setTextSize(15);
            tv.setHeight(50);
            return convertView;
        }

        @Override
        //기본 스피터 View의 정의
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
        //대분류와 그에 해당하는 세부분야들을 갖고 있으며 get메소드들과 set메소드를 통해
        //대분류에 해당하는 세부분야를 만들고, 원하는 Data를 리턴해준다.
        private String bigId;
        private String bigname;
        private String[] middleList;
        private String[] middleidList;
        private String middleStr = "";
        private String middleidStr = "";

        public BigMiddleConnect(String bigname,String bigId){
            //인자로 받는 bigname과 bigId를 통해 생성
            this.bigname = bigname;
            this.bigId = bigId;
        }

        public void setMiddleList() {
            //현재 객체의 bigId를 통해서 현재 객체의 대분류에 해당하는 세부분야의 쿼리문을 작성하고
            //스트링 리스트로 저장
            String inserviceUrl = "http://ibtk.kr/inspectionAgencyDetail_api/";
            String inserviceKey = "3f7f0c56c14ad73f3a0534ba2999f4a2?";
            String query = "model_query_pageable.enable=true&model_query_distinct=middlename&model_query={\"bigid\":\"" + this.bigId + "\"}";
            String instrUrl = inserviceUrl + inserviceKey + query;

            try {
                String inline = getDataFromUrl(instrUrl);
                JSONObject injson = new JSONObject(inline);
                JSONArray injArr = injson.getJSONArray("content");
//                middleList = new String[injArr.length()];

                for (int j = 0; j < injArr.length(); j++) {
                    injson = injArr.getJSONObject(j);
                    String middleName = injson.getString("middlename");
                    String middleId = injson.getString("middleid");
                    //System.out.println(middleName);
                    if(middleName.length() ==0)
                        continue;
                    middleStr = middleStr + middleName + ",";
                    middleidStr = middleidStr + middleId + ",";
                }
                //쿼리문에서 얻어온 데이터에서 세부분야의 이름이 빈칸으로 되어있는 부분이 있기 떄문에
                //해당 부분을 삭제하기 위해 StringTokenizer를 사용한다.
                StringTokenizer middleNameSt = new StringTokenizer(middleStr,",");
                StringTokenizer middleIdSt = new StringTokenizer(middleidStr,",");
                middleList = new String[middleNameSt.countTokens()];
                middleidList = new String[middleIdSt.countTokens()];
                int i = 0;
                while(middleNameSt.hasMoreTokens()){
                    String tmpName = middleNameSt.nextToken();
                    String tmpId = middleIdSt.nextToken();
                    middleidList[i] = new String(tmpId);
                    middleList[i++] = new String(tmpName);
                }
            } catch (Exception e){
                return;
                //return "middleList" + e.toString();
            }
        }

        public String getBigname(){
            return bigname;
        }
        public String getMiddleId(int id) { return middleidList[id];}
        public String[] getMiddleList(){
            return middleList;
        }

        public String getBigId(){
            return bigId;
        }
    }

    private class ResultData {
        //검색을 통해 얻어진 데이터의 정보를 저장하고 있을 클래스
        private String companyName;
        private String addr;
        private String accreditNumber;

        public ResultData(String companyName,String addr,String accreditNumber){
            this.companyName = companyName;
            this.addr = addr;
            this.accreditNumber = accreditNumber;
        }

        public String getCompanyName(){
            return companyName;
        }
        public String getData(){
            return accreditNumber + "\n" + companyName + "\n" + addr +"\n";
        }
        public String getAddr(){
            return addr;
        }
    }

    public void makeDataList() {
        //API URL로 부터 bigname을 가져와서 bigList에 입력
        String serviceUrl = "http://ibtk.kr/inspectionRecognize_api/";
        String serviceKey = "48744e4796989e49eef86081ab416116?";
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
                String bigId = json.getString("bigid");
                bigList[i] = new String(bigName);
                System.out.println(bigList[i]);
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
        //검색버튼 클릭시에 작동하는 메소드로 선택된 대분류Id(selectBigid)와 세부분야Id(selectMiddleid)를 통해서
        //API로 부터 해당하는 기관을 찾아준다.
        String searchUrl = "http://ibtk.kr/inspectionAgencyDetail_api/";
        String searchUrlKey = "3f7f0c56c14ad73f3a0534ba2999f4a2?";
        String searchquery = "model_query={$and:[{\"bigid\":\"" + selectBigid + "\"},{\"middleid\":\"" + selectMiddleid + "\"}]}&model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"accreditnumber\",direction:1}]}&model_query_distinct=companyno";
        String instrUrl = searchUrl + searchUrlKey + searchquery;
        results = new ArrayList<ResultData>();

        try {
            String inline = getDataFromUrl(instrUrl);
            System.out.println("line : "+inline);
            JSONObject injson = new JSONObject(inline);
            JSONArray injArr = injson.getJSONArray("content");
//            results = new resultData[injArr.length()];
            for (int j = 0; j < injArr.length(); j++) {
                //얻어온 데이터의 회사명, 인증번호, 주소를 얻어와서 ResultData객체를 생성하고
                //생성된 ResultData객체를 results벡터에 저장
                injson = injArr.getJSONObject(j);
                String companyName = injson.getString("company");
                //System.out.println(middleName);
                String addr = injson.getString("corporateaddr");
                String accreditNumber = injson.getString("accreditnumber");
//                System.out.println(companyName + addr + accreditNumber);
                results.add(new ResultData(companyName, addr, accreditNumber));

            }
        } catch (Exception e){
            System.out.println("makeQuery" + e.toString());
            return;
            //return "middleList" + e.toString();
        }
    }

    public ArrayAdapter setBigAdapter() {
        //bigList를 통해서 어레이 어댑터를 생성하고 리턴해줌
        ArrayAdapter tmpAdapter = new myAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, bigList);
        tmpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return tmpAdapter;
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