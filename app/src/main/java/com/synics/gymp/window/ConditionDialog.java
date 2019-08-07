package com.synics.gymp.window;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.activity.QueryActivity;

import java.util.Calendar;

/**
 * Created by Administrator on 2019/6/30.
 */

public class ConditionDialog {
    protected AlertDialog dialog = null;
    protected ProgressDialog mDialog = null;

    protected int requestCode = 0;
    protected BaseActivity parentView = null;
    protected View.OnClickListener confirmEvent = null;       // 确定事件
    protected View.OnClickListener cancelEvent = null;        // 取消事件
    protected View.OnClickListener clickEvent = null;         // 普通事件
    protected View.OnClickListener dateSelectEvent = null;   // 日期选择
    private TextView viewForSelectDate = null;

    protected void toast(String msg) {
        Toast.makeText(parentView.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    protected void toastLong(String msg) {
        Toast.makeText(parentView.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    protected void showProgress(String msg) {
        if (mDialog == null) {
            mDialog = new android.app.ProgressDialog(parentView);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
        }
        mDialog.setMessage((msg.equals("")) ? "请稍后..." : msg);
        mDialog.show();
    }

    protected void hideProgress() {
        if (mDialog != null) mDialog.hide();
    }

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
                if (viewchild.getTag() != null && !viewchild.getTag().toString().equals("")) {
                    String stag = viewchild.getTag().toString();
                    if (stag.indexOf("click:") > -1) {
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

    protected void doReturn(View v){
        if (parentView.conditionCheck(requestCode, true, v, 0, "")) {
            dismiss();
            parentView.conditionSelect(requestCode, true, v, 0, "");
        }
    }

    protected void doClick(View v){
        dismiss();
        parentView.conditionSelect(requestCode, false, v, 0, "");
    }

    private void initEnvent(View contentView) {
        confirmEvent = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doReturn(v);
            }
        };
        cancelEvent = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        };
        clickEvent = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doClick(v);
            }
        };
        dateSelectEvent = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateDialog((TextView) v);
            }
        };
        setAllChildViewClick(contentView);
    }

    protected void onPaint(){

    }
    private void showDialog(View contentView, BaseActivity parent, View topPanel, int width, int gravity) {
        if (contentView.getParent() != null) {
            ((ViewGroup) contentView.getParent()).removeView(contentView);
        }
        parentView = parent;
        initEnvent(contentView);

        dialog = new AlertDialog.Builder(parent).create();
        dialog.show();
        dialog.setContentView(contentView);
        initData(contentView, parent);

        dialog.setCancelable(true);

        Window dialogWindow = dialog.getWindow();
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(gravity);
        dialogWindow.setBackgroundDrawableResource(R.drawable.dialog_radius);
        //lp.x = 0; // 新位置X坐标
        if (topPanel != null) {
            lp.y = topPanel.getHeight(); // 新位置Y坐标
        }
        lp.width = width;
        dialogWindow.setAttributes(lp);
        onPaint();
    }

    /* 子类完成其它数据的初始化 */
    protected void initData(View contentView, BaseActivity parent) {

    }

    public ConditionDialog(View contentView, BaseActivity parent, View topPanel, int width, int gravity) {
        showDialog(contentView, parent, topPanel, width, gravity);
    }

    public ConditionDialog(View contentView, BaseActivity parent, View topPanel) {
        showDialog(contentView, parent, topPanel, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER | Gravity.CENTER_VERTICAL);
    }

    public ConditionDialog(int requestCode, View contentView, BaseActivity parent, View topPanel) {
        this.requestCode = requestCode;
        showDialog(contentView, parent, topPanel, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER | Gravity.CENTER_VERTICAL);
    }

    public ConditionDialog(int requestCode, int layoutId, BaseActivity parent, View topPanel) {
        this.requestCode = requestCode;
        View contentView = LayoutInflater.from(parent).inflate(layoutId, null, false);
        showDialog(contentView, parent, topPanel, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER | Gravity.CENTER_VERTICAL);
    }

    protected void onClose(){

    }
    public void dismiss() {
        onClose();
        if (mDialog != null){
            mDialog.dismiss();
        }
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
