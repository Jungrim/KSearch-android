package com.example.jori.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class MaterialActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private String activityName = "MaterialActivity";
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    String[] city = {"시도별","서울","부산","대구","광주","인천","대전","울산","경기","강원","제주","세종","충청북도","충청남도"
            ,"전라북도","전라남도","경상북도","경상남도"};

    private String[] bigList;
    private SmallData[] smallList;
    private Spinner bigSpinner;
    private Spinner middleSpinner;
    private Spinner smallSpinner;
    private Button searchButton;
    private BigMiddleConnect[] dataList;

    private String selectBigid;
    private String selectMiddleid;
    private String selectSmall;
    private String selectCity;
    private boolean[] checkList;
    private Spinner citySpinner;

    private ListView resultView;
    private ArrayList<ResultData> results;
    private HttpUrlConnect urlConnector;

    private NotesDbAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correction);

        urlConnector = new HttpUrlConnect();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_correction);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer_correction,
                (DrawerLayout) findViewById(R.id.drawer_layout_correction));

        bigSpinner = (Spinner)findViewById(R.id.big_spinner);
        middleSpinner = (Spinner)findViewById(R.id.middle_spinner);
        smallSpinner = (Spinner)findViewById(R.id.small_spinner);
        citySpinner = (Spinner)findViewById(R.id.city_spinner);
        citySpinner.setAdapter(new MyArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,city));
        citySpinner.setOnItemSelectedListener(new CityItemSelected());
        searchButton = (Button)findViewById(R.id.search_button);
        searchButton.setOnClickListener(new searchButtonListener());
//        userInput = (EditText)findViewById(R.id.company_name);
        new BigAdapterTask().execute();
        resultView = (ListView) findViewById(R.id.result_view);
        resultView.setOnItemClickListener(new resultViewListener());

        dbAdapter = new NotesDbAdapter(this);
        dbAdapter.open();
    }

    public class resultViewListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MaterialActivity.this,ResultActivity.class);
            intent.putExtra("Result",results.get(position));
            startActivity(intent);
        }
    }
    public class searchButtonListener implements View.OnClickListener {
        public void onClick(View v){
            //Toast.makeText(getApplicationContext(), selectBigname+selectMiddlename, Toast.LENGTH_SHORT).show();
//            inputCompanyName = new String(userInput.getText().toString());
//            System.out.println(inputCompanyName);
            new ResultTask().execute();
        }
    }

    private class CityItemSelected implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
            String select = (String)citySpinner.getSelectedItem();

            if(select.equals("시도별"))
                selectCity = new String("");
            else
                selectCity = new String(select);

            System.out.println(selectCity + selectCity.length());
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }
    public class SmallAdapterListener implements  AdapterView.OnItemSelectedListener{

        public void onItemSelected(AdapterView<?> parent, View view, int position, long Id){
            selectSmall = new String(smallList[position].getSmallName());
//            System.out.println(selectSmall);
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
    public class MiddleAdapterListener implements AdapterView.OnItemSelectedListener{
        int id;

        public MiddleAdapterListener(int id){
            this.id = id;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int position, long Id){
            selectMiddleid = new String(dataList[this.id].getMiddleId(position));

            new SmallAdapterTask().execute();
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class ResultTask extends AsyncTask<Void, Void, Void> {
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
            dialog = new ProgressDialog(MaterialActivity.this);
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
            checkList = new boolean[results.size()];
            dialog.dismiss();
            int i = 0;
            for(ResultData tmpData : results){
                infoList.add(new InfoClass(tmpData.getAccreditNumber(),tmpData.getCompanyName(),tmpData.getAddr(),tmpData.getActivityName()));
                checkList[i++] = tmpData.getCheck();
            }
            resultView.setAdapter(new CustomBaseAdapter(getApplicationContext(),infoList,checkList));

        }
    }
    private class SmallAdapterTask extends AsyncTask<Void, Void, Void>{
        private ProgressDialog dialog;
        private String[] smallNameList;
        @Override
        protected void onPreExecute() {
            //쿼리를 만들어서 데이터를 불러오기전에 실행되는 부분
            //로딩창을 만들어서 띄워줌
            dialog = new ProgressDialog(MaterialActivity.this);
            //          dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("Loading");
            dialog.show();

            super.onPreExecute();
        }

        protected Void doInBackground(Void... ids){
            //입력받은 대분류Id(bigid)를 통해서 해당되는 세부분야(middlename)를 불러옴
//            midSmallList[ids[0]].setSmallList();
            makeSmallList();
            return null;
        }
        protected void onPostExecute(Void id){
            //불러와진 데이터를 통해 어레이어댑터를 만들고
            //미들스피너에 붙이고 로딩창 종료
            smallNameList = new String[smallList.length];
            System.out.println(smallList.length);
            int i = 0;
            for(SmallData tmpData : smallList){
                smallNameList[i++] = new String(tmpData.getSmallName());
                System.out.println(smallNameList[i-1]);
            }
            ArrayAdapter smallAdapter = new MyArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,smallNameList );
            smallAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            smallSpinner.setAdapter(smallAdapter);
            smallSpinner.setOnItemSelectedListener(new SmallAdapterListener());
            dialog.dismiss();
        }
    }
    private class MiddleAdapterTask extends AsyncTask<Integer, Void, Integer>{
        //대분류가 선택되었을때 대분류에 해당하는 세부분야를 불러오는 AsyncTask
        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            //쿼리를 만들어서 데이터를 불러오기전에 실행되는 부분
            //로딩창을 만들어서 띄워줌
            dialog = new ProgressDialog(MaterialActivity.this);
            //          dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("Loading");
            dialog.show();

            super.onPreExecute();
        }

        protected Integer doInBackground(Integer... ids){
            //입력받은 대분류Id(bigid)를 통해서 해당되는 세부분야(middlename)를 불러옴
            dataList[ids[0]].setMiddleList("http://ibtk.kr/standardMaterialDetailSearch_api/","cb0b2b73d22b4a00a9c5504901aa9fe0?");
            return ids[0];
        }
        protected void onPostExecute(Integer id){
            //불러와진 데이터를 통해 어레이어댑터를 만들고
            //미들스피너에 붙이고 로딩창 종료
            ArrayAdapter middleAdapter = new MyArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, dataList[id].getMiddleList());
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
            dialog = new ProgressDialog(MaterialActivity.this);
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
            bigSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    for (int i = 0; i < dataList.length; i++) {
                        if (bigList[position].equals(dataList[i].getBigname())) {
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

    public void makeSmallList(){
        //API URL로 부터 bigId와 middleId를 통해 smallList를 만든다
        String serviceUrl = "http://ibtk.kr/standardMaterialCategories_api/";
        String serviceKey = "b8ec9e277c178992d1e30adbdefa4295?";
        String query = "model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"smallid\",direction:1}]}&model_query_distinct=smallid&model_query={$and:[{\"bigid\":\""+selectBigid+"\"},{\"middleid\":\""+selectMiddleid+"\"}]}";
        String strUrl = serviceUrl + serviceKey + query;
        System.out.println(strUrl);

        if(selectMiddleid.equals("-1")){
            smallList = new SmallData[1];
            smallList[0] = new SmallData("소분류","-1");
            return;
        }

        try {
            String inline = urlConnector.getDataFromUrl(strUrl);
            System.out.println(inline);
            JSONObject json = new JSONObject(inline);
            JSONArray jArr = json.getJSONArray("content");
            smallList = new SmallData[jArr.length()+1];
            smallList[0] = new SmallData("소분류","-1");

            for (int i = 0; i< jArr.length()+1 ; i++) {
                json = jArr.getJSONObject(i);
                smallList[i+1] = new SmallData(new String(json.getString("smallname")),new String(json.getString("smallid")));
                System.out.println(smallList[i+1].getSmallName());
            }
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public void makeDataList() {
        //API URL로 부터 bigid을 가져와서 bignameList와 연결해서 bigList에 입력
        String serviceUrl = "http://ibtk.kr/standardMaterialDetailSearch_api/";
        String serviceKey = "cb0b2b73d22b4a00a9c5504901aa9fe0?";
        String query = "model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"bigid\",direction:1}]}&model_query_distinct=bigid";
        String strUrl = serviceUrl + serviceKey + query;

        try {
            String line = urlConnector.getDataFromUrl(strUrl);
            JSONObject json = new JSONObject(line);
            JSONArray jArr = json.getJSONArray("content");
            bigList = new String[jArr.length()+1];
            bigList[0] = new String("대분류");
            dataList = new BigMiddleConnect[jArr.length()+1];
            dataList[0] = new BigMiddleConnect("대분류","-1",serviceUrl,serviceKey);
            for (int i = 0; i < jArr.length()+1; i++) {
                //JSONArray jARR로 부터 bigname과 bigid에 해당하는 data를 읽어옴
                json = jArr.getJSONObject(i);
                String bigId = json.getString("bigid");
                String bigName = json.getString("bigname");
                bigList[i+1] = new String(bigName);
                System.out.println(bigList[i+1]);
                dataList[i+1] = new BigMiddleConnect(bigName,bigId,serviceUrl,serviceKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeSearchQuery(){
        //검색버튼 클릭시에 작동하는 메소드로 선택된 대분류Id(selectBigid)와 세부분야Id(selectMiddleid)를 통해서
        //API로 부터 해당하는 기관을 찾아준다.
        String searchUrl = "http://ibtk.kr/standardMaterialAgencies_api/";
        String searchUrlKey = "e176ac6741fd7bb2ce7507368d8818fb?";
        String searchquery = "model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"accreditnumber\",direction:1}]}&model_query_distinct=companyno";
        String[] companynoList;

        if(selectCity.length() == 0) {
            //도시 선택이 없을 때
            if (selectBigid.equals("-1")) {
                //대분류에 대한 선택이 없을 때 -> 모든 기관들 데이터
                System.out.println("1");
                searchquery = new String("model_query_pageable={enable:true,pageSize:1000}&model_query_distinct=companyno");
            } else if ((!selectBigid.equals("-1")) && selectMiddleid.equals("-1")) {
                //대분류 선택 있고 중분류 선택 없을 때
                System.out.println("2");
                searchquery = searchquery + "&model_query={\"bigid\":\"" + selectBigid + "\"}";
//                searchquery = new String("model_query={\"bigid\":\"" + selectBigid + "\"}&model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"accreditnumber\",direction:1}]}&model_query_distinct=companyno");
            } else {
                //중분류의 선택이 있을 때
                if (selectSmall.equals(("소분류"))) {
                    //소분류의 선택이 없을 때
                    searchquery = searchquery + "&model_query={$and:[{\"bigid\":\"" + selectBigid + "\"},{\"middleid\":\"" + selectMiddleid + "\"}]}";
//                searchquery = new String("model_query={$and:[{\"bigid\":\"" + selectBigid + "\"},{\"middleid\":\"" + selectMiddleid + "\"}]}&model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"accreditnumber\",direction:1}]}&model_query_distinct=companyno");
                } else {
                    //소분류의 선택이 있을 때
                    try {
                        selectSmall = URLEncoder.encode(selectSmall, "UTF-8");
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                    searchquery = searchquery + "&model_query={$and:[{\"bigid\":\"" + selectBigid + "\"},{\"middleid\":\"" + selectMiddleid + "\"},{\"smallname\":\"" + selectSmall + "\"}]}";
                }
            }
        }
        else {
            //도시 선택이 있을 때
            try {
                selectCity = URLEncoder.encode(selectCity, "UTF-8");
            } catch (Exception e) {
                System.out.println(e.toString());
            }

            if (selectBigid.equals("-1")) {
                //대분류에 대한 선택이 없을 때 -> 모든 기관들 데이터
                System.out.println("1");
                searchquery = new String("model_query={\"delegateaddr\":{\"$regex\":\"" + selectCity + "\"}}&model_query_pageable={enable:true,pageSize:1000}&model_query_distinct=companyno");
            } else if ((!selectBigid.equals("-1")) && selectMiddleid.equals("-1")) {
                //대분류 선택 있고 중분류 선택 없을 때
                System.out.println("2");
                searchquery = searchquery + "&model_query={$and:[{\"bigid\":\"" + selectBigid + "\"},{\"delegateaddr\":{\"$regex\":\"" + selectCity + "\"}}]}";
//              searchquery = new String("model_query={\"bigid\":\"" + selectBigid + "\"}&model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"accreditnumber\",direction:1}]}&model_query_distinct=companyno");
            } else {
                //중분야의 선택이 있을 때
                if (selectSmall.equals("소분류")) {
                    //소분류의 선택이 없을 때
                    searchquery = searchquery + "&model_query={$and:[{\"bigid\":\"" + selectBigid + "\"},{\"middleid\":\"" + selectMiddleid + "\"},{\"delegateaddr\":{\"$regex\":\"" + selectCity + "\"}}]}";
//                  searchquery = new String("model_query={$and:[{\"bigid\":\"" + selectBigid + "\"},{\"middleid\":\"" + selectMiddleid + "\"}]}&model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"accreditnumber\",direction:1}]}&model_query_distinct=companyno");
                } else {
                    try {
                        selectSmall = URLEncoder.encode(selectSmall, "UTF-8");
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                    //소분류의 선택이 있을 때
                    searchquery = searchquery + "&model_query={$and:[{\"bigid\":\"" + selectBigid + "\"},{\"middleid\":\"" + selectMiddleid + "\"},{\"smallname\":\"" + selectSmall + "\"},{\"delegateaddr\":{\"$regex\":\"" + selectCity + "\"}}]}";
                }
            }
        }
        String strUrl = searchUrl + searchUrlKey + searchquery;
        results = new ArrayList<ResultData>();

        try {
            String line = urlConnector.getDataFromUrl(strUrl);
            System.out.println("line : "+line);
            JSONObject json = new JSONObject(line);
            JSONArray jArr = json.getJSONArray("content");
            companynoList = new String[jArr.length()];
//            results = new resultData[injArr.length()];
            for (int j = 0; j < jArr.length(); j++) {
                //얻어온 데이터의 회사명, 인증번호, 주소를 얻어와서 ResultData객체를 생성하고
                //생성된 ResultData객체를 results벡터에 저장
                json = jArr.getJSONObject(j);
                String companyNo = json.getString("companyno");
                companynoList[j] = new String(companyNo);
                try {
                    String insearchUrl = "http://ibtk.kr/standardMaterialAgenciesDetail_api/";
                    String insearchUrlKey = "efb8b2c1d5c397733701cfed51f77308?";
                    String insearchquery = "model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"accreditnumber\",direction:1}]}&model_query_distinct=companyno&model_query={\"companyno\":\""+companyNo+"\"}";
                    String instrUrl = insearchUrl + insearchUrlKey + insearchquery;

                    String inline = urlConnector.getDataFromUrl(instrUrl);
                    System.out.println("inline : "+inline);
                    JSONObject injson = new JSONObject(inline);
                    JSONArray injArr = injson.getJSONArray("content");
//            results = new resultData[injArr.length()];
                    for (int i = 0; i < injArr.length(); i++) {
                        //얻어온 데이터의 회사명, 인증번호, 주소를 얻어와서 ResultData객체를 생성하고
                        //생성된 ResultData객체를 results벡터에 저장
                        injson = injArr.getJSONObject(i);
                        String companyName = injson.getString("company");

                        Boolean check = false;

                        Cursor result = dbAdapter.fetchAllNotes();
                        result.moveToFirst();
                        while (!result.isAfterLast()) {
                            String title = result.getString(1);
                            String activity = result.getString(3);

                            System.out.println("디비정보불러오기 "+ title);
                            System.out.println("회사이름 " + companyName);
                            System.out.println("아니시발이거뭐야 " + companyName.equals(title.trim()));
                            if ((companyName.equals(title.trim()))&&(activity.equals(activityName))) {
                                check = true;
                                System.out.println("둘이똑같을때" + title);
                                break;
                            }
                            result.moveToNext();
                        }
                        result.close();

                        String addr = injson.getString("delegateaddr");
                        String accreditNumber = injson.getString("accreditnumber");
                        String phoneNumber = injson.getString("delegatephone");
                        String chargeName = injson.getString("chargename");
                        String rangePower = injson.getString("rangepower");

                        results.add(new ResultData(companyName, addr, accreditNumber,phoneNumber,chargeName,rangePower,check,activityName));

                    }
                } catch (Exception e){
                    System.out.println("makeQuery1" + e.toString());
                    return;
                    //return "middleList" + e.toString();
                }
            }
        } catch (Exception e){
            System.out.println("makeQuery2" + e.toString());
            return;
            //return "middleList" + e.toString();
        }

    }

    public ArrayAdapter setBigAdapter() {
        //bigList를 통해서 어레이 어댑터를 생성하고 리턴해줌
        ArrayAdapter tmpAdapter = new MyArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, bigList);
        tmpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return tmpAdapter;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_material, menu);
        return true;
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

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if(position ==0){
            Intent i = new Intent(this,CorrectionActivity.class);
            Bundle b= new Bundle();
            b.putInt("position",position);

            i.putExtras(b);
            startActivity(i);

        }
        else if(position ==1){
            Intent i = new Intent(this,ExamActivity.class);
            Bundle b= new Bundle();
            b.putInt("position",position);

            i.putExtras(b);
            startActivity(i);
        }
        else if(position ==2){
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
