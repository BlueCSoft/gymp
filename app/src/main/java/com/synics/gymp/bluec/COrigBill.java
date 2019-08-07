package com.synics.gymp.bluec;

import com.synics.gymp.iface.IDataLoadCallback;

/**
 * 当前订单JavaBean
 * Created by Administrator on 2016/10/30.
 */

public class COrigBill extends CBill {
    private static COrigBill sOrigBill = null;

    public static COrigBill get() {
        if (sOrigBill == null) sOrigBill = new COrigBill();
        return sOrigBill;
    }

    private COrigBill() {
    }

    public String getJson() throws Exception {
        String hJson = mbillHead.getJson();
        String gJson = mbillProducts.getJson();
        String pJson = mbillPayment.getJson();
        return "{\"head\":" + hJson + ",\"products\":" + gJson + ",\"payment\":" + pJson + "}";
    }

    public void applyUpdate(int rquestCode,int issaved, IDataLoadCallback pcallView) throws Exception{
        try {
            String data = getJson();
            CHttpUtil.Post("iymp/billsave.jsp", new String[]{
                    "billkey", mbillHead.getString("billkey"), "billid", mbillHead.getString("billid"),
                    "issaved", issaved + "", "data", data
            }, pcallView, rquestCode);
        }catch (Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

    public void cancelBill(int rquestCode,IDataLoadCallback pcallView) throws Exception{
        try {
            CHttpUtil.Post("iymp/billundo.jsp", new String[]{
                    "billkey", mbillHead.getString("billkey"), "billid", mbillHead.getString("billid")
            }, pcallView, rquestCode);
        }catch (Exception ex){
            throw new Exception(ex.getMessage());
        }
    }
}
