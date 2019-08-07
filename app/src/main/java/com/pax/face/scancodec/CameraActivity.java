package com.pax.face.scancodec;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.iface.IConditionSelect;

/**
 * Created by Administrator on 2019/7/7.
 */

public class CameraActivity extends AppCompatActivity implements IConditionSelect {
    TextView tvInfo;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置横屏模式以及全屏模式
        setContentView(R.layout.fragment_scancodec);//设置View
        tvInfo = (TextView)findViewById(R.id.fragment_scancodec_res_text);
        //((LinearLayout)findViewById(R.id.fragment_scancodec_surfaceview)).addView(new CameraView(this,this));
    }
    @Override
    public void conditionSelect(int requestCode, Boolean isConfirm, View source, int position, String value){
        tvInfo.setText(value);
    }
    @Override
    public Boolean conditionCheck(int requestCode,Boolean isConfirm, View source, int position, String value){
        return true;
    }
}
