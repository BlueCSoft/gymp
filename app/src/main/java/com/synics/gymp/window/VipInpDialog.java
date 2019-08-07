package com.synics.gymp.window;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.iface.IAuthorizeCallBack;

/**
 * Created by Administrator on 2019/7/23.
 */

public class VipInpDialog extends ConditionDialog {
    EditText metKh;
    ImageView mivScan;
    IAuthorizeCallBack mCallback = null;
    public VipInpDialog(int requestCode, BaseActivity parent, View topPanel, IAuthorizeCallBack mCallback){
        super(requestCode, R.layout.condition_findvip, parent, topPanel);
        this.mCallback = mCallback;
    }

    @Override
    protected void initData(View contentView, BaseActivity parent) {
        try {
            metKh = (EditText) contentView.findViewWithTag("kh");
            mivScan = (ImageView) contentView.findViewWithTag("scan");
            mivScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    mCallback.onConfirm(requestCode,"","");
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void doReturn(View v) {
        try {
            if(metKh.getText().toString().equals("")){
                toast("请输入卡号或手机号");
                return;
            }
            dismiss();
            mCallback.onConfirm(this.requestCode,metKh.getText().toString(),"");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
