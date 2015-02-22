package com.example.jori.myapplication;

import java.io.Serializable;

/**
 * Created by admin on 2015-02-07.
 */
public class ResultData implements Serializable {
    private String companyName;
    private String chargeName;
    private String rangePower;
    private String addr;
    private String accreditNumber;
    private String phoneNumber;
    private String activityName;
    private boolean check ;

    public ResultData(String companyName,String addr,String accreditNumber,String phoneNumber,String chargeName,String rangePower,Boolean check,String activityName){
        this.companyName = companyName;
        this.addr = addr;
        this.accreditNumber = accreditNumber;
        this.phoneNumber = phoneNumber;
        this.chargeName = chargeName;
        this.rangePower = rangePower;
        this.check = check;
        this.activityName = activityName;
    }
    public void setCheck(){ check = !check; }
    public boolean getCheck(){ return check; }
    public String getActivityName() { return activityName;}
    public String getCompanyName(){
        return companyName+"\n";
    }
    public String getAccreditNumber() { return accreditNumber+"\n"; }
    public String getPhoneNumber() { return phoneNumber+"\n";}
    public String getChargeName(){ return chargeName+"\n";}
    public String getRangePower(){ return rangePower+"\n";}
    public String getData(){
        return accreditNumber + "\n" + companyName + "\n" + addr +"\n" + phoneNumber+ "\n";
    }
    public String getAddr(){
        return addr+"\n";
    }
}
