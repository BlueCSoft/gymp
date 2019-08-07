package com.synics.gymp.bluec;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.synics.gymp.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/30.
 */

public class CBillPayment extends CDataSet {

    public CBillPayment() {
        __mData = new ArrayList<PosPaymentBean>();
        getFields(PosPaymentBean.class);
    }

    protected DataBean createRowObject(boolean isAppend) {
        if (!isAppend) __mData.clear();
        return new PosPaymentBean();
    }

    @Override
    protected void doInsert() {
        PosPaymentBean rowObj = (PosPaymentBean) createRowObject(true);
        ((List<PosPaymentBean>) __mData).add(rowObj);
        mRecNo = __mData.size();
    }

    protected void loadRowData(JSONObject row) throws Exception {
        try {
            PosPaymentBean rowObj = (PosPaymentBean) createRowObject(true);
            dataToRow(row, rowObj);
            ((List<PosPaymentBean>) __mData).add(rowObj);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<PosPaymentBean> getData() {
        return (List<PosPaymentBean>) __mData;
    }

    @Override
    protected void OnLoadData() throws Exception {
        __rowid = getMaxInt("seq");
    }

    @Override
    protected void AfterInsert() throws Exception {
        if(__MDataSet!=null) {
            try {
                setLong("billkey", __MDataSet.getLong("billkey"));
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }

    public String getPayInfo(){
        StringBuffer buf = new StringBuffer();
        try{
            for(int i=0;i<getRecordCount();i++){
                moveRecord(i+1);
                buf.append(String.format("%s %8.2f %s\n", CUtil.formatAlign(getString("paymentname"),8,0),getDouble("amount"),getString("lsh")));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return buf.toString();
    }

    public void setPayViewList(Context context,ViewGroup parent){
        try{
            parent.removeAllViews();
            for(int i=0;i<getRecordCount();i++){
                moveRecord(i+1);
                View view = LayoutInflater.from(context).inflate(R.layout.item_bill_payinfo, null, false);
                ((TextView)view.findViewWithTag("mc")).setText(CUtil.formatAlign(getString("paymentname"),8,0));
                ((TextView)view.findViewWithTag("je")).setText(getDouble("amount")+"");
                ((TextView)view.findViewWithTag("kh")).setText(getString("reqcode"));
                parent.addView(view);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void setRefundViewList(Context context,ViewGroup parent){
        try{
            parent.removeAllViews();
            for(int i=0;i<getRecordCount();i++){
                moveRecord(i+1);
                View view = LayoutInflater.from(context).inflate(R.layout.item_bill_payrefund, null, false);
                String zt = (getInt("paystatus")==-2)? "已退":"";
                ((TextView)view.findViewWithTag("zt")).setText(zt);
                ((TextView)view.findViewWithTag("mc")).setText(CUtil.formatAlign(getString("paymentname"),8,0));
                ((TextView)view.findViewWithTag("je")).setText(getDouble("amount")+"");
                ((TextView)view.findViewWithTag("kh")).setText(getString("reqcode"));
                parent.addView(view);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void setPayCardList(Context context,ViewGroup parent,String paymentcode){
        try{
            parent.removeViews(1,parent.getChildCount()-1);
            for(int i=0;i<getRecordCount();i++){
                moveRecord(i+1);
                if(getString("paymentcode").equals(paymentcode)) {
                    View view = LayoutInflater.from(context).inflate(R.layout.item_bill_cards, null, false);
                    ((TextView) view.findViewWithTag("kh")).setText(CUtil.formatAlign(getString("reqcode"), 8, 0));
                    ((TextView) view.findViewWithTag("je")).setText(getDouble("amount") + "");
                    ((TextView) view.findViewWithTag("ye")).setText(getString("balance"));
                    parent.addView(view);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    //检查是否存在某种支付方式
    public boolean exists(String paymentCode){
        return locate("paymentcode",paymentCode);
    }

    public boolean existsEx(String paymentCode){
        return locate(new String[]{"paymentcode","recatt"},new String[]{paymentCode,"0"});
    }
}
