package com.synics.gymp.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.synics.gymp.R;
import com.synics.gymp.setting.ChangePwdActivity;
import com.synics.gymp.setting.HelpActivity;
import com.synics.gymp.setting.ParamsetActivity;
import com.synics.gymp.setting.PoscodesetActivity;
import com.synics.gymp.setting.SysinfoActivity;

import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener{

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(Bundle args) {
        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId(){
        return R.layout.fragment_setting;
    }

    @OnClick({R.id.id_set_poscode,R.id.id_set_param,R.id.id_set_changepwd,R.id.id_set_sysinfo,R.id.id_set_help,
            R.id.id_set_btexit})
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.id_set_poscode:
                callActivity(getActivity(),PoscodesetActivity.class);
                break;
            case R.id.id_set_param:
                callActivity(getActivity(),ParamsetActivity.class);
                break;
            case R.id.id_set_changepwd:
                callActivity(getActivity(),ChangePwdActivity.class);
                break;
            case R.id.id_set_sysinfo:
                callActivity(getActivity(),SysinfoActivity.class);
                break;
            case R.id.id_set_help:
                callActivity(getActivity(),HelpActivity.class);
                break;
            case R.id.id_set_btexit:
                msgBox("提示", "确定退出系统登录吗？", new String[]{"确定", "取消"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==-1) {
                                    close(Activity.RESULT_FIRST_USER+2);
                                }
                            }
                        });
                break;
        }
    }
}
