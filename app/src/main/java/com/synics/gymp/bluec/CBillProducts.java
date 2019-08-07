package com.synics.gymp.bluec;

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
 * 订单商品
 * Created by Administrator on 2016/10/30.
 */

public class CBillProducts extends CDataSet {

    //private List<PosProductBean> data;

    public CBillProducts() {
        //data = new ArrayList<PosProductBean>();
        __mData = new ArrayList<PosProductBean>();
        getFields(PosProductBean.class);
    }

    public List<PosProductBean> getData() {
        return (List<PosProductBean>) __mData;
    }

    protected DataBean createRowObject(boolean isAppend) {
        if (!isAppend) __mData.clear();
        return new PosProductBean();
    }
/*
    @Override
    public Object getRow(int recNo) {
        return __mData.get(recNo - 1);
    }

    @Override
    protected void doLoadRowDataFrom(JSONObject jsonObject) throws Exception {
        dataToRow(jsonObject, data.get(mRecNo - 1));
    } */

    @Override
    protected void doInsert() {
        PosProductBean rowObj = (PosProductBean) createRowObject(true);
        ((List<PosProductBean>) __mData).add(rowObj);
        mRecNo = __mData.size();
    }

    @Override
    protected void OnLoadData() throws Exception {
        __rowid = getMaxInt("seq");
    }

    protected void loadRowData(JSONObject row) throws Exception {
        try {
            PosProductBean rowObj = (PosProductBean) createRowObject(true);
            dataToRow(row, rowObj);
            ((List<PosProductBean>) __mData).add(rowObj);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    protected void AfterInsert() throws Exception {
            try {
                if (__MDataSet != null) {
                    long key = __MDataSet.getLong("billkey");
                    setLong("billkey", key);
                    setInt("seq", __rowid++);
                }
                setDouble("quantity", 1);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
    }

    /*分摊总价折扣*/
    @Override
    public void shareData(double value0,double value1,double value2) {
        try {
            int nRecNo = mRecNo, ncount = getRecordCount();
            double sumv = __MDataSet.getDouble("amount");
            double bcje0 = 0, bcje1 = 0, bcje2 = 0;
            double v0 = value0, v1 = value1, v2 = value2;
            for (int i = 1; i <= ncount; i++) {
                moveRecord(i);
                double tv = getDouble("amount");
                if (i == ncount) {
                    bcje0 = v0;
                    bcje1 = v1;
                    bcje2 = v2;
                } else {
                    bcje0 = (sumv==0||value0==0)? 0: CUtil.divide(tv , sumv * value0, CGlobalData.digiNum);
                    bcje1 = (sumv==0||value1==0)? 0: CUtil.divide(tv , sumv * value1, CGlobalData.digiNum);
                    bcje2 = (sumv==0||value2==0)? 0: CUtil.divide(tv , sumv * value2, CGlobalData.digiNum);
                }
                setDouble("totaldiscount", bcje0);
                setDouble("tsupplyshare", bcje1);
                setDouble("tstoreshare", bcje2);

                double dssum = CUtil.roundEx(getDouble("vipdiscount") + getDouble("counterdiscount") + bcje0,2);
                setDouble("sumdiscount", dssum);
                setDouble("amount", CUtil.sub(tv,dssum,CGlobalData.digiNum));
                v0 -= bcje0;
                v1 -= bcje1;
                v2 -= bcje2;
            }
            mRecNo = nRecNo;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setGoodsViewList(Context context, ViewGroup parent){
        try{
            parent.removeAllViews();
            for(int i=0;i<getRecordCount();i++){
                moveRecord(i+1);
                View view = LayoutInflater.from(context).inflate(R.layout.item_bill_goodsinfo, null, false);
                ((TextView)view.findViewWithTag("dm")).setText(getString("poscode"));
                ((TextView)view.findViewWithTag("pm")).setText(getString("posname")+"");
                ((TextView)view.findViewWithTag("sl")).setText(getString("quantity"));
                ((TextView)view.findViewWithTag("yj")).setText(getString("iprice"));
                String zk = (getDouble("vipdiscount")!=0) ? "会员-"+getDouble("vipdiscount"):"";
                zk += (getDouble("counterdiscount")!=0) ? " 单品-"+getDouble("counterdiscount"):"";
                ((TextView)view.findViewWithTag("zk")).setText(zk.trim());
                ((TextView)view.findViewWithTag("sj")).setText(getString("price"));
                parent.addView(view);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
