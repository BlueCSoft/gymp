package com.synics.gymp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.synics.gymp.bluec.CGlobalData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity {

    protected boolean disabledBack = false;   //是否禁止后退键
    protected ProgressDialog mDialog = null;
    protected View.OnClickListener closeEvent = null;   // 窗口关闭事件

    protected List<View> allDatasetView = null;        // 所有关联了字段的view

    public void closeActivity(View source) {
        close();
    }

    /**
     * 显示进度条
     *
     * @param msg
     */
    public void showProgress(String msg) {
        if (mDialog == null) {
            mDialog = new android.app.ProgressDialog(this);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
        }
        mDialog.setMessage((msg.equals("")) ? "请稍后..." : msg);
        mDialog.show();
    }

    /**
     * 隐藏进度条
     */
    public void hideProgress() {
        if (mDialog != null) mDialog.hide();
    }

    protected void msgBox(String title, String message, String[] btInfo,
                          DialogInterface.OnClickListener OnClick) {
        hideProgress();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        for (int i = 0; i < btInfo.length; i++) {
            switch (i) {
                case 0:
                    builder.setPositiveButton(btInfo[i], OnClick);
                    break;
                case 1:
                    builder.setNegativeButton(btInfo[i], OnClick);
                    break;
                case 2:
                    builder.setNeutralButton(btInfo[i], OnClick);
                    break;
            }
        }
        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void msgBox(String title, String message) {
        hideProgress();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((title.equals("")) ? "提示" : title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("确定", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void msgBox(String message) {
        msgBox("",message);
    }
    protected void askBox(String message,
                          DialogInterface.OnClickListener OnClick) {
        msgBox("提示", message, new String[]{"确定", "取消"}, OnClick);
    }

    protected void yesNoBox(String message,
                            DialogInterface.OnClickListener OnClick) {
        msgBox("提示", message, new String[]{"是", "否"}, OnClick);
    }

    protected void successBox(String message,
                              DialogInterface.OnClickListener OnClick) {
        msgBox("提示", message, new String[]{"成功", "不成功"}, OnClick);
    }

    protected void callActivity(Context packageContext, Class<?> cls, String[] keyValues, int reqCode) {
        Intent intent = new Intent(packageContext, cls);

        if (keyValues != null) {
            for (int i = 0; i < keyValues.length; i++) {
                String[] v = keyValues[i].split("\\=");
                intent.putExtra(v[0], v[1]);
            }
        }
        if (reqCode == 0) {
            startActivity(intent);
        } else {
            startActivityForResult(intent, reqCode);
        }
    }

    protected void callActivity(Context packageContext, Class<?> cls, String[] keyValues) {
        Intent intent = new Intent(packageContext, cls);

        if (keyValues != null) {
            for (int i = 0; i < keyValues.length; i++) {
                String[] v = keyValues[i].split("\\=");
                intent.putExtra(v[0], v[1]);
            }
        }
        startActivity(intent);
    }

    protected void callActivity(Context packageContext, Class<?> cls, int reqCode) {
        Intent intent = new Intent(packageContext, cls);
        intent.putExtra("code", "123");
        startActivityForResult(intent, reqCode);
    }

    protected void callActivity(Context packageContext, Class<?> cls) {
        Intent intent = new Intent(packageContext, cls);
        startActivity(intent);
    }

    private void getAllChildViews(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                if (viewchild.getTag() != null && !viewchild.getTag().toString().equals("")) {
                    allDatasetView.add(viewchild);
                }
                //再次 调用本身（递归）
                getAllChildViews(viewchild);
            }
        }
    }

    protected List<View> getAllChildViews() {
        allDatasetView = new ArrayList<View>();
        getAllChildViews(getWindow().getDecorView());
        return allDatasetView;
    }

    protected void initStart() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        closeEvent = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        };
        initStart();
        initViewBefore();
        initView();
        initViewAfter();
    }

    protected void onBeforeDestroy() {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        onBeforeDestroy();
        super.onDestroy();
    }

    //获取视图资源id
    protected abstract int getLayoutId();

    //初始化视图之前
    protected void initViewBefore() {
    }

    //初始化视图
    protected void initView() {
    }

    //初始化视图之后
    protected void initViewAfter() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    protected void toast(String msg) {
        hideProgress();
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    protected void toastLong(String msg) {
        hideProgress();
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    protected void close() {
        finish();
    }

    protected void close(int resultCode) {
        setResult(resultCode);
        finish();
    }

    protected void close(int resultCode, String[] paramNames, String[] paramValues) {
        Intent data = new Intent();
        for (int i = 0; i < paramNames.length; i++) {
            data.putExtra(paramNames[i], paramValues[i]);
        }
        CGlobalData.returnData = data;
        setResult(resultCode, data);
        finish();
    }

    /*查询条件选择后回调函数*/
    public void conditionSelect(int requestCode, Boolean isConfirm, View source, int position, String value) {
    }

    /*查询条件选择后参数检查回调函数*/
    public Boolean conditionCheck(int requestCode, Boolean isConfirm, View source, int position, String value) {
        return true;
    }

    protected void doActivityResult(int requestCode, Intent data) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            doActivityResult(requestCode, data);
        }
    }

    private String doGetValueByView(View view) {
        String result = "";
        String cName = view.getClass().getSimpleName();
        switch (cName) {
            case "AppCompatTextView":
                result = ((TextView) view).getText().toString();
                break;
            case "AppCompatEditText":
                result = ((EditText) view).getText().toString();
                break;
            case "AppCompatSpinner":
                result = ((Spinner) view).getSelectedItemPosition() + "";
                break;
        }
        return result;
    }

    protected String getValueByView(View view) {
        return doGetValueByView(view);
    }

    protected String getValueByViewId(View parentView, int viewId) {
        return doGetValueByView(parentView.findViewById(viewId));
    }

    private View findViewByViewTag(View view, String viewId) {
        return view.findViewWithTag(viewId);
    }

    protected View findViewByTag(String viewTag) {
        return getWindow().getDecorView().findViewWithTag(viewTag);
    }

    public View getViewByViewTag(View view, String viewId) {
        return findViewByViewTag(view, viewId);
    }

    protected String getValueByViewTag(View parentView, String viewTag) {
        View view = findViewByViewTag(parentView, viewTag);
        return doGetValueByView(view);
    }

    private void doSetValueByView(View view, String value) {
        String cName = view.getClass().getSimpleName();
        switch (cName) {
            case "AppCompatTextView":
                ((TextView) view).setText(value);
                break;
            case "AppCompatEditText":
                ((EditText) view).setText(value);
                break;
            case "AppCompatSpinner":
                ((Spinner) view).setSelection(Integer.parseInt(value));
                break;
        }
    }

    protected void setValueByView(View view, String value) {
        doSetValueByView(view, value);
    }

    protected void setValueByViewId(View parentView, int viewId, String value) {
        doSetValueByView(parentView.findViewById(viewId), value);
    }

    protected void setValueByViewTag(View parentView, String viewTag, String value) {
        View view = findViewByViewTag(parentView, viewTag);
        doSetValueByView(view, value);
    }

    protected void setValueByViewTag(String viewTag, String value) {
        View view = findViewByViewTag(this.getWindow().getDecorView(), viewTag);
        doSetValueByView(view, value);
    }

    protected void setValueByViewTag(String viewTag, double value) {
        setValueByViewTag(viewTag, value + "");
    }

    protected void setValueByViewTag(String[] viewTags, JSONObject obj) throws Exception {
        try {
            for (int i = 0; i < viewTags.length; i++) {
                setValueByViewTag(viewTags[i], obj.getString(viewTags[i]));
            }
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    protected void jsonArrayToListMap(List<Map<String, Object>> listitem, JSONArray data) {
        try {
            listitem.clear();
            for (int i = 0; i < data.length(); i++) {
                Map<String, Object> item = new HashMap<String, Object>();
                JSONObject obj = data.getJSONObject(i);
                Iterator<?> it = obj.keys();

                while (it.hasNext()) {//遍历JSONObject
                    String key = (String) it.next().toString();
                    item.put(key, obj.get(key));
                }
                listitem.add(item);
            }
        } catch (Exception e) {
        }
    }

    protected boolean isDisabledBack() {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (disabledBack || isDisabledBack()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }
}
