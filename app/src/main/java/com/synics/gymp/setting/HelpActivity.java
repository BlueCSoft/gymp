package com.synics.gymp.setting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;

import butterknife.BindView;

public class HelpActivity extends BaseActivity {
    @BindView(R.id.id_set_help_exit)
    ImageView tv_exit;
    protected int getLayoutId() {
        return R.layout.activity_help;
    }
    @Override
    protected void initView() {
        tv_exit.setOnClickListener(closeEvent);
    }
}
