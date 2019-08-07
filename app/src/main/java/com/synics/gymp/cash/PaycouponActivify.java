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

/**
 * Created by Administrator on 2019/8/1.
 */

public class PaycouponActivify extends BaseActivityEx {
    private CBillHead mBillHead = COrigBill.get().getMbillHead();
    private CBillPayment mBillPayment = COrigBill.get().getMbillPayment();
    private EditText etKhInp;
    private TextView tvaye;
    private TextView tvmaxje;
    private EditText etJeInp;
    private LinearLayout llcardlist;
    double rye = 0;
    double maxje = 0;
    String rstatus = "";
    String rtype = "";
    boolean canPay = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activify_paycoupon;
    }

    @Override
    protected void initViewAfter() {
        setValueByViewTag("amount", mBillHead.$("amount"));
        setValueByViewTag("yfje", mBillHead.mYfje);
        setValueByViewTag("dfje", mBillHead.mDfje);

        etKhInp = (EditText) findViewByTag("khEdit");
        tvaye = (TextView) findViewByTag("aye");
        tvmaxje = (TextView) findViewByTag("maxje");
        etJeInp = (EditText) findViewByTag("jeEdit");
        llcardlist = (LinearLayout) findViewByTag("cardlist");

        etKhInp.setText("");
        tvaye.setText("");
        tvmaxje.setText("");
        etJeInp.setText("");

        mBillPayment.setPayCardList(this, llcardlist, "05");
        if (llcardlist.getChildCount() > 1) {
            llcardlist.setVisibility(View.VISIBLE);
        }
    }

    private void findCard(String kh) {
        if (mBillPayment.locate(new String[]{"paymentcode", "reqcode"}, new String[]{"05", kh})) {
            toastLong("同一张券不能重复支付");
        } else {
            showProgress("读取卡信息...");
            CHttpUtil.Post("icommon/ygmpgetscoreinfo.jsp", new String[]{
                    "kh", kh, "type", "2"
            }, this, CConfig.HTTPEVENT_READSCORE);
        }
    }

    public void doFind(View source) {
        if (!etKhInp.getText().equals("")) {
            findCard(etKhInp.getText().toString());
        }
    }

    @Override
    protected void doActivityResult(int requestCode, Intent data) {
        try {
            switch (requestCode) {
                case CConfig.CALLEVENT_PAYCOUPON:     //扫描
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
                    }
                    break;
                case CConfig.HTTPEVENT_PAYCOUPON:   //支付成功
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
                        setValueByViewTag("yfje", mBillHead.mDfje);
                        setValueByViewTag("dfje", mBillHead.mDfje);
                        mBillPayment.setPayCardList(this, llcardlist, "05");
                        llcardlist.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        } catch (Exception ex) {
            msgBox("", ex.getMessage());
        }
    }

    public void doScan(View source) {
        callActivity(this, CodecActivity.class, CConfig.CALLEVENT_PAYCOUPON);
    }

    public void doPay(View source) {
        if (mBillPayment.locate(new String[]{"paymentcode", "reqcode"}, new String[]{"05", etKhInp.getText().toString()})) {
            toastLong("同一张券不能重复支付");
        } else {
            if (canPay) {
                double payvalues = Double.parseDouble(etJeInp.getText().toString());
                double payje = (payvalues > mBillHead.mDfje) ? mBillHead.mDfje : payvalues;
                showProgress("");
                CHttpUtil.Post("iymp/payscoreorcoupon.jsp", new String[]{
                        "type", "2", "kh", etKhInp.getText().toString(), "billkey", mBillHead.$("billkey"),
                        "billid", mBillHead.$("billid"), "payje", payje + "", "payvalues", payvalues + "",
                        "payprice", tvaye.getText().toString(), "memo", rtype
                }, this, CConfig.HTTPEVENT_PAYCOUPON);
            }
        }
    }
}
