package com.synics.gymp.cash;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivityEx;
import com.synics.gymp.activity.MainActivity;
import com.synics.gymp.bluec.CBillHead;
import com.synics.gymp.bluec.CBillPayment;
import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.bluec.CDataSource;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.COrigBill;
import com.synics.gymp.bluec.CPrint;
import com.synics.gymp.iface.IAuthorizeCallBack;
import com.synics.gymp.window.AuthorizeDialog;
import com.synics.gymp.window.InpMoneyDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymainActivity extends BaseActivityEx implements InpMoneyDialog.IInpMoneyCallBack {
    private CBillHead mBillHead = COrigBill.get().getMbillHead();
    private CBillPayment mBillPayment = COrigBill.get().getMbillPayment();
    private CDataSource dsBillHead = new CDataSource();
    private CDataSource dsBillPayment = new CDataSource();

    private LinearLayout tvPayinfo = null;
    private LinearLayout tvPayEndinfo = null;

    private boolean bBlLogin = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_paymain;
    }

    protected void initViewAfter() {
        findViewByTag("closebutton").setVisibility(View.GONE);
        findViewByTag("printbtn").setVisibility(View.GONE);
        tvPayinfo = (LinearLayout) findViewByTag("payinfo");
        tvPayEndinfo = (LinearLayout) findViewByTag("payendinfo");
        dsBillHead.setOnDataChange(new CDataSource.OnDataChange() {
            @Override
            public void onChange(CDataSource sender) {
                setValueByViewTag("yfje", mBillHead.mYfje);
                setValueByViewTag("dfje", mBillHead.mDfje);
                setValueByViewTag("billstate", CGlobalData.getBillStateName(mBillHead.$("billstate")));
                //tvPayinfo.setText(mBillPayment.getPayInfo());
                //tvPayEndinfo.setText(mBillPayment.getPayInfo());
                mBillPayment.setPayViewList(PaymainActivity.this, tvPayinfo);
                mBillPayment.setPayViewList(PaymainActivity.this, tvPayEndinfo);
                if (mBillHead.getInt("billstate") == 2) {
                    findViewByTag("printbtn").setVisibility(View.VISIBLE);
                    findViewByTag("payingpanel").setVisibility(View.GONE);
                    findViewByTag("payedpanel").setVisibility(View.VISIBLE);
                    findViewByTag("paycash").setVisibility(View.GONE);
                    ((TextView) findViewByTag("title")).setText("支付完成");
                }
                if (!mBillPayment.isEmpty()) {
                    findViewByTag("btgd").setVisibility(View.GONE);
                }
            }
        });
        mBillHead.addDataSource(dsBillHead);
        mBillPayment.addDataSource(dsBillPayment);
        getAllChildViews();
        dsBillHead.bindViews(allDatasetView);
        if (CGlobalData.ispaycash == 1) {
            findViewByTag("paycash").setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onBeforeDestroy() {
        mBillHead.removeDataSource(dsBillHead);
        mBillPayment.removeDataSource(dsBillPayment);
    }

    @Override
    protected boolean isDisabledBack() {
        return !mBillPayment.isEmpty();
    }

    //撤单
    public void doCd(View source) {
        if (mBillPayment.isEmpty()) {
            askBox("确定撤销当前订单吗?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == -1) {
                        try {
                            showProgress("");
                            COrigBill.get().cancelBill(CConfig.HTTPEVENT_BILLCANCEL, PaymainActivity.this);
                        } catch (Exception ex) {
                            toast(ex.getMessage());
                        }
                    }
                }
            });
        }else{
            callActivity(PaymainActivity.this, PayrefundActivity.class, CConfig.HTTPEVENT_BILLREFUND);
        }
    }

    //挂单
    public void doGd(View source) {
        if (!mBillPayment.isEmpty()) {
            toastLong("订单发生支付后，不能再挂单");
            return;
        }
        askBox("确定挂起当前订单吗?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == -1) {
                    try {
                        showProgress("");
                        COrigBill.get().applyUpdate(CConfig.HTTPEVENT_BILLUPDATEGD, 1, PaymainActivity.this);
                    } catch (Exception ex) {
                        toast(ex.getMessage());
                    }
                }
            }
        });
    }

    //银联支付
    public void doPayYl(View source) {
        new InpMoneyDialog(CConfig.HTTPEVENT_PAYBANK, this, null, this);
    }

    //聚合支付
    public void doPayJh(View source) {
        new InpMoneyDialog(CConfig.HTTPEVENT_PAYMARGE, this, null, this);
    }

    //积分卡支付
    public void doPayJfk(View source) {
        callActivity(this, PayscoreActivify.class, CConfig.HTTPEVENT_PAYSCORE);
    }

    //积分卡支付
    public void doPayLq(View source) {
        callActivity(this, PaycouponActivify.class, CConfig.HTTPEVENT_PAYCOUPON);
    }

    //补录支付
    public void doPayBl(View source) {
        if (!bBlLogin) {
            new AuthorizeDialog(0, this, null,
                    new IAuthorizeCallBack() {
                        @Override
                        public void onConfirm(int requestCode, String uid, String uName) {
                            CGlobalData.managerid = uid;
                            CGlobalData.managername = uName;
                            bBlLogin = true;
                            callActivity(PaymainActivity.this, PayotherActivify.class, CConfig.HTTPEVENT_PAYOTHER);
                        }
                    });
        } else {
            callActivity(PaymainActivity.this, PayotherActivify.class, CConfig.HTTPEVENT_PAYOTHER);
        }
    }

    public void doPayCash(View source) {
        callActivity(PaymainActivity.this, PaycashActivify.class, CConfig.HTTPEVENT_PAY);
    }

    public void doPrint(View source) {
        try {
            showProgress("");
            CHttpUtil.Post("iymp/billconfirm.jsp", new String[]{
                    "billkey", mBillHead.$("billkey"), "billid", mBillHead.$("billid"),
                    "billtype", mBillHead.$("billtype")}, this, CConfig.HTTPEVENT_CONFIRM);
        } catch (Exception ex) {
            toast(ex.getMessage());
        }
    }

    protected void handleHttp(int requestCode, CJson json) {
        try {
            switch (requestCode) {
                case CConfig.HTTPEVENT_BILLCANCEL:    //撤单成功
                case CConfig.HTTPEVENT_BILLUPDATEGD:  //挂单成功
                    close(RESULT_OK);
                    break;
                case CConfig.HTTPEVENT_PAYBANK:
                case CConfig.HTTPEVENT_PAYMARGE:
                    mBillHead.setInt("billstate", json.getJsonObj().getInt("billstate"));
                    mBillPayment.loadData(json.getJsonObj().getJSONArray("payment"), false);
                    if (json.getJsonObj().getInt("paystate") == 1) {
                        toast("支付已经存在");
                    }
                    break;
                case CConfig.HTTPEVENT_CONFIRM:
                    CPrint.printRecerpt(COrigBill.get());
                    if (json.getJsonObj().getInt("toerp") != 0) {
                        toast(json.getJsonObj().getString("toerpmsg"));
                    }
                    close(RESULT_OK);
                    break;
            }
        } catch (Exception ex) {
            toastLong(ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case CConfig.HTTPEVENT_PAYSCORE:
                    case CConfig.HTTPEVENT_PAYCOUPON:
                    case CConfig.HTTPEVENT_PAYOTHER:
                    case CConfig.HTTPEVENT_PAY:
                        break;
                    case CConfig.HTTPEVENT_BILLREFUND:  //退款完成
                        close(RESULT_OK);
                        break;
                }
            }
        } catch (Exception ex) {
            toastLong(ex.getMessage());
        }
    }

    private Boolean isSuccess(String result) {
        try {
            JSONObject o = new JSONObject(result);
            if (o.getInt("rspCode") != 0) {
                //toast("交易失败或被取消");
                return false;
            }
        } catch (Exception ex) {

        }
        return true;
    }

    private void doYlSale(final String transType, String transAmount) {
        try {
            JSONObject json = new JSONObject();
            json.put("transType", transType);
            json.put("transAmount", transAmount);
            json.put("appId", CConfig.APPIDNAME);//appId
            json.put("orderNo", CGlobalData.getReqCode());
            String result = MainActivity.payHelper.doTrans(json.toString());
            Log.e("doJhSale", result);
            if (isSuccess(result)) {
                showProgress("");
                CHttpUtil.Post("iymp/paybank.jsp", new String[]{
                        "billkey", mBillHead.$("billkey"), "data", result}, this, CConfig.HTTPEVENT_PAYBANK);
            }
        } catch (RemoteException e) {
            toast(e.getMessage());
        } catch (JSONException e) {
            toast(e.getMessage());
        }
    }

    private void doJhSale(final String transType, String transAmount) {
        try {
            JSONObject json = new JSONObject();
            json.put("transType", transType);
            json.put("transAmount", transAmount);
            json.put("appId", CConfig.APPIDNAME);//appId
            json.put("orderNo", CGlobalData.getReqCode());
            String result = MainActivity.payHelper.doTrans(json.toString());
            Log.e("doJhSale", result);
            if (isSuccess(result)) {
                showProgress("");
                CHttpUtil.Post("iymp/paymarge.jsp", new String[]{
                        "billkey", mBillHead.$("billkey"), "data", result}, this, CConfig.HTTPEVENT_PAYMARGE);
            }
        } catch (RemoteException e) {
            toast(e.getMessage());
        } catch (JSONException e) {
            toast(e.getMessage());
        }
    }

    public void onInpMoney(int requestCode, String inpMoney) {
        try {
            switch (requestCode) {
                case CConfig.HTTPEVENT_PAYBANK:    //银联支付
                    doYlSale("SALE", inpMoney);
                    break;
                case CConfig.HTTPEVENT_PAYMARGE:   //聚合支付
                    doJhSale("JH_MICRO_PAY", inpMoney);
                    break;
            }
        } catch (Exception ex) {
            toastLong(ex.getMessage());
        }
    }

    public void doShowBillInfo(View source) {
        callActivity(this, BillinfoActivify.class, new String[]{"billkey=" + mBillHead.$("billkey")});
    }

}
