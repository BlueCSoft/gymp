package com.synics.gymp.activity;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pax.pay.service.aidl.PayHelper;
import com.synics.gymp.R;
import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.fragment.BaseFragment;
import com.synics.gymp.fragment.CashFragment;
import com.synics.gymp.fragment.QueryFragment;
import com.synics.gymp.fragment.SettingFragment;
import com.synics.gymp.fragment.TransFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.id_txt_tab_cash)
    TextView tv_tab_cash;
    @BindView(R.id.id_txt_tab_query)
    TextView tv_tab_query;
    @BindView(R.id.id_txt_tab_trans)
    TextView tv_tab_trans;
    @BindView(R.id.id_txt_tab_setting)
    TextView tv_tab_setting;

    @BindView(R.id.id_ly_main_content)
    FrameLayout fr_main_content;

    private AlertDialog dialog;
    public static PayHelper payHelper = null;
    private String APPIDNAME = "com.pax.up.stdgd";

    private long exitTime = 0;
    private int selectedIndex = 0;
    private BaseFragment fgcash,fgquery,fgtrans,fgsetting;
    private FragmentManager fManager;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                toast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                close(RESULT_FIRST_USER+1,new String[]{"test"},new String[]{"123"});
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected int getLayoutId(){
        return R.layout.activity_main;
    }

    @Override
    protected void initViewBefore(){
        fManager = getSupportFragmentManager();
    }

    @Override
    protected void initView(){
        tv_tab_cash.performClick();   //模拟一次点击，既进去后选择第一项
        bindservice();
    }

    //重置所有文本的选中状态
    private void setSelected(){
        tv_tab_cash.setSelected(false);
        tv_tab_query.setSelected(false);
        tv_tab_trans.setSelected(false);
        tv_tab_setting.setSelected(false);
    }

    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(fgcash != null)fragmentTransaction.hide(fgcash);
        if(fgquery != null)fragmentTransaction.hide(fgquery);
        if(fgtrans != null)fragmentTransaction.hide(fgtrans);
        if(fgsetting != null)fragmentTransaction.hide(fgsetting);
    }

    @OnClick({R.id.id_txt_tab_cash, R.id.id_txt_tab_query, R.id.id_txt_tab_trans,R.id.id_txt_tab_setting})
    @Override
    public void onClick(View v) {
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        setSelected();
        switch (v.getId()){
            case R.id.id_txt_tab_cash:
                selectedIndex = 0;
                tv_tab_cash.setSelected(true);
                if(fgcash == null){
                    fgcash = new CashFragment();
                    fTransaction.add(R.id.id_ly_main_content,fgcash);
                }else{
                    fTransaction.show(fgcash);
                }
                break;
            case R.id.id_txt_tab_query:
                selectedIndex = 1;
                tv_tab_query.setSelected(true);
                if(fgquery == null){
                    fgquery = new QueryFragment();
                    fTransaction.add(R.id.id_ly_main_content,fgquery);
                }else{
                    fTransaction.show(fgquery);
                }
                break;
            case R.id.id_txt_tab_trans:
                selectedIndex = 2;
                tv_tab_trans.setSelected(true);
                if(fgtrans == null){
                    fgtrans = new TransFragment();
                    fTransaction.add(R.id.id_ly_main_content,fgtrans);
                }else{
                    fTransaction.show(fgtrans);
                }
                break;
            case R.id.id_txt_tab_setting:
                selectedIndex = 3;
                tv_tab_setting.setSelected(true);
                if(fgsetting == null){
                    fgsetting = new SettingFragment();
                    fTransaction.add(R.id.id_ly_main_content,fgsetting);
                }else{
                    fTransaction.show(fgsetting);
                }
                break;
        }
        fTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (selectedIndex) {
            case 0:
                fgcash.onActivityResult(requestCode,resultCode,data);
                break;
            case 1:
                fgquery.onActivityResult(requestCode,resultCode,data);
                break;
            case 2:
                fgtrans.onActivityResult(requestCode,resultCode,data);
                break;
            case 3:
                fgsetting.onActivityResult(requestCode,resultCode,data);
                break;
        }
    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            payHelper = PayHelper.Stub.asInterface(arg1);
            //toast("聚合支付服务绑定成功");
            if (dialog.isShowing()) {
                dialog.cancel();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            payHelper = null;
            if (dialog.isShowing()) {
                dialog.cancel();
            }
        }
    };

    private void bindservice() {
        if (payHelper == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("服务绑定中请稍等。。。");
            dialog = builder.create();
            dialog.show();
            Intent intent = new Intent();
            intent.setAction("com.pax.pay.SERVICE_ZHZF");
            intent.setPackage(APPIDNAME);//"com.pax.up.std"
            bindService(intent, conn, Service.BIND_AUTO_CREATE);
        } else {
            Intent intent = new Intent();
            intent.setAction("com.pax.pay.SERVICE_ZHZF");
            intent.setPackage(APPIDNAME);//"com.pax.up.std"
            boolean isSuccess = bindService(intent, conn, Service.BIND_AUTO_CREATE);
            if (!isSuccess) {
               toast("聚合支付服务绑定失败");
            }
        }
    }
}
