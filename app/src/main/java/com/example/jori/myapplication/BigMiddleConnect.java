package com.example.jori.myapplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Created by Administrator on 2015-02-12.
 */
public class BigMiddleConnect {
    //대분류와 그에 해당하는 세부분야들을 갖고 있으며 get메소드들과 set메소드를 통해
    //대분류에 해당하는 세부분야를 만들고, 원하는 Data를 리턴해준다.
    private String bigId;
    private String bigname;
    private String[] middleList;
    private String[] middleidList;
    private String serviceUrl;
    private String serviceKey;

    public BigMiddleConnect(String bigname,String bigId,String url,String key){
        //인자로 받는 bigname과 bigId를 통해 생성
        this.bigname = bigname;
        this.bigId = bigId;
        this.serviceUrl = url;
        this.serviceKey = key;
    }

    public void setMiddleList(String url,String key) {
        //현재 객체의 bigId를 통해서 현재 객체의 대분류에 해당하는 세부분야의 쿼리문을 작성하고
//            //스트링 리스트로 저장
//            String inserviceUrl = "http://ibtk.kr/examAdmitDetail_api/";
//            String inserviceKey = "790f112628bf15b344699602ef729cb1?";
        String middleStr = "";
        String middleidStr = "";
        String query = "model_query_pageable={enable:true,pageSize:100,sortOrders:[{property:\"middleid\",direction:1}]}&model_query_distinct=middlename&model_query={\"bigid\":\"" + this.bigId + "\"}";
        String instrUrl = url + key + query;
        if(this.bigname.equals("인정분야")){
            middleList = new String[1];
            middleidList = new String[1];
            middleidList[0] = new String("-1");
            middleList[0] = new String("세부분야");
            return;
        }
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
            middleList = new String[middleNameSt.countTokens()+1];
            middleList[0] = new String("세부분야");
            middleidList = new String[middleIdSt.countTokens()+1];
            middleidList[0] = new String("-1");
            int i = 1;
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

    private String getDataFromUrl(String url) throws IOException {
        // HttpURLConnection을 사용해서 주어진 URL에 대한 입력 스트림을 얻는다.
        //얻어진 입력스트림을 한줄씩 읽어서 page에 저장하고 return한다.
        HttpURLConnection conn = null;
        System.out.println(url);
        try {
            URL u = new URL(url);
            conn = (HttpURLConnection)u.openConnection();
            conn.setDoOutput(false);
            BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf));

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
