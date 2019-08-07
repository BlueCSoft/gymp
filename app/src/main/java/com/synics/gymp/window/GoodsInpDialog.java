package com.synics.gymp.window;

import android.view.View;
import android.widget.EditText;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.iface.IAuthorizeCallBack;

/**
 * Created by Administrator on 2019/7/31.
 */

public class GoodsInpDialog extends ConditionDialog {
    EditText metCode;
    EditText metName;
    EditText metSpec;
    EditText metSize;
    EditText metColor;

    IAuthorizeCallBack mCallback = null;

    private void setDefault(String code, String name, String spec, String size, String color) {
        metCode.setText(code);
        metName.setText(name);
        metSpec.setText(spec);
        metSize.setText(size);
        metColor.setText(color);
    }

    public GoodsInpDialog(int requestCode, BaseActivity parent, View topPanel,
                          String code, String name, String spec, String size, String color, IAuthorizeCallBack mCallback) {
        super(requestCode, R.layout.dialog_goodsinp, parent, topPanel);
        setDefault(code, name, spec, size, color);
        this.mCallback = mCallback;
    }

    @Override
    protected void initData(View contentView, BaseActivity parent) {
        try {
            metCode = (EditText) contentView.findViewWithTag("code");
            metName = (EditText) contentView.findViewWithTag("name");
            metSpec = (EditText) contentView.findViewWithTag("spec");
            metSize = (EditText) contentView.findViewWithTag("nsize");
            metColor = (EditText) contentView.findViewWithTag("color");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void doReturn(View v) {
        try {
            dismiss();
            String value = String.format("%s~%s~%s~%s~%s~123",metCode.getText().toString(),metName.getText().toString(),
                    metSpec.getText().toString(),metSize.getText().toString(),metColor.getText().toString());
            if(mCallback!=null) {
                mCallback.onConfirm(requestCode, "", value);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
