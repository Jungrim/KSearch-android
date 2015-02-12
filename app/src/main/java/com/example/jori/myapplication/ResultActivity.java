package com.example.jori.myapplication;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by admin on 2015-02-11.
 */
public class ResultActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks  {
    String lat;
    String lon;
    String address;
    GoogleMap mMap;
    ResultData result;
    TextView data;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        result = (ResultData)intent.getSerializableExtra("Result");

        setContentView(R.layout.activity_result);

        mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        makeQuery(result.getCompanyName(),result.getAddr());
        mMap.setMyLocationEnabled(true);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        data = (TextView)findViewById(R.id.resultData);
        data.setTextColor(Color.BLACK);
        data.setTextSize(16);
        data.setText(setResultData());

    }
    private class ResultTask extends AsyncTask<String, String, Boolean> {
        //검색 버튼 누를시에 작동하는 AsyncTask로 resultData를 만든다.
//        ArrayAdapter<String> resultAdapter;
        private ArrayList<InfoClass> infoList;
        CustomBaseAdapter resultAdapter;
        private ProgressDialog dialog;
        private boolean[] checkList;

        @Override
        protected void onPreExecute() {
            //로딩 창을 만들어서 띄워줌
            infoList = new ArrayList<InfoClass>();
            dialog = new ProgressDialog(ResultActivity.this);
            dialog.setMessage("Loading");
            dialog.show();
            super.onPreExecute();
        }

        protected Boolean doInBackground(String... args){
            if(!makeLonLat(args[0])){
                return false;
            }
            return true;
        }
        protected void onPostExecute(Boolean jud) {
            dialog.dismiss();
            if(!jud){
                Toast.makeText(getApplicationContext(), "지도에서 찾을 수 없음.", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                float latitudeE6 = (Float.valueOf(lat));
                float longitudeE6 = (Float.valueOf(lon));
                System.out.println("lat:" + latitudeE6 + "lon:" + longitudeE6);
                LatLng LOC = new LatLng(latitudeE6, longitudeE6);
                mMap.addMarker(new MarkerOptions().position(LOC).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(result.getCompanyName()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LOC, 16));
                return;
            }
        }
    }
    private String setResultData(){
        String data = new String("인증번호 : "+result.getAccreditNumber());

        data = data+"회사이름 : "+result.getCompanyName();
        data = data+"전화번호 : "+result.getPhoneNumber();
        data = data+"담당자 : "+result.getChargeName();
        data = data+"측정능력 : "+result.getRangePower();
        System.out.println("data : "+data);
        return data;
    }
    private boolean makeLonLat(String query){
        try {

            String line = getDataFromUrl(query).toString();
            System.out.println("line : "+line);
            line = line.substring(9);// while(1); 문자 지우기
            JSONObject json = new JSONObject(line);
            JSONObject overlays = json.getJSONObject("overlays");
            JSONArray markers = overlays.getJSONArray("markers");

            if(markers != null){
                ArrayList<String> searchList = new ArrayList<String>();

                for(int i=0; i<markers.length(); i++){
                    address = markers.getJSONObject(i).getString("laddr");
                    lat = new String(markers.getJSONObject(i).getJSONObject("latlng").getString("lat"));
                    lon = new String(markers.getJSONObject(i).getJSONObject("latlng").getString("lng"));
                    //주소, 위도, 경도 순으로 저장함.
                }
            } else {
                System.out.println("No Search Data");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private void makeQuery(String address,String nearAddress){
//        try{
//            address = URLEncoder.encode(address, "UTF-8");
//            nearAddress = URLEncoder.encode(nearAddress,"UTF-8");
//        } catch(Exception e){
//            System.out.println("murlEncodingError!");
//        }
        List<BasicNameValuePair> qparams  = new ArrayList<BasicNameValuePair>();
        //검색어
        qparams.add(new BasicNameValuePair("q", address));
        //인근 지역 검색 생략
        qparams.add(new BasicNameValuePair("near", nearAddress));
        //결과포멧 - json
        qparams.add(new BasicNameValuePair("output", "json"));
        //검색 유형 범위
        qparams.add(new BasicNameValuePair("mrt", "yp"));
        //언어
        qparams.add(new BasicNameValuePair("hl", "ko"));
        // 검색반경 30km, miles = kilometers / 1.60934
        qparams.add(new BasicNameValuePair("radius", "18.641"));
        //검색갯수
        qparams.add(new BasicNameValuePair("num", "1"));
        String parameter = encodeParams(qparams.toArray(new BasicNameValuePair[qparams.size()]));

        new ResultTask().execute(parameter);
    }

    private  String encodeParams(NameValuePair[] parameters) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0 ; i < parameters.length ; i ++) {
            sb.append(parameters[i].getName());
            sb.append('=');
            sb.append(URLEncoder.encode(parameters[i].getValue().replace(" ", "+")));//공백은 + 를 붙여줌(생략가능)
            if (i + 1 < parameters.length) sb.append('&');
        }

        return sb.toString();
    }
    public StringBuilder getDataFromUrl(String url) throws IOException {
        // HttpURLConnection을 사용해서 주어진 URL에 대한 입력 스트림을 얻는다.
        //얻어진 입력스트림을 한줄씩 읽어서 page에 저장하고 return한다.
        HttpURLConnection conn = null;
        HttpClient httpclient = new DefaultHttpClient();
        StringBuilder sb = new StringBuilder();
        try {
            HttpGet get = new HttpGet();
            URL u = new URL("http://maps.google.co.kr?"+url);
            System.out.println(u.toString());
            get.setURI(new URI("http://maps.google.co.kr?" + url));

            //10초 응답시간 타임아웃
            HttpParams params = httpclient.getParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            HttpConnectionParams.setConnectionTimeout(params, 10000);
            HttpConnectionParams.setSoTimeout(params, 10000);
            HttpResponse response = httpclient.execute(get);
            InputStreamReader isr = new InputStreamReader(response.getEntity().getContent(), "EUC-KR");
            BufferedReader br = new BufferedReader(isr);
            for (;;) {
                String line = br.readLine();
                if (line == null)
                    break;
                sb.append(line + '\n');
            }
            return sb;
        }catch (Exception e ){
            System.out.println(e.toString());
            return sb;
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
