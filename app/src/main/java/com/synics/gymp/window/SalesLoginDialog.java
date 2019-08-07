package com.synics.gymp.window;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.iface.IAuthorizeCallBack;
import com.synics.gymp.iface.IDataLoadCallback;

/**
 * Created by Administrator on 2019/7/23.
 */

public class SalesLoginDialog extends ConditionDialog implements IDataLoadCallback {
    ListView mlvSales;
    EditText metPwd;
    IAuthorizeCallBack mCallback = null;

    public SalesLoginDialog(int requestCode, BaseActivity parent, View topPanel, IAuthorizeCallBack mCallback) {
        super(requestCode, R.layout.dialog_saleslogin, parent, topPanel);
        this.mCallback = mCallback;
    }

    @Override
    protected void initData(View contentView, BaseActivity parent) {
        try {
            mlvSales = (ListView) contentView.findViewWithTag("saleslist");
            metPwd = (EditText) contentView.findViewWithTag("pwd");

            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (parent, android.R.layout.simple_list_item_single_choice, CGlobalData.getSales());

            mlvSales.setAdapter(adapter);
            mlvSales.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            String duid = (CGlobalData.salesid.equals(""))? CGlobalData.cashierid:CGlobalData.salesid;
            mlvSales.setItemChecked(CGlobalData.getSalesPosition(duid),true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void doReturn(View v) {
        try {
            String sid = CGlobalData.getSalesIdByPosition(mlvSales.getCheckedItemPosition());
            if(sid.equals(CGlobalData.cashierid)){
                dismiss();
                if(mCallback!=null){
                    mCallback.onConfirm(this.requestCode,CGlobalData.cashierid,CGlobalData.cashier);
                }
            }else {
                if (metPwd.getText().toString().equals("")) {
                    toast("请输入密码");
                    return;
                }
                showProgress("");
                CHttpUtil.Post("icommon/ygmpsaleslogin.jsp", new String[]{
                        "salesid", sid, "pwd", metPwd.getText().toString()}, this, 0);
            }
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
                        mCallback.onConfirm(this.requestCode,json.getString("uid"),json.getString("uname"));
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
