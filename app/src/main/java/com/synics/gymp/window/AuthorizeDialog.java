package com.synics.gymp.window;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.EditText;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.iface.IAuthorizeCallBack;
import com.synics.gymp.iface.IDataLoadCallback;

/**
 * Created by Administrator on 2019/7/13.
 */

public class AuthorizeDialog extends ConditionDialog implements IDataLoadCallback {

    EditText metUid;
    EditText metPwd;
    IAuthorizeCallBack mCallback = null;

    public AuthorizeDialog(int requestCode, BaseActivity parent, View topPanel,IAuthorizeCallBack mCallback) {
        super(requestCode, R.layout.dialog_authorize, parent, topPanel);
        this.mCallback = mCallback;
    }

    @Override
    protected void initData(View contentView, BaseActivity parent) {
        try {
            metUid = (EditText) contentView.findViewWithTag("uid");
            metPwd = (EditText) contentView.findViewWithTag("pwd");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void doReturn(View v) {
        try {
            if(metUid.getText().toString().equals("")){
                toast("请输入工号");
                return;
            }
            if(metPwd.getText().toString().equals("")){
                toast("请输入密码");
                return;
            }
            showProgress("");
            CHttpUtil.Post("icommon/ygmpuserlogin.jsp", new String[]{
                    "uid", metUid.getText().toString(), "pwd", metPwd.getText().toString()}, this, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void dataLoadCallback(boolean isHttpCall, int requestCode, int responseCode, Object data) {
        hideProgress();
        if (responseCode < 0) {
            toastLong(data.toString());
            return;
        }
        try {
            if (isHttpCall) {
                CJson json = new CJson();
                if (json.toJsonObject(data.toString())) {
                    dismiss();
                    if(mCallback!=null){
                        mCallback.onConfirm(0,json.getString("uid"),json.getString("uname"));
                    }
                } else {
                    toast(json.getErrorMsg());
                }
            }
        } catch (Exception ex) {
            toastLong(ex.getMessage());
        }
    }
}
