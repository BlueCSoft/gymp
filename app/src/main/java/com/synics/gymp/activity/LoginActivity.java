package com.synics.gymp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pax.dal.IDAL;
import com.pax.neptunelite.api.NeptuneLiteUser;
import com.synics.gymp.R;
import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.CLoginInfo;
import com.synics.gymp.iface.IDataLoadCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements IDataLoadCallback {

    @BindView(R.id.id_login_username_et)
    EditText et_uid;
    @BindView(R.id.id_login_password_et)
    EditText et_pwd;
    @BindView(R.id.id_login_btok)
    TextView tv_btOk;

    @BindView(R.id.id_login_savepwd)
    CheckBox cb_savepwd;

    @BindView(R.id.id_login_setting)
    TextView tv_setting;

    @BindView(R.id.id_login_alertinfo)
    TextView tv_error;

    public static IDAL idal = null;

    SharedPreferences spf = null;

    protected int getLayoutId(){
        return R.layout.activity_login;
    }

    private void doLogin(){
        String uid = et_uid.getText().toString();
        String pwd = et_pwd.getText().toString();
        //et_uid.setError(null);
        //et_pwd.setError(null);
        if(TextUtils.isEmpty(uid)){
            toastLong(getString(R.string.login_uiderrormsg));
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            toastLong(getString(R.string.login_pwderrormsg));
            return;
        }

        showProgress("");
        CHttpUtil.Post("icommon/ygmpcashierlogin.jsp",
                new String[]{"cashierid",uid,"mposid",CConfig.SMPOSID,"pwd",pwd},  //001A1001/001A1/888888
                LoginActivity.this,
                CConfig.HTTPEVENT_LOGIN);
    }
    @Override
    protected void initView(){
        try {
            idal = NeptuneLiteUser.getInstance().getDal(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        spf = getApplicationContext().getSharedPreferences(CConfig.SAPPID, Context.MODE_PRIVATE);
        //读取保存的参数
        CConfig.SERVER_URL = spf.getString("SERVER_URL","");
        CConfig.SMPOSID = spf.getString("SMPOSID","");
        if(spf.getInt("SAVEPWD",0)==1){
            et_uid.setText(spf.getString("LOGIN_UID",""));
            et_pwd.setText(spf.getString("LOGIN_PWD",""));
            cb_savepwd.setChecked(true);
        }
        tv_error.setVisibility(View.GONE);
        //登录按钮
        tv_btOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!TextUtils.isEmpty(CConfig.SERVER_URL))
                    doLogin();
                else
                    toastLong(getString(R.string.login_serverurlmsg));
            }
        });
        //设置按钮
        tv_setting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                callActivity(LoginActivity.this,LoginSettingActivity.class);
            }
        });
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_FIRST_USER+1){
            finish();
        }
    }

    public void dataLoadCallback(boolean isHttpCall, int requestCode, int responseCode, Object data){
        hideProgress();
        if(responseCode<0){
            toastLong(data.toString());
            return;
        }
        try {
            if(isHttpCall){
                CLoginInfo json = new CLoginInfo();
                if (json.saveToGlobalData(data.toString())) {
                    //登录成功，保存账号密码
                    SharedPreferences.Editor editor = spf.edit();
                    if(cb_savepwd.isChecked()){
                        editor.putInt("SAVEPWD",1);
                        editor.putString("LOGIN_UID",et_uid.getText().toString());
                        editor.putString("LOGIN_PWD",et_pwd.getText().toString());
                    }else{
                        editor.putInt("SAVEPWD",0);
                        editor.putString("LOGIN_UID","");
                        editor.putString("LOGIN_PWD","");
                    }
                    editor.commit();
                    callActivity(LoginActivity.this,MainActivity.class,1);
                }else{
                    tv_error.setText(json.getErrorMsg());
                    tv_error.setVisibility(View.VISIBLE);
                }
            }
        }catch (Exception ex){
            toastLong(ex.getMessage());
        }
    }
}
