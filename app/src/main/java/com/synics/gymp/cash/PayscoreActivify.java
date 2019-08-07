package com.synics.gymp.cash;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pax.face.scancodec.CodecActivity;
import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivityEx;
import com.synics.gymp.bluec.CBillHead;
import com.synics.gymp.bluec.CBillPayment;
import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.COrigBill;
import com.synics.gymp.iface.IAuthorizeCallBack;
import com.synics.gymp.window.AuthorizeDialog;
import com.synics.gymp.window.SwipecardDialog;

/**
 * Created by Administrator on 2019/7/20.
 */

public class PayscoreActivify extends BaseActivityEx {
    private CBillHead mBillHead = COrigBill.get().getMbillHead();
    private CBillPayment mBillPayment = COrigBill.get().getMbillPayment();
    private EditText etKhInp;
    private TextView tvaye;
    private TextView tvmaxje;
    private TextView tvgqhint;
    private EditText etJeInp;
    private LinearLayout llcardlist;

    double rye = 0;
    double maxje = 0;
    String rstatus = "";
    String rtype = "";
    boolean canPay = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activify_payscore;
    }

    @Override
    protected void initViewAfter() {
        setValueByViewTag("amount", mBillHead.$("amount"));
        setValueByViewTag("yfje", mBillHead.mYfje);
        setValueByViewTag("dfje", mBillHead.mDfje);

        etKhInp = (EditText) findViewByTag("khEdit");
        tvaye = (TextView) findViewByTag("aye");
        tvmaxje = (TextView) findViewByTag("maxje");
        tvgqhint = (TextView) findViewByTag("gqhint");
        etJeInp = (EditText) findViewByTag("jeEdit");
        llcardlist = (LinearLayout) findViewByTag("cardlist");

        etKhInp.setText("");
        tvaye.setText("");
        tvmaxje.setText("");
        etJeInp.setText("");
        mBillPayment.setPayCardList(this, llcardlist, "04");
        if (llcardlist.getChildCount() > 1) {
            llcardlist.setVisibility(View.VISIBLE);
        }
    }

    private void findCard(String kh) {
        showProgress("读取卡信息...");
        CHttpUtil.Post("icommon/ygmpgetscoreinfo.jsp", new String[]{
                "kh", kh, "type", "1"
        }, this, CConfig.HTTPEVENT_READSCORE);
    }

    //1000807185892154968
    //1100200100801660088184556073100
    public void doFind(View source) {
        //findCard("1000807185892154968");
        new SwipecardDialog(0, this, null,
                new IAuthorizeCallBack() {
                    @Override
                    public void onConfirm(int requestCode, String uid, String uName) {
                        findCard(uName);
                    }
                });
    }
    @Override
    protected void doActivityResult(int requestCode, Intent data) {
        try {
            switch (requestCode) {
                case CConfig.CALLEVENT_PAYSCORE:     //扫描
                    findCard(CGlobalData.returnData.getStringExtra("code"));
                    break;
            }
        } catch (Exception ex) {
            toast(ex.getMessage());
        }
    }

    protected void handleHttp(int requestCode, CJson json) {
        try {
            switch (requestCode) {
                case CConfig.HTTPEVENT_READSCORE:  //读取卡成功
                    if (mBillPayment.locate(new String[]{"paymentcode", "reqcode"}, new String[]{"04", json.getStringEx("rcardno")})) {
                        toastLong("同一张券不能重复支付");
                    } else {
                        etKhInp.setText(json.getStringEx("rcardno"));
                        tvaye.setText(json.getStringEx("rye"));
                        rstatus = json.getStringEx("rstatus");
                        rtype = json.getStringEx("rtype");
                        rye = json.getDoubleEx("rye");
                        maxje = json.getDoubleEx("rmaxje");

                        canPay = rstatus.equals("Y") || rstatus.equals("A");
                        if (!canPay) maxje = 0;

                        tvmaxje.setText(maxje + "");

                        double uje = (maxje >= mBillHead.mDfje) ? mBillHead.mDfje : maxje;

                        etJeInp.setText(uje + "");
                        if (rstatus.equals("Y")) {
                            etJeInp.setEnabled(true);
                            etJeInp.setFocusable(true);
                            etJeInp.requestFocus();
                            tvgqhint.setVisibility(View.GONE);
                        }else{
                            tvgqhint.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case CConfig.HTTPEVENT_PAYSCORE:
                    mBillHead.setInt("billstate", json.getJsonObj().getInt("billstate"));
                    mBillPayment.loadData(json.getJsonObj().getJSONArray("payment"), false);
                    if (mBillHead.getInt("billstate") >= 2) {
                        close(RESULT_OK);
                    } else {
                        canPay = false;
                        etKhInp.setText("");
                        tvaye.setText("");
                        tvmaxje.setText("");
                        etJeInp.setText("");
                        setValueByViewTag("yfje", mBillHead.mYfje);
                        setValueByViewTag("dfje", mBillHead.mDfje);
                        mBillPayment.setPayCardList(this, llcardlist, "04");
                        llcardlist.setVisibility(View.VISIBLE);
                        tvgqhint.setVisibility(View.GONE);
                    }
                    break;
            }
        } catch (Exception ex) {
            msgBox("", ex.getMessage());
        }
    }

    public void doScan(View source) {
        callActivity(this, CodecActivity.class, CConfig.CALLEVENT_PAYSCORE);
    }

    public void doPay(View source) {
        if (mBillPayment.locate(new String[]{"paymentcode", "reqcode"}, new String[]{"04", etKhInp.getText().toString()})) {
            toastLong("同一张券不能重复支付");
        } else {
            if (canPay) {
                double payvalues = Double.parseDouble(etJeInp.getText().toString());
                double payje = (payvalues > mBillHead.mDfje) ? mBillHead.mDfje : payvalues;
                showProgress("");
                CHttpUtil.Post("iymp/payscoreorcoupon.jsp", new String[]{
                        "type", "1", "kh", etKhInp.getText().toString(), "billkey", mBillHead.$("billkey"),
                        "billid", mBillHead.$("billid"), "payje", payje + "", "payvalues", tvaye.getText().toString(),
                        "payprice", tvaye.getText().toString(), "memo", rtype
                }, this, CConfig.HTTPEVENT_PAYSCORE);
            }
        }
    }


}
