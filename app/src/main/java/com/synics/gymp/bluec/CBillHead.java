package com.synics.gymp.bluec;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CBillHead extends CDataSet implements Serializable {
    public double mYfje = 0;                    //订单已付，待付金额
    public double mDfje = 0;

    public CBillHead() {
        __mData = new ArrayList<PosBillBean>();
        getFields(PosBillBean.class);
    }

    public List<PosBillBean> getData() {
        return (List<PosBillBean>) __mData;
    }

    protected DataBean createRowObject(boolean isAppend) {
        if (!isAppend) __mData.clear();
        return new PosBillBean();
    }

    protected void loadRowData(JSONObject row) throws Exception {
        try {
            PosBillBean rowObj = (PosBillBean) createRowObject(true);
            dataToRow(row, rowObj);
            ((List<PosBillBean>) __mData).add(rowObj);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    protected void doInsert() {
        PosBillBean rowObj = (PosBillBean) createRowObject(true);
        ((List<PosBillBean>) __mData).add(rowObj);
        mRecNo = __mData.size();
    }

    /* 是否小程序订单 */
    public boolean isYssBill() {
        boolean result = false;
        try {
            result = !isEmpty() && getInt("isyss") == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getStateName() throws Exception {
        try {
            return CGlobalData.getBillStateName(getRow().getString("billstate"));
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    /* 商品表变化通知 */
    @Override
    public void notifyMDataSet(CDataSet dataSet, boolean isLoad) {
        try {
            String cname = dataSet.getClass().getSimpleName();
            if (cname.equals("CBillProducts")) {
                if (!isLoad) {
                    setInt("bs", dataSet.getRecordCount());
                    setDouble("quantity", dataSet.getSumDouble("quantity"));
                    setDouble("totalamount", dataSet.getSumDouble("totalamount"));
                    setDouble("counterdiscount", dataSet.getSumDouble("counterdiscount"));
                    setDouble("vipdiscount", dataSet.getSumDouble("vipdiscount"));

                    setDouble("popzk", dataSet.getSumDouble("popzk"));
                    setDouble("rulezk", dataSet.getSumDouble("rulezk"));

                    setDouble("cstoreshare", dataSet.getSumDouble("cstoreshare"));
                    setDouble("csupplyshare", dataSet.getSumDouble("csupplyshare"));

                    if (dataSet.isEmpty()) {
                        setDouble("totaldiscount", 0);
                        setDouble("totaldisatt", 0);
                        setDouble("totaldiscountrate", 100);
                        setDouble("totalmaxdiscountrate", 100);
                        setDouble("tsupplysharerate", 0);
                        setDouble("tsupplyshare", 0);
                        setDouble("tstoreshare", 0);
                        setDouble("sumdiscount", 0);
                        setDouble("amount", 0);
                        mDfje = 0;
                        notifyCalFieldChange("mdfje");
                    } else {
                        calTotalZk();
                    }
                }
            } else {
                mYfje = CUtil.round(dataSet.getSumDouble("amount"), 2);
                mDfje = CUtil.round(getDouble("amount") - mYfje, 2);
                notifyCalFieldChange("mdfje");
            }
            OnDataChange();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 新增记录之后 */
    @Override
    protected void AfterInsert() throws Exception {
        try {
            setLong("billkey", CGlobalData.origbillkey);
            setInt("arecno", 0);
            setInt("crecno", 0);
            setString("billid", CGlobalData.origbillid);
            setInt("isminus", 1);  //负数为退单
            setInt("billtype", CGlobalData.billtype);

            setString("orgid", CGlobalData.orgid);
            setString("storeid", CGlobalData.storeid);
            setString("counterid", CGlobalData.counterid);
            setString("mposid", CConfig.SMPOSID);
            setString("supplyid", CGlobalData.supplyid);
            setString("cashierid", CGlobalData.cashierid);
            setString("cashier", CGlobalData.cashier);
            setString("salesid", CGlobalData.salesid);
            setString("sales", CGlobalData.sales);

            setString("erpcashid", CGlobalData.erpcashid);

            setString("billdate", CGlobalData.time);
            setString("belongdate", CGlobalData.time);
            setString("stime", CUtil.getSTime());

            setInt("clienttype", 0);   //匿名客户

            setInt("billstate", CGlobalData.billtype);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void append() {
        if (isEmpty()) {
            try {
                insert();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //计算整单折扣
    private void calTotalZk() throws Exception {
        double zkl = getDouble("totaldiscountrate");  //整单折扣率
        zkl = (zkl == 0) ? 100 : zkl;
        double zhje = CUtil.round(getDouble("totalamount") - getDouble("vipdiscount") - getDouble("counterdiscount"), 2);

        double zdzke = zkl;
        int zkatt = getInt("totaldisatt");
        if (zkatt == 0) {  //折扣率
            zdzke = CUtil.round(zhje * (1 - zkl / 100), CGlobalData.digiNum);
        }

        setDouble("totaldiscount", zdzke);

        int ftatt = getInt("totaldisshareatt");
        double fkje = getDouble("tsupplysharerate");
        switch (ftatt) {
            case 0:
                fkje = zdzke;
                break;
            case 1:
                fkje = 0;
                break;
            case 2:
                fkje = CUtil.mul(zdzke, fkje / 100, 2);
                break;
        }

        setDouble("tsupplyshare", fkje);
        setDouble("tstoreshare", zdzke - fkje);

        if (__DDataSet != null) __DDataSet.shareData(zdzke, fkje, zdzke - fkje);  // 分摊折扣到每个商品
        //合计折扣
        zdzke = CUtil.round(getDouble("vipdiscount") + getDouble("counterdiscount") +
                getSumDouble("popzk") + getSumDouble("rulezk") + zdzke, CGlobalData.digiNum);

        setDouble("sumdiscount", zdzke);
        setDouble("amount", CUtil.round(getDouble("totalamount") - zdzke, CGlobalData.digiNum));
        mDfje = CUtil.round(getDouble("amount"), 2);
        mYfje = 0;
        notifyCalFieldChange("mdfje");
    }

    //设置总价折扣
    public void calTotalDiscount() throws Exception {
        calTotalZk();
        OnDataChange();
    }

}
