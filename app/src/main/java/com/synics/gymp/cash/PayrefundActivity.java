package com.synics.gymp.cash;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivityEx;
import com.synics.gymp.activity.MainActivity;
import com.synics.gymp.bluec.CBillPayment;
import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.COrigBill;
import com.synics.gymp.iface.IAuthorizeCallBack;
import com.synics.gymp.window.AuthorizeDialog;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2019/8/4.
 */

public class PayrefundActivity extends BaseActivityEx {
    LinearLayout llGoodslist;
    LinearLayout llPayslist;
    CBillPayment mbillPayment = new CBillPayment();

    String authuid = "";
    String authuname = "";
    int billstate = 0;
    Boolean isCancel = true;
    @Override
    protected int getLayoutId() {
        return R.layout.activify_payrefund;
    }

    @Override
    public void closeActivity(View source) {
        if(billstate<=1){
            close(RESULT_OK);
        }else{
            close();
        }
    }
    @Override
    protected void initViewAfter() {
        try {
            billstate = COrigBill.get().getMbillHead().getInt("billstate");

            isCancel = COrigBill.get().getMbillHead().getString("paycounterid").equals(CGlobalData.counterid);

            mbillPayment.cloneData(COrigBill.get().getMbillPayment(), null);
            llGoodslist = (LinearLayout) findViewByTag("goodslist");
            llPayslist = (LinearLayout) findViewByTag("payslist");

            JSONObject head = new JSONArray(COrigBill.get().getMbillHead().getJson()).getJSONObject(0);
            head.put("billstate", CGlobalData.getBillStateName(head.getString("billstate")));
            setValueByViewTag(new String[]{"billid", "billdate", "counterid", "cashier", "sales", "vipid", "customer", "billstate",
                            "quantity", "totalamount", "vipdiscount", "counterdiscount", "popzk", "rulezk", "totaldiscount", "sumdiscount", "amount"},
                    head);
            COrigBill.get().getMbillProducts().setGoodsViewList(this, llGoodslist);
            COrigBill.get().getMbillPayment().setRefundViewList(this, llPayslist);
        } catch (Exception ex) {
            toastLong(ex.getMessage());
        }
    }

    private Boolean isSuccess(JSONObject o) {
        try {
            if (o.getInt("rspCode") != 0) {
                //toast("交易失败或被取消");
                return false;
            }
        } catch (Exception ex) {
            msgBox(ex.getMessage());
        }
        return true;
    }

    //银联退款
    private void doYlRefund() {
        try {
            JSONObject json = new JSONObject();

            json.put("transAmount", (int) (mbillPayment.getDouble("amount") * 100) + "");
            json.put("appId", CConfig.APPIDNAME);//appId
            if(isCancel) {
                json.put("transType", "VOID");
                json.put("voucherNo", mbillPayment.$("lsh"));
            }else{
                json.put("transType", "VOID");
                json.put("origRefNo", mbillPayment.$("tradecode"));
                json.put("origDate", mbillPayment.$("paytime").substring(3,5)+mbillPayment.$("paytime").substring(6,8));
            }
            json.put("orderNo", CGlobalData.getReqCode());
            String result = MainActivity.payHelper.doTrans(json.toString());
            Log.e("doJhSale", result);
            JSONObject o = new JSONObject(result);
            if (isSuccess(o)) {
                showProgress(mbillPayment.$("paymentname") + "撤销...");
                CHttpUtil.Post("iymp/payrefund.jsp", new String[]{
                        "billkey", mbillPayment.$("billkey"), "seq", mbillPayment.$("seq"),
                        "tradecode", "", "lsh", o.getString("voucherNo"),
                        "authuid", authuid, "authuname", authuname}, this, CConfig.HTTPEVENT_PAYBANK);
            }
        } catch (Exception e) {
            msgBox(e.getMessage());
        }
    }

    //聚合退款
    private void doJhRefund() {
        try {
            JSONObject json = new JSONObject();
            json.put("transType", "JH_REFUND");
            json.put("appId", CConfig.APPIDNAME);//appId
            json.put("transAmount", (int) (mbillPayment.getDouble("amount") * 100) + "");
            json.put("orderAmount", (int) (mbillPayment.getDouble("amount") * 100) + "");
            json.put("transactionId", mbillPayment.$("tradecode"));
            json.put("orderNo", CGlobalData.getReqCode());
            String result = MainActivity.payHelper.doTrans(json.toString());
            Log.e("doJhSale", result);
            JSONObject o = new JSONObject(result);
            if (isSuccess(o)) {
                showProgress(mbillPayment.$("paymentname") + "撤销...");
                CHttpUtil.Post("iymp/payrefund.jsp", new String[]{
                        "billkey", mbillPayment.$("billkey"), "seq", mbillPayment.$("seq"),
                        "tradecode", o.getString("transactionId"), "lsh", o.getString("voucherNo"),
                        "authuid", authuid, "authuname", authuname}, this, CConfig.HTTPEVENT_PAYBANK);
            }
        } catch (Exception e) {
            msgBox(e.getMessage());
        }
    }

    //卡券退款
    public void doKqRefund() {
        try {
            showProgress(mbillPayment.$("paymentname") + "撤销...");
            String type = mbillPayment.$("paymentcode").equals("04") ? "1" : "2";
            CHttpUtil.Post("iymp/payscoreorcoupon.jsp", new String[]{
                    "type", "1", "kh", mbillPayment.$("reqcode"), "billkey", mbillPayment.$("billkey"),
                    "billid", mbillPayment.$("billid"), "payje", mbillPayment.$("amount"), "type", type,
                    "authuid", authuid, "authuname", authuname
            }, this, CConfig.HTTPEVENT_PAYSCORE);
        } catch (Exception e) {
            msgBox(e.getMessage());
        }
    }

    //其它退款撤销
    private void doQtRefund(String seq) {
        try {
            showProgress((seq.equals("0")) ? "撤销检查..." : mbillPayment.$("paymentname") + "撤销...");
            CHttpUtil.Post("iymp/payrefund.jsp", new String[]{
                    "billkey", mbillPayment.$("billkey"), "seq", seq,
                    "tradecode", "", "lsh", "",
                    "authuid", authuid, "authuname", authuname}, this, CConfig.HTTPEVENT_PAYBANK);
        } catch (Exception e) {
            msgBox(e.getMessage());
        }
    }

    private void doRefund() {
        try {
            Boolean b = false;
            String seq = "0";
            for (int i = 1; !b && i <= mbillPayment.getRecordCount(); i++) {
                mbillPayment.moveRecord(i);
                if (mbillPayment.getInt("paystatus") == 2) {
                    b = true;
                    seq = mbillPayment.$("seq");
                }
            }
            if (!b) {
                doQtRefund("0");
            } else {
                String paycode = mbillPayment.$("paymentcode");
                if (mbillPayment.getInt("recatt") == 1 || paycode.equals("01")) {
                    doQtRefund(seq);
                } else {
                    switch (paycode) {
                        case "0204":
                        case "0301":
                            doJhRefund();
                            break;
                        case "03":
                            doYlRefund();
                            break;
                        case "04":
                        case "05":
                            doKqRefund();
                            break;
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    public void doPay(View source) {
        if(billstate<=2) {
            doRefund();
        }else{
            new AuthorizeDialog(0, this, null,
                    new IAuthorizeCallBack() {
                        @Override
                        public void onConfirm(int requestCode, String uid, String uName) {
                            authuid = uid;
                            authuname = uName;
                            doRefund();
                        }
                    });
        }
    }

    protected void handleHttp(int requestCode, CJson json) {
        try {
            billstate = json.getJsonObj().getInt("billstate");
            if (billstate <= -2) {   //退货完成
                toast("撤单完成");
                close(RESULT_OK);
            } else {
                mbillPayment.loadData(json.getJsonObj().getJSONArray("payment"), false);
                doRefund();
            }
        } catch (Exception ex) {
            msgBox(ex.getMessage());
        }
    }
}
