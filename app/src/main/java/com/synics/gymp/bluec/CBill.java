package com.synics.gymp.bluec;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/10/30.
 */

public class CBill extends CObject {
    protected CBillHead mbillHead;          //订单表头数据集
    protected CBillProducts mbillProducts;  //订单商品数据
    protected CBillPayment mbillPayment;    //订单支付数据

    public CBill() {
        this.mbillHead = new CBillHead();
        this.mbillProducts = new CBillProducts();
        this.mbillProducts.setMDataSet(this.mbillHead);
        this.mbillPayment = new CBillPayment();
        this.mbillPayment.setMDataSet(this.mbillHead);

        this.mbillHead.setDDataSet(this.mbillProducts);
    }

    public CBillProducts getMbillProducts() {
        return mbillProducts;
    }

    public CBillHead getMbillHead() {
        return mbillHead;
    }

    public CBillPayment getMbillPayment() {
        return mbillPayment;
    }

    public boolean loadFromJsonObj(JSONObject jsonObject) {
        mErrorCode = -1;
        try {
            if (!mbillHead.loadData(jsonObject.getJSONArray("head"), false)) {
                mErrorMsg = mbillHead.getErrorMsg();
            } else if (!mbillProducts.loadData(jsonObject.getJSONArray("products"), false)) {
                mErrorMsg = mbillProducts.getErrorMsg();
            } else if (!mbillPayment.loadData(jsonObject.getJSONArray("payment"), false)) {
                mErrorMsg = mbillPayment.getErrorMsg();
            } else {
                mErrorCode = 0;
                mbillHead.mDfje = CUtil.round(mbillHead.getDouble("amount") - mbillPayment.getSumDouble("amount"), 2);
            }
            CGlobalData.origbillkey = mbillHead.getInt("billkey");
            CGlobalData.origbillid = mbillHead.getString("billid");
            CGlobalData.billtype = mbillHead.getInt("billtype");
        } catch (Exception e) {
            mErrorCode = -1;
            mErrorMsg = e.getMessage();
        }
        return mErrorCode == 0;
    }

    public void emptyDataSet() {
        mbillHead.emptyDataSet();
        mbillProducts.emptyDataSet();
        mbillPayment.emptyDataSet();
    }

    public boolean dataIsChange() {
        return mbillHead.dataIsChange() || mbillProducts.dataIsChange() || mbillPayment.dataIsChange();
    }
}
