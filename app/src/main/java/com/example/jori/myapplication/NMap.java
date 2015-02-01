package com.example.jori.myapplication;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;

public class NMap extends NMapActivity {
    private static final String API_KEY = "770d3836366fcee226c976b9d6624d7c";
    private NMapView mMapView;
    private NMapController mMapController;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //네이버 지도 MapView를 생성해준다.
        mMapView = new NMapView(this);
        //지도에서 ZoomControll을 보여준다
        mMapView.setBuiltInZoomControls(true, null);

        //네이버 OPEN API 사이트에서 할당받은 KEY를 입력하여 준다.
        mMapView.setApiKey(API_KEY);

        //이 페이지의 레이아웃을 네이버 MapView로 설정해준다.
        setContentView(mMapView);
        //네이버 지도의 클릭이벤트를 처리 하도록한다.
        mMapView.setClickable(true);

        //맵의 상태가 변할때 이메소드를 탄다.
        mMapView.setOnMapStateChangeListener(new NMapView.OnMapStateChangeListener() {

            @Override
            public void onZoomLevelChange(NMapView arg0, int arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onMapInitHandler(NMapView arg0, NMapError arg1) {
                // TODO Auto-generated method stub
                //오류없이 네이버 지도가 초기화 되었다면 특정 좌표의 특정 Zoom으로 위치를 표시한다.
                if (arg1 == null) {
                    mMapController.setMapCenter(new NGeoPoint(126.978371, 37.5666091), 11);
                } else {
                    Toast.makeText(getApplicationContext(), arg1.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMapCenterChangeFine(NMapView arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onMapCenterChange(NMapView arg0, NGeoPoint arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStateChange(NMapView arg0, int arg1, int arg2) {
                // TODO Auto-generated method stub

            }
        });

        //맵뷰의 이벤트리스너의 정의.
        mMapView.setOnMapViewTouchEventListener(new NMapView.OnMapViewTouchEventListener() {

            @Override
            public void onTouchDown(NMapView arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {

            }

            @Override
            public void onSingleTapUp(NMapView arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(NMapView arg0, MotionEvent arg1, MotionEvent arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLongPressCanceled(NMapView arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLongPress(NMapView arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

            }
        });


        mMapController = mMapView.getMapController();

        //지도 데이터를 받아올시 처리(?)
        super.setMapDataProviderListener(new OnDataProviderListener() {

            @Override
            //findPlacemarkAtLocation가 호출 되었을때 Callback으로 호출된다.
            public void onReverseGeocoderResponse(NMapPlacemark arg0, NMapError arg1) {
                // TODO Auto-generated method stub

            }
        });
    }
}