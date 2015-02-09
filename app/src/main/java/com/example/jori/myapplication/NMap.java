package com.example.jori.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Vector;

public class NMap extends NMapActivity {
    private static final String API_KEY = "770d3836366fcee226c976b9d6624d7c";
    private NMapView mMapView;
    private NMapController mMapController;
    private NMapViewerResourceProvider nMapViewerResourceProvider;
    private NMapOverlayManager overlayManager;
    private NMapPOIdataOverlay poiDataOverlay;
    private String lat;
    private String lon;
    private ResultData result;
    private Intent intent;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        result = (ResultData)intent.getSerializableExtra("Result");

        //네이버 지도 MapView를 생성해준다.
        mMapView = new NMapView(this);
        //resource provider 생성
        nMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        //지도위의 오버레이를 관리하는 OverlayManager 생성
        overlayManager = new NMapOverlayManager(this, mMapView, nMapViewerResourceProvider);
        //지도에서 ZoomControll을 보여준다
        mMapView.setBuiltInZoomControls(true, null);
        //네이버 OPEN API 사이트에서 할당받은 KEY를 입력하여 준다.
        mMapView.setApiKey(API_KEY);
        //이 페이지의 레이아웃을 네이버 MapView로 설정해준다.
        setContentView(mMapView);
        //네이버 지도의 클릭이벤트를 처리 하도록한다.
        mMapView.setClickable(true);
        new getAddrTask().execute(result.getAddr(),result.getCompanyName());

//        System.out.println("lat"+this.lat+"lon"+this.lon);
// set POI data
//        NMapPOIdata poiData = new NMapPOIdata(2, nMapViewerResourceProvider);
//        poiData.beginPOIdata(2);
//        poiData.addPOIitem(127.055, 37.49, "Pizza 789-1011", markerId, 0);
//        poiData.addPOIitem(127.061, 37.51, "Pizza 123-456", markerId, 0);
//        poiData.endPOIdata();
//
//// create POI data overlay
//        poiDataOverlay = overlayManager.createPOIdataOverlay(poiData, null);
//        poiDataOverlay.showAllPOIdata(0);

        //맵의 상태가 변할때 이메소드를 탄다.
//        mMapView.setOnMapStateChangeListener(new NMapView.OnMapStateChangeListener() {
//
//            @Override
//            public void onZoomLevelChange(NMapView arg0, int arg1) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onMapInitHandler(NMapView arg0, NMapError arg1) {
//                // TODO Auto-generated method stub
//                //오류없이 네이버 지도가 초기화 되었다면 특정 좌표의 특정 Zoom으로 위치를 표시한다.
//                if (arg1 == null) {
//                    mMapController.setMapCenter(new NGeoPoint(126.978371, 37.5666091), 11);
//                } else {
//                    Toast.makeText(getApplicationContext(), arg1.toString(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onMapCenterChangeFine(NMapView arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onMapCenterChange(NMapView arg0, NGeoPoint arg1) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onAnimationStateChange(NMapView arg0, int arg1, int arg2) {
//                // TODO Auto-generated method stub
//
//            }
//        });
//
//        //맵뷰의 이벤트리스너의 정의.
//        mMapView.setOnMapViewTouchEventListener(new NMapView.OnMapViewTouchEventListener() {
//
//            @Override
//            public void onTouchDown(NMapView arg0, MotionEvent arg1) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {
//
//            }
//
//            @Override
//            public void onSingleTapUp(NMapView arg0, MotionEvent arg1) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onScroll(NMapView arg0, MotionEvent arg1, MotionEvent arg2) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onLongPressCanceled(NMapView arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onLongPress(NMapView arg0, MotionEvent arg1) {
//                // TODO Auto-generated method stub
//
//            }
//        });
//
//
//        mMapController = mMapView.getMapController();
//
//        //지도 데이터를 받아올시 처리(?)
//        super.setMapDataProviderListener(new OnDataProviderListener() {
//
//            @Override
//            //findPlacemarkAtLocation가 호출 되었을때 Callback으로 호출된다.
//            public void onReverseGeocoderResponse(NMapPlacemark arg0, NMapError arg1) {
//                // TODO Auto-generated method stub
//
//            }
//        });

    }
    private class getAddrTask extends AsyncTask<String,String,String>{
        private ProgressDialog dialog;
        private String addr;
        protected void onPreExecute() {
            //쿼리를 날려 데이터를 만들기 전에 미리 작동되는 부분으로
            //로딩 창을 만들어서 띄워줌
            dialog = new ProgressDialog(NMap.this);
            //          dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("Loading");
            dialog.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... args){
            this.addr = getAddr(args[0],args[1]);
            return this.addr;
        }
        protected void onPostExecute(String addr) {
            new ResultTask().execute(this.addr);
            dialog.dismiss();
        }
    }
    private class ResultTask extends AsyncTask<String, String, Boolean> {
        //검색 버튼 누를시에 작동하는 AsyncTask로 resultData를 만든다.
//        ArrayAdapter<String> resultAdapter;
        private ProgressDialog dialog;
        private NGeoPoint nPoint;
        @Override
        protected void onPreExecute() {
            //쿼리를 날려 데이터를 만들기 전에 미리 작동되는 부분으로
            //로딩 창을 만들어서 띄워줌
            dialog = new ProgressDialog(NMap.this);
            //          dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("Loading");
            dialog.show();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(String... args){
            if(!setLonLat(args[0]))
                return false;
            return true;
        }
        protected void onPostExecute(Boolean jud) {
            dialog.dismiss();
            int markerId = NMapPOIflagType.PIN;
            // set POI data
            if(!jud){
                System.out.println("지도에서 못찾음 ");
                Toast.makeText(getApplicationContext(),"지도에 찾을수가 없넼ㅋㅋ어쩌나" , Toast.LENGTH_SHORT).show();
//                startActivity(intent);
                return;
            }
            float lonData = Float.parseFloat(lon);
            float latData = Float.parseFloat(lat);
            System.out.println(lonData+"||||"+latData);
            NMapPOIdata poiData = new NMapPOIdata(1, nMapViewerResourceProvider);
            poiData.beginPOIdata(1);
            poiData.addPOIitem(lonData, latData, result.getCompanyName(), markerId, 0);
            poiData.endPOIdata();
            poiDataOverlay = overlayManager.createPOIdataOverlay(poiData, null);
            poiDataOverlay.showAllPOIdata(0);
        }
    }
    public String getAddr(String inAddr,String companyName) {
        String addr = "";
        String aurl = "";
        String mapxml = "";
        String tmpCompanyName = "";
        ArrayList<Integer> count = new ArrayList<Integer>();
        ArrayList<String> tmpString = new ArrayList<String>();

        for(int j=0;j<companyName.length();j++) {
            if (companyName.charAt(j) == '(') {
                for (int i = j; i < companyName.length(); i++) {
                    //괄호안의 문자열 모두 제거 ex)(주),(시험),(교정)
                    if (companyName.charAt(i) == ')') {
                        count.add(j);
                        count.add(i);
                        break;
                    }
                }
            }
        }

        if(count.size()!=0){
            for(int i=0;i<count.size();i+=2){
                //System.out.println(companyName.charAt(count.get(i))+companyName.charAt(count.get(i+1)));
                tmpString.add(companyName.substring(count.get(i),count.get(i+1)+1));
            }
        }
        for(int i=0;i<tmpString.size();i++) {
            System.out.println(tmpString.get(i));
            companyName = companyName.replaceAll(tmpString.get(i),"");
        }
        System.out.println(companyName);
        try{
            inAddr = URLEncoder.encode(inAddr, "UTF-8");
            companyName = URLEncoder.encode(companyName,"UTF-8");
        } catch(Exception e){
            System.out.println("murlEncodingError!");
        }
        aurl ="http://openapi.naver.com/search?key=00dfd51ceb4a1fad21b784348704cd64&query="+inAddr+companyName+"&target=local&start=1&display=1";

        System.out.println("AddrUrl"+aurl);
        try {
            URL mapXmlUrl = new URL(aurl);  //URL연결하고 받아오고 하는 부분들은 import가 필요하다. java.net.*
            HttpURLConnection urlConn = (HttpURLConnection) mapXmlUrl.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("POST");

            int len = urlConn.getContentLength();  //받아오는 xml의 길이

            if (len > 0) {
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    mapxml += inputLine;  //한글자씩 읽어옵니다
                }
                if (mapxml != null) {
                    if (mapxml.indexOf("</item>") > -1) {   //item이 있으면 좌표를 받아와야지
                        int first = 1;
                        addr = mapxml.substring(mapxml.indexOf("address") + 8, mapxml.indexOf("</address>")); //경도 잘라오기
                    }
                }
                System.out.println(addr);
                br.close();  //버퍼리더 닫기
            }
            return addr;
        } catch (Exception e) {
            System.out.println("getAddr"+e.toString());
            return e.toString();
        }

    }
    public boolean setLonLat(String addr) {
        String inlat = "";  //위도
        String inlon = "";  //경도
        String murl = ""; //연결할 URL
        String mapxml = ""; //받아온 xml

        if(addr.length()==0){
//            Toast.makeText(getApplicationContext(),"지도에 찾을수가 없넼ㅋㅋ어쩌나" , Toast.LENGTH_SHORT).show();
//            startActivity(intent);
            return false;
        }

        try{
            addr = URLEncoder.encode(addr, "UTF-8");
        } catch(Exception e){
            System.out.println("murlEncodingError!");
        }
        murl = "http://openapi.map.naver.com/api/geocode.php?key="+API_KEY+"&encoding=utf-8&coord=latlng&query="+addr;

        System.out.println("MapUrl"+murl);
            try {
            URL mapXmlUrl = new URL(murl);  //URL연결하고 받아오고 하는 부분들은 import가 필요하다. java.net.*
            HttpURLConnection urlConn = (HttpURLConnection) mapXmlUrl.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("POST");

            int len = urlConn.getContentLength();  //받아오는 xml의 길이

            if (len > 0) {
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    mapxml += inputLine;  //한글자씩 읽어옵니다
                }
                if (mapxml != null) {
                    if (mapxml.indexOf("</item>") > -1) {   //item이 있으면 좌표를 받아와야지
                        int first = 1;
                        inlon = mapxml.substring(mapxml.indexOf("<x>") + 3, mapxml.indexOf("</x>")); //경도 잘라오기
                        inlat = mapxml.substring(mapxml.indexOf("<y>") + 3, mapxml.indexOf("</y>")); //위도 잘라오기
                    }
                }
                System.out.println("inlon:"+inlon+"inlat"+inlat);
                br.close();  //버퍼리더 닫기
            }
            this.lon = new String(inlon);
            this.lat = new String(inlat);
            return true;
        } catch (Exception e) {
            System.out.println("위도 경로 불러오기"+e.toString());
            return false;
        }
    }
}