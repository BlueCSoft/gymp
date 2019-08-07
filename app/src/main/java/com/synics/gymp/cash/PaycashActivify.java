package com.synics.gymp.cash;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivityEx;
import com.synics.gymp.bluec.CBillHead;
import com.synics.gymp.bluec.CBillPayment;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.COrigBill;

/**
 * Created by Administrator on 2019/8/2.
 */

public class PaycashActivify extends BaseActivityEx {
    private CBillHead mBillHead = COrigBill.get().getMbillHead();
    private CBillPayment mBillPayment = COrigBill.get().getMbillPayment();
    private EditText etInp;
    private TextView tvZl;
    private double zfje = 0;
    private double zlje = 0;
    @Override
    protected int getLayoutId() {
        return R.layout.activify_paycash;
    }

    @Override
    protected void initViewAfter() {
        setValueByViewTag("amount", mBillHead.$("amount"));
        setValueByViewTag("yfje", mBillHead.mYfje);
        setValueByViewTag("dfje", mBillHead.mDfje);
        tvZl = (TextView) findViewByTag("zl");

        etInp = (EditText) findViewByTag("inpedit");

        etInp.setText(mBillHead.mDfje + "");

        etInp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    zfje = Double.parseDouble(s.toString());
                }catch (Exception ex){
                    zfje = 0;
                }
                zlje = 0;
                if(zfje>0){
                    if(zfje>mBillHead.mDfje){
                        zlje = zfje - mBillHead.mDfje;
                    }
                }
                tvZl.setText(zlje+"");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void doPay(View source) {
        if (zfje <= 0) {
            toast("支付金额必须大于0");
            return;
        }
        showProgress("");

        CHttpUtil.Post("iymp/paycash.jsp", new String[]{
                "billkey", mBillHead.$("billkey"), "payfee", (zfje-zlje) + "","payvalues",zfje+"",
                "reqcode",CGlobalData.getReqCode(), "uid", CGlobalData.managerid, "uname", CGlobalData.managername}, this, 0);
    }

    @Override
    protected void handleHttp(int requestCode, CJson json) {
        try {
            mBillHead.setInt("billstate", json.getJsonObj().getInt("billstate"));
            mBillPayment.loadData(json.getJsonObj().getJSONArray("payment"), false);
            if (json.getJsonObj().getInt("paystate") == 1) {
                toast("支付已经存在");
            }
            close(RESULT_OK);
        } catch (Exception ex) {
            toast(ex.getMessage());
        }
    }
}
