package com.synics.gymp.window;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.activity.QueryActivity;

import java.util.Calendar;

/**
 * Created by Administrator on 2019/6/29.
 */

public class ConditionWindow extends PopupWindow {
    private int requestCode = 0;
    private BaseActivity parentView = null;
    private View.OnClickListener confirmEvent = null;       // 确定事件
    private View.OnClickListener cancelEvent = null;        // 取消事件
    private View.OnClickListener clickEvent = null;         // 普通事件
    private View.OnClickListener dateSelectEvent = null;   // 日期选择
    private TextView viewForSelectDate = null;

    protected void openDateDialog(TextView source) {
        viewForSelectDate = source;
        Calendar cale1 = Calendar.getInstance();
        if (!source.getText().toString().equals("")) {
            String[] ds = source.getText().toString().split("-");
            cale1.set(Integer.parseInt(ds[0]), Integer.parseInt(ds[1]) - 1, Integer.parseInt(ds[2]));
        }
        new DatePickerDialog(parentView, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                viewForSelectDate.setText(String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth));
                //dateConditionChange(viewForSelectDate);
            }
        }, cale1.get(Calendar.YEAR), cale1.get(Calendar.MONTH), cale1.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void setAllChildViewClick(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                if(viewchild.getTag()!=null&&!viewchild.getTag().toString().equals("")){
                    String stag = viewchild.getTag().toString();
                    if(stag.indexOf("click:")>-1) {
                        switch (stag.substring(6)) {
                            case "date":
                                viewchild.setOnClickListener(dateSelectEvent);
                                break;
                            case "confirm":
                                viewchild.setOnClickListener(confirmEvent);
                                break;
                            case "cancel":
                                viewchild.setOnClickListener(cancelEvent);
                                break;
                            default:
                                viewchild.setOnClickListener(clickEvent);
                                break;
                        }
                    }
                }
                //再次 调用本身（递归）
                setAllChildViewClick(viewchild);
            }
        }
    }


    private void initEnvent(View contentView){
        confirmEvent = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(parentView.conditionCheck(requestCode,true,v,0,"")) {
                    dismiss();
                    parentView.conditionSelect(requestCode, true, v,0,"");
                }
            }
        };
        cancelEvent = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dismiss();
            }
        };
        clickEvent = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dismiss();
                parentView.conditionSelect(requestCode,false,v,0,"");
            }
        };
        dateSelectEvent =  new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openDateDialog((TextView) v);
            }
        };
        setAllChildViewClick(contentView);
    }

    private void showWindow(View contentView, BaseActivity parent, View topPanel, int width, int gravity) {
        parentView = parent;
        initEnvent(contentView);

        WindowManager.LayoutParams lp = parent.getWindow().getAttributes();
        lp.alpha = 0.5f;//调节透明度
        parent.getWindow().setAttributes(lp);
        //dismiss时恢复原样
        setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = parent.getWindow().getAttributes();
                lp.alpha = 1f;
                parent.getWindow().setAttributes(lp);
            }
        });
        //setBackgroundDrawable(new ColorDrawable(0x55555555));
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        if(topPanel==null) {
            showAtLocation(parent.getWindow().getDecorView(), gravity, 0, 0);
        }else{
            showAtLocation(topPanel, gravity, 0, topPanel.getHeight());
        }
    }

    public ConditionWindow(View contentView, BaseActivity parent, View topPanel) {
        super(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        showWindow(contentView,parent,topPanel,ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER | Gravity.CENTER_VERTICAL);
    }

    public ConditionWindow(View contentView, BaseActivity parent, View topPanel,int width,int gravity) {
        super(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        showWindow(contentView,parent,topPanel,width,gravity);
    }
}
