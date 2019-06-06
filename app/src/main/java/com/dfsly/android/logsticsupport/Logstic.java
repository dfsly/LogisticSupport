package com.dfsly.android.logsticsupport;

public class Logstic {
    private String mNo;
    private int id;
    private int mManpower;
    private int mAmmunition;
    private int mRation;
    private int mParts;
    private int mQuickRepair;
    private int mQuickDone;
    private int mContract;
    private int mEquipment;
    private int mCoin;
    private int h;
    private int m;
    public boolean isSave=false;

//    public boolean isSave(){
//        return  isSave;
//    }
//    public void setSave(boolean b){
//        isSave=b;
//    }

    public String getNo() {
        return mNo;
    }

    public void setNo(String No) {
        mNo = No;
    }

    public int getManpower() {
        return mManpower;
    }

    public void setManpower(int Manpower) {
        mManpower = Manpower;
    }

    public int getAmmunition() {
        return mAmmunition;
    }

    public void setAmmunition(int Ammunition) {
        mAmmunition = Ammunition;
    }

    public int getRation() {
        return mRation;
    }

    public void setRation(int Ration) {
        mRation = Ration;
    }

    public int getParts() {
        return mParts;
    }

    public void setParts(int Parts) {
        mParts = Parts;
    }

    public int getQuickRepair() {
        return mQuickRepair;
    }

    public void setQuickRepair(int QuickRepair) {
        mQuickRepair = QuickRepair;
    }

    public int getQuickDone() {
        return mQuickDone;
    }

    public void setQuickDone(int QuickDone) {
        mQuickDone = QuickDone;
    }

    public int getContract() {
        return mContract;
    }

    public void setContract(int Contract) {
        mContract = Contract;
    }

    public int getEquipment() {
        return mEquipment;
    }

    public void setEquipment(int Equipment) {
        mEquipment = Equipment;
    }

    public int getCoin() {
        return mCoin;
    }

    public void setCoin(int Coin) {
        mCoin = Coin;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
}
