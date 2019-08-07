package com.synics.gymp.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.bluec.CUtil;

import butterknife.BindView;

public class LoginSettingActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.id_loginset_url1)
    EditText loginset_url1;
    @BindView(R.id.id_loginset_url2)
    EditText loginset_url2;
    @BindView(R.id.id_loginset_url3)
    EditText loginset_url3;
    @BindView(R.id.id_loginset_url4)
    EditText loginset_url4;
    @BindView(R.id.id_loginset_port)
    EditText loginset_port;

    @BindView(R.id.id_loginset_counterid)
    EditText loginset_counterid;

    @BindView(R.id.id_loginset_btok)
    TextView loginset_btok;
    @Override
    protected int getLayoutId(){
        return R.layout.activity_login_setting;
    }

    @Override
    protected void initView() {
        super.initView();
        if(!TextUtils.isEmpty(CConfig.SERVER_URL)){
            String ip = CConfig.SERVER_URL;
            int pos = ip.indexOf(":");
            if(pos>-1){
                loginset_port.setText(ip.substring(pos+1));
                ip = ip.substring(0,pos);
            }
            String[] ipd = ip.split("\\.");
            loginset_url1.setText(ipd[0]);
            loginset_url2.setText(ipd[1]);
            loginset_url3.setText(ipd[2]);
            loginset_url4.setText(ipd[3]);

            loginset_counterid.setText(CConfig.SMPOSID);
        }
        loginset_btok.setOnClickListener(this);
    }

    private boolean inputIsEmpty(String value){
        return CUtil.strIsEmpty(value);
    }

    private boolean checkData(){
        if(inputIsEmpty(loginset_url1.getText().toString())||inputIsEmpty(loginset_url2.getText().toString())||
                inputIsEmpty(loginset_url3.getText().toString())||inputIsEmpty(loginset_url4.getText().toString())){
            toast("服务器ip地址不正确");
            return false;
        }

        String mposid = loginset_counterid.getText().toString().trim();
        if(inputIsEmpty(mposid)){
            toast("终端编号不能为空");
            return false;
        }

        String ip = CUtil.joinArray(new String[]{loginset_url1.getText().toString().trim(),
                loginset_url2.getText().toString().trim(),
                loginset_url3.getText().toString().trim(),
                loginset_url4.getText().toString().trim()},".");
        String port = loginset_port.getText().toString();
        if(!inputIsEmpty(port)){
            ip += ":"+port;
        }

        SharedPreferences spf = getApplicationContext().getSharedPreferences(CConfig.SAPPID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString("SERVER_URL",ip);
        editor.putString("SMPOSID",mposid);
        editor.commit();

        CConfig.SERVER_URL = ip;
        CConfig.SMPOSID = mposid;
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_loginset_btok:
                if(checkData()) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }

    public void closeActivity(View source){
        setResult(RESULT_CANCELED);
        finish();
    }
}
