package com.synics.gymp.cash;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

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
 * Created by Administrator on 2019/7/14.
 */

public class PayotherActivify extends BaseActivityEx {
    private CBillHead mBillHead = COrigBill.get().getMbillHead();
    private CBillPayment mBillPayment = COrigBill.get().getMbillPayment();
    private EditText etInp;
    private ListView lvpayatt;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_payother;
    }

    @Override
    protected void initViewAfter() {
        setValueByViewTag("amount", mBillHead.$("amount"));
        setValueByViewTag("yfje", mBillHead.mYfje);
        setValueByViewTag("dfje", mBillHead.mDfje);

        etInp = (EditText) findViewByTag("inpedit");
        lvpayatt = (ListView) findViewByTag("payattlist");

        etInp.setText(mBillHead.mDfje + "");

        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_single_choice, CGlobalData.getPaymentsTo());
        lvpayatt.setAdapter(adapter);
        lvpayatt.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvpayatt.setItemChecked(0, true);
    }

    public void doPay(View source) {
        double je = Double.parseDouble(etInp.getText().toString());
        if (je < 0.01 || je > mBillHead.mDfje) {
            toast("支付金额只能在0.01-" + mBillHead.mDfje + "之间");
            return;
        }
        showProgress("");
        String sno = CGlobalData.getPaymentIdByPosition(lvpayatt.getCheckedItemPosition()+1);
        String sname = CGlobalData.getPaymentNameByPosition(lvpayatt.getCheckedItemPosition()+1);

        CHttpUtil.Post("iymp/payother.jsp", new String[]{
                "billkey", mBillHead.$("billkey"), "paymentcode", sno, "paymentname", sname, "payfee", je + "",
                "reqcode",CGlobalData.getReqCode(),"tradecode", "", "uid", CGlobalData.managerid, "uname", CGlobalData.managername}, this, 0);
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
