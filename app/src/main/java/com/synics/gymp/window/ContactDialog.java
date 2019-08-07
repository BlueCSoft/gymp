package com.synics.gymp.window;

import android.view.View;
import android.widget.EditText;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.iface.IAuthorizeCallBack;

/**
 * Created by Administrator on 2019/7/30.
 */

public class ContactDialog extends ConditionDialog {
    EditText metXm;
    EditText metDh;
    IAuthorizeCallBack mCallback = null;

    private void setDefault(String xm, String dh) {
        metXm.setText(xm);
        metDh.setText(dh);
    }

    public ContactDialog(int requestCode, BaseActivity parent, View topPanel, String xm, String dh, IAuthorizeCallBack mCallback) {
        super(requestCode, R.layout.dialog_contact, parent, topPanel);
        setDefault(xm,dh);
        this.mCallback = mCallback;
    }

    @Override
    protected void initData(View contentView, BaseActivity parent) {
        try {
            metXm = (EditText) contentView.findViewWithTag("xm");
            metDh = (EditText) contentView.findViewWithTag("dh");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void doReturn(View v) {
        try {
            dismiss();
            mCallback.onConfirm(requestCode, metXm.getText().toString(), metDh.getText().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
