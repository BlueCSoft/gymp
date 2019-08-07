package com.synics.gymp.window;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.bluec.CObject;
import com.synics.gymp.bluec.COrigBill;
import com.synics.gymp.bluec.CUtil;

/**
 * Created by Administrator on 2019/7/17.
 */

public class InpMoneyDialog extends ConditionDialog {
    EditText metInp;
    TextView mtvInfo;
    double maxValue;

    IInpMoneyCallBack mCallback = null;

    public interface IInpMoneyCallBack {
        void onInpMoney(int requestCode, String inpMoney);
    }

    public InpMoneyDialog(int requestCode, BaseActivity parent, View topPanel, IInpMoneyCallBack mCallback) {
        super(requestCode, R.layout.dialog_inpmoney, parent, topPanel);
        this.mCallback = mCallback;
        this.maxValue = maxValue;
    }

    @Override
    protected void initData(View contentView, BaseActivity parent) {
        try {
            maxValue = COrigBill.get().getMbillHead().mDfje;
            metInp = (EditText) contentView.findViewWithTag("inpedit");
            mtvInfo = (TextView) contentView.findViewWithTag("info");
            mtvInfo.setText("金额范围：0.01-" + maxValue);
            metInp.setText(maxValue+"");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void doReturn(View v) {
        try {
            double f = Double.parseDouble(metInp.getText().toString());
            if (f < 0.01 || f > maxValue) {
                toast(mtvInfo.getText().toString());
                return;
            }
            dialog.dismiss();
            int payfee = (int)(f*100);
            mCallback.onInpMoney(requestCode, payfee+"");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
