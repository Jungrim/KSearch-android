package com.example.jori.myapplication;

import java.io.Serializable;

/**
 * Created by admin on 2015-02-07.
 */
public class ResultData implements Serializable {
    private String companyName;
    private String addr;
    private String accreditNumber;
    private String phoneNumber;
    private boolean check = false;

    public ResultData(String companyName,String addr,String accreditNumber,String phoneNumber){
        this.companyName = companyName;
        this.addr = addr;
        this.accreditNumber = accreditNumber;
        this.phoneNumber = phoneNumber;
    }
    public void setCheck(){ check = !check; }
    public boolean getCheck(){ return check; }

    public String getCompanyName(){
        return companyName+"\n";
    }
    public String getAccreditNumber() { return accreditNumber+"\n"; }
    public String getPhoneNumber() { return phoneNumber+"\n";}
    public String getData(){
        return accreditNumber + "\n" + companyName + "\n" + addr +"\n";
    }
    public String getAddr(){
        return addr+"\n";
    }
}
