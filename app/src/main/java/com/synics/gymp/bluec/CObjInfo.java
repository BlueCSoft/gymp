package com.synics.gymp.bluec;

/**
 * Created by Administrator on 2016/10/25.
 * 基本对象信息
 */

public class CObjInfo {
    private String mObjId = "";
    private String mObjName = "";
    private String mShowName = "";
    private String mBarCode = "";
    private double mPrice = 0;
    private int mPriceAtt = 0;
    public CObjInfo(String objid, String objname) {
        this.mObjId = objid;
        this.mObjName = objname;
    }

    public CObjInfo(String objid, String objname,String showName,String barCode) {
        this.mObjId = objid;
        this.mObjName = objname;
        this.mShowName = showName;
        this.mBarCode = barCode;
    }

    public CObjInfo(String objid, String objname,String showName,String barCode,double price,int priceAtt) {
        this.mObjId = objid;
        this.mObjName = objname;
        this.mShowName = showName;
        this.mBarCode = barCode;
        this.mPrice = price;
        this.mPriceAtt = priceAtt;
    }

    public String getObjId() {
        return mObjId;
    }

    public String getObjName() {
        return mObjName;
    }

    public String getShowName() {
        return mShowName;
    }

    public String getBarCode() {
        return mBarCode;
    }

    public int getmPriceAtt() {
        return mPriceAtt;
    }

    public double getmPrice() {
        return mPrice;
    }
}
