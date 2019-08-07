package com.pax.face.printer;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;

public class CutPaperFragment extends BaseActivity implements OnClickListener {

    private Button cutPaperBt, cutModeBt;
    private TextView resultTv;
    private RadioGroup radioGroup;
    private RadioButton fullCut, partialCut;
    private int mode = 0;

    @Override
    protected int getLayoutId(){
        return R.layout.fragment_printer_cutpaper;
    }

    //初始化视图
    @Override
    protected void initView() {
        cutPaperBt = (Button) findViewById(R.id.fragment_printer_cutpaper_bt);
        cutModeBt = (Button) findViewById(R.id.fragment_printer_getcutmode_bt);
        resultTv = (TextView) findViewById(R.id.fragment_printer_result_tv);

        radioGroup = (RadioGroup) findViewById(R.id.fragment_printer_cutpaper_rg);
        fullCut = (RadioButton) findViewById(R.id.fragment_printer_fullpaper_rb);
        partialCut = (RadioButton) findViewById(R.id.fragment_printer_partialpaper_rb);

        cutPaperBt.setOnClickListener(this);
        cutModeBt.setOnClickListener(this);

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.fragment_printer_fullpaper_rb:
                        mode = 0;
                        break;
                    case R.id.fragment_printer_partialpaper_rb:
                        mode = 1;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fragment_printer_cutpaper_bt:
                String res = PrinterTester.getInstance().cutPaper(mode);
                resultTv.setText(res);
                break;
            case R.id.fragment_printer_getcutmode_bt:
               String resultStr = PrinterTester.getInstance().getCutMode();
                resultTv.setText(resultStr);
                break;

            default:
                break;
        }
    }
}
