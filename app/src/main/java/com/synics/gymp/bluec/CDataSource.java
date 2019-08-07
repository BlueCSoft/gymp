package com.synics.gymp.bluec;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnTextChanged;

/**
 * Created by Administrator on 2019/7/4.
 */

public class CDataSource extends CObject {
    private List<View> fieldViews = null;        // 字段视图列表
    private List<String> fieldNames = null;      // 字段视图列表
    private List<String> fieldFmts = null;       // 数据格式

    private CDataSet dataset = null;             // 绑定的数据集
    private OnSetFieldText onSetFieldText = null;
    private OnFieldChange onFieldChange = null; // 数据字段产生变化
    private OnFieldChange onCalFieldChange = null; //计算字段产生变化
    private OnDataChange onDataChange = null;

    private Boolean isInnerSetValue = false;
    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = null;
    private View.OnFocusChangeListener onFocusChangeListener = null;
    private Activity owner = null;
    private TextView focusView = null;

    private TextView.OnEditorActionListener onEditorActionListener = null;

    public CDataSource() {
        fieldViews = new ArrayList<>();
        fieldNames = new ArrayList<>();
        fieldFmts = new ArrayList<>();
    }

    public CDataSource(Activity owner) {
        this.owner = owner;
        fieldViews = new ArrayList<>();
        fieldNames = new ArrayList<>();
        fieldFmts = new ArrayList<>();
    }

    public CDataSource(AppCompatActivity holder, int fieldsId[]) {
        fieldViews = new ArrayList<>();
        fieldNames = new ArrayList<>();
        fieldFmts = new ArrayList<>();
        bindFields(holder, fieldsId);
    }

    private String getFieldName(View view) {
        String s = view.getTag().toString();
        int k = s.indexOf(":");
        return (k > -1) ? s.substring(0, k) : s;
    }

    private void valueToField(TextView view) {
        String fieldName = getFieldName(view);
        String v = view.getText().toString();
        String oldv = dataset.getString(fieldName);
        if (!v.equals(oldv)) {
            try {
                if (v.equals("")) {
                    dataset.setNull(fieldName);
                } else {
                    dataset.setString(fieldName, v);
                }
            } catch (Exception ex) {
            }
        }
    }

    private void dataToField(String v) {
        if (focusView != null) {
            String fieldName = getFieldName(focusView);
            String oldv = dataset.getString(fieldName);
            if (!v.equals(oldv)) {
                try {
                    if (v.equals("")) {
                        dataset.setNull(fieldName);
                    } else {
                        dataset.setString(fieldName, v);
                    }
                } catch (Exception ex) {
                }
            }
        }
    }

    private String saveFocusView() {
        String result = "";
        if (owner != null) {
            View v = owner.getCurrentFocus();
            if (v instanceof EditText) {
                focusView = (TextView) v;
                result = focusView.getText().toString();
            } else {
                focusView = null;
            }
        }
        return result;
    }

    private void createEvent() {
        if (onCheckedChangeListener == null) {
            onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (!isInnerSetValue) {
                        String v = saveFocusView();
                        String fieldName = getFieldName(group);
                        try {
                            dataset.setInt(fieldName, CUtil.getSelectedIndex(group));
                        } catch (Exception ex) {
                        }
                        dataToField(v);
                    }
                }
            };
            onFocusChangeListener = new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        valueToField((TextView) view);
                    }
                }
            };

            onEditorActionListener = new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == 6) {
                        valueToField(v);
                    }
                    return false;
                }
            };
        }
    }

    private void bindEvent(View view) {
        String cName = view.getClass().getSimpleName();
        switch (cName) {
            case "AppCompatEditText":
                ((EditText) view).setOnFocusChangeListener(onFocusChangeListener);
                ((EditText) view).setOnEditorActionListener(onEditorActionListener);
                break;
            case "CheckBox":
                ((CheckBox) view).setChecked(false);
                break;
            case "RadioGroup":
                ((RadioGroup) view).setOnCheckedChangeListener(onCheckedChangeListener);
                break;
        }
    }

    /* 绑定视图 */
    private String doBindField(View field) {
        String fieldName = "";
        if (field != null && !fieldViews.contains(field) && !field.getTag().toString().equals("")) {
            fieldViews.add(field);
            String s = field.getTag().toString();
            int k = s.indexOf(":");
            if (k > -1) {
                fieldName = s.substring(0, k);
                fieldNames.add(fieldName);
                fieldFmts.add(s.substring(k + 1));
            } else {
                fieldName = s;
                fieldNames.add(s);
                fieldFmts.add("");
            }
        }
        return fieldName;
    }

    private CDataSource doBindField(AppCompatActivity holder, int fieldId) {
        doBindField(holder.findViewById(fieldId));
        return this;
    }

    public CDataSource bindField(View field) {
        String fieldName = doBindField(field);
        if (dataset != null)
            doDataChange(fieldName);
        return this;
    }

    public CDataSource bindField(AppCompatActivity holder, int fieldId) {

        String fieldName = doBindField(holder.findViewById(fieldId));
        if (dataset != null)
            doDataChange(fieldName);
        return this;
    }

    public CDataSource bindFields(AppCompatActivity holder, int fieldsId[]) {
        for (int i = 0; i < fieldsId.length; i++) {
            doBindField(holder, fieldsId[i]);
        }
        doRecordChange();
        return this;
    }

    public CDataSource bindViews(List<View> views) {
        if (dataset != null) {
            createEvent();
            for (int i = 0; i < views.size(); i++) {
                View field = views.get(i);
                String s = field.getTag().toString();
                String fieldName = "", fieldFmt = "";
                int k = s.indexOf(":");
                if (k > -1) {
                    fieldName = s.substring(0, k);
                    fieldFmt = s.substring(k + 1);
                } else {
                    fieldName = s;
                }

                if (dataset.findField(fieldName) != null) {
                    fieldViews.add(field);
                    fieldNames.add(fieldName);
                    fieldFmts.add(fieldFmt);
                    bindEvent(field);
                }
            }
            doRecordChange();
        }
        return this;
    }

    /* 绑定数据集 */
    public CDataSource setDataset(CDataSet dataset) {
        this.dataset = dataset;
        if (dataset == null) {
            doRecordChange();
        } else {
            doDataChange(null);
        }
        return this;
    }

    private boolean isNullOrEmpty(String value) {
        return (value == null || value.equals(""));
    }

    private void clearViewValue(View view) {
        String cName = view.getClass().getSimpleName();
        switch (cName) {
            case "AppCompatTextView":
                ((TextView) view).setText("");
                break;
            case "AppCompatEditText":
                ((EditText) view).setText("");
                break;
            case "CheckBox":
                ((CheckBox) view).setChecked(false);
                break;
            case "RadioButton":
                ((RadioButton) view).setChecked(false);
                break;
            case "RadioGroup":
                ((RadioGroup) view).clearCheck();
                break;
        }
    }

    private void setViewValue(View view, String value) {
        String cName = view.getClass().getSimpleName();
        switch (cName) {
            case "AppCompatTextView":
                ((TextView) view).setText(value);
                break;
            case "AppCompatEditText":
                ((EditText) view).setText(value);
                if(owner!=null&&owner.getCurrentFocus()==view){
                    ((EditText) view).setSelection(((EditText) view).length());
                }
                break;
            case "CheckBox":
                ((CheckBox) view).setChecked(value.compareTo("0") > 0);
                break;
            case "RadioButton":
                isInnerSetValue = true;
                ((RadioButton) view).setChecked(value.compareTo("0") > 0);
                isInnerSetValue = false;
                break;
            case "RadioGroup":
                isInnerSetValue = true;
                if (isNullOrEmpty(value)) {
                    ((RadioGroup) view).clearCheck();
                } else {
                    int n = Integer.parseInt(value);
                    if (n < ((RadioGroup) view).getChildCount()) {
                        ((RadioButton) ((RadioGroup) view).getChildAt(n)).setChecked(true);
                    }
                }
                isInnerSetValue = false;
                break;
        }
    }

    /* 数据集数据产生变化，fieldName=null时刷新全部字段 */
    public void doDataChange(String fieldName) {
        try {
            if (fieldName != null) {
                CField field = dataset.findField(fieldName);
                int k = fieldNames.indexOf(fieldName);
                if (field != null && k > -1) {
                    View view = fieldViews.get(k);
                    String value = ((field.dataType == DataType.DOUBLE) && !(fieldFmts.get(k).equals(""))) ?
                            dataset.getDouble(fieldName, fieldFmts.get(k)) : dataset.getString(fieldName);
                    if (onSetFieldText != null) {
                        value = onSetFieldText.onSetText(view.getId(), fieldName, value);
                    }
                    setViewValue(view, value);
                    if (onFieldChange != null) {
                        onFieldChange.onChange(this, fieldName);
                    }
                }
            } else {
                for (int i = 0; i < fieldNames.size(); i++) {
                    String fname = fieldNames.get(i);
                    CField field = dataset.findField(fname);
                    if (field != null) {
                        View view = fieldViews.get(i);
                        String value = ((field.dataType == DataType.DOUBLE) && !(fieldFmts.get(i).equals(""))) ?
                                dataset.getDouble(fname, fieldFmts.get(i)) : dataset.getString(fname);
                        if (onSetFieldText != null) {
                            value = onSetFieldText.onSetText(view.getId(), fname, value);
                        }
                        setViewValue(view, value);
                        if (onFieldChange != null) {
                            onFieldChange.onChange(this, fieldName);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(CDataSource.class.getName(), ex.getMessage());
        }
    }

    public void doCalDataChange(String fieldName) {
        if (onCalFieldChange != null) {
            onCalFieldChange.onChange(this, fieldName);
        }
        if (onDataChange != null) {
            onDataChange.onChange(this);
        }
    }

    public void doDataChanges(String[] fieldNames) {
        for (int i = 0; i < fieldNames.length; i++) {
            doDataChange(fieldNames[i]);
        }
    }

    public void doRecordChange() {
        if (dataset == null || dataset.getRecordCount() == 0) {
            isInnerSetValue = true;
            for (int i = 0; i < fieldViews.size(); i++) {
                clearViewValue(fieldViews.get(i));
            }
            isInnerSetValue = false;
        } else {
            doDataChange(null);
        }
        if (onDataChange != null) {
            onDataChange.onChange(this);
        }
    }

    public interface OnSetFieldText {
        String onSetText(int viewId, String fieldName, String defaultValue);
    }

    public void setOnSetFieldText(OnSetFieldText onSetFieldText) {
        this.onSetFieldText = onSetFieldText;
    }

    public interface OnFieldChange {
        void onChange(CDataSource sender, String fieldName);
    }

    public void setOnFieldChange(OnFieldChange onFieldChange) {
        this.onFieldChange = onFieldChange;
    }

    public void setOnCalFieldChange(OnFieldChange onCalFieldChange) {
        this.onCalFieldChange = onCalFieldChange;
    }

    public interface OnDataChange {
        void onChange(CDataSource sender);
    }

    public void setOnDataChange(OnDataChange onDataChange) {
        this.onDataChange = onDataChange;
    }

    public void refresh() {
        this.doRecordChange();
    }

    public void updateData(View source) {
        if (source instanceof EditText) {
            valueToField((TextView) source);
        }
    }

    public void updateData() {
        if (owner != null) {
            updateData(owner.getCurrentFocus());
        }
    }
}
