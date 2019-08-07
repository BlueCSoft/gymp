package com.synics.gymp.bluec;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import com.synics.gymp.iface.IDataLoadCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by Administrator on 2019/06/22.
 */

public abstract class CDataSet extends CObject {
    protected List<?> __mData;

    private List<String> mFieldNames;         // 字段名列表
    private List<CField> mFields;              // 字段列表

    private List<CDataSource> mDataSources;   // 绑定的数据yuan

    //private IDataLoadCallback mView = null;
    private String mDateSetName;
    private int mDataSetId;

    protected int mRecNo = 0;              //当前记录号
    protected boolean isEof = false;

    protected Context mContext;

    protected CDataSet __MDataSet = null;   //父数据集
    protected CDataSet __DDataSet = null;   //子数据集

    protected int __rowid = 0;

    protected boolean __dataIsChange = false;

    private boolean EnableControls = true;

    public CDataSet() {
        mFields = new ArrayList<>();
        mFieldNames = new ArrayList<>();
        mDataSources = new ArrayList<>();
    }

    protected void initField(Class<?> cls) throws Exception {
        try {
            if (mFields.isEmpty()) {
                Field[] fields = cls.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {

                    Field field = fields[i];
                    String fieldName = field.getName();
                    String type = field.getGenericType().toString();
                    if (type != null) {
                        int nDataType = CUtil.FieldType(type);
                        if (nDataType >= 0) {
                            CField cfield = new CField();
                            cfield.fieldName = fieldName;
                            cfield.dataType = nDataType;
                            mFieldNames.add(fieldName);
                            mFields.add(cfield);
                        } else {
                            throw new Exception(fieldName + " is no field");
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    //获取所有行
    public List<DataBean> getRows() {
        return (List<DataBean>) __mData;
    }

    //添加数据集绑定
    public CDataSet addDataSource(CDataSource dataDource) {
        if (mDataSources.indexOf(dataDource) < 0) {
            mDataSources.add(dataDource);
            dataDource.setDataset(this);
        }
        return this;
    }

    //解除数据源绑定
    public CDataSet removeDataSource(CDataSource dataDource) {
        if (mDataSources.indexOf(dataDource) >= 0) {
            mDataSources.remove(dataDource);
            dataDource.setDataset(null);
        }
        return this;
    }

    //允许数据变化时通知数据集源
    public void setEnableControls() {
        EnableControls = true;
        if (__dataIsChange) {
            notifyDataSource();
        }
    }

    //禁止数据变化时通知数据集源
    public void setDisableControls() {
        EnableControls = false;
    }

    public int getRowid() {
        return ++__rowid;
    }

    public int getRecNo() {
        return mRecNo;
    }

    public boolean dataIsChange() {
        return __dataIsChange;
    }

    public CDataSet getDDataSet() {
        return __DDataSet;
    }

    public void setDDataSet(CDataSet __pDataSet) {
        this.__DDataSet = __pDataSet;
    }

    public CDataSet getMDataSet() {
        return __MDataSet;
    }

    public void setMDataSet(CDataSet __pDataSet) {
        this.__MDataSet = __pDataSet;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    //获取字段数量
    public int getFieldCount() {
        return mFields.size();
    }

    //获取当前记录
    public DataBean getRow() {
        return (DataBean) __mData.get(mRecNo - 1);
    }

    //获取制定行记录
    public DataBean getRow(int recno) {
        return (DataBean) __mData.get(recno - 1);
    }

    public int moveRecord(int toRow) {
        mRecNo = toRow;
        return mRecNo;
    }

    public int first() {
        mRecNo = 1;
        return mRecNo;
    }

    public int next() {
        if (mRecNo < getRecordCount()) {
            mRecNo++;
            isEof = false;
        } else isEof = true;
        return mRecNo;
    }

    public boolean isEnd() {
        return mRecNo >= getRecordCount() && isEof;
    }

    /*通知数据源数据发生变化*/
    private void notifyDataSource() {
        if (EnableControls) {
            if (!isEmpty()) {
                getRow().OnFieldChange(null);
            }
            for (int i = 0; i < mDataSources.size(); i++) {
                mDataSources.get(i).doRecordChange();
            }
        }
    }

    private void notifyDataSource(String fieldName) {
        if (EnableControls) {
            if (getRow().OnFieldChange(fieldName)) {
                notifyDataSource();
            } else {
                for (int i = 0; i < mDataSources.size(); i++) {
                    mDataSources.get(i).doDataChange(fieldName);
                }
            }
        }
    }

    protected void notifyCalFieldChange(String fieldName) {
        for (int i = 0; i < mDataSources.size(); i++) {
            mDataSources.get(i).doCalDataChange(fieldName);
        }
    }

    public void delete() {
        if (!isEmpty()) {
            __mData.remove(mRecNo - 1);
            if (mRecNo > getRecordCount()) mRecNo = getRecordCount();
            OnDataChange();
            notifyDataSource();
            notifyMDataSet(this, false);
        }
    }

    public void delete(int recno) {
        if (!isEmpty()) {
            __mData.remove(recno - 1);
            if (mRecNo > getRecordCount()) mRecNo = getRecordCount();
            OnDataChange();
            notifyDataSource();
            notifyMDataSet(this, false);
        }
    }

    public boolean locate(String[] fieldNames, String[] values) {
        boolean b = false;
        try {
            if (!isEmpty()) {
                int _tRecNo = mRecNo;
                for (int i = 0; !b && i < __mData.size(); i++) {
                    moveRecord(i + 1);
                    boolean c = true;
                    for (int j = 0; c && j < fieldNames.length; j++) {
                        String f = getString(fieldNames[j]);
                        String v = values[j];
                        c = f.equals(v);
                    }
                    b = c;
                }
                if (!b) {
                    mRecNo = _tRecNo;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    //查找记录
    public boolean locate(String fieldName, String value) {
        boolean b = false;
        try {
            if (!isEmpty()) {
                int _tRecNo = mRecNo;
                for (int i = 0; !b && i < __mData.size(); i++) {
                    moveRecord(i + 1);
                    boolean c = true;
                    String f = getString(fieldName);
                    b = f.equals(value);
                }
                if (!b) {
                    mRecNo = _tRecNo;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }


    //清空当前行数据
    public void clearRowData() throws Exception {
        try {
            Object rowObj = getRow(mRecNo);
            Field[] fields = rowObj.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                switch (CUtil.FieldType(field.getType().getSimpleName())) {
                    case DataType.INT:
                        field.setInt(rowObj, 0);
                        break;
                    case DataType.LONG:
                        field.setLong(rowObj, 0);
                        break;
                    case DataType.SHORT:
                        field.setShort(rowObj, (short) 0);
                        break;
                    case DataType.DOUBLE:
                        field.setDouble(rowObj, 0);
                        break;
                    default:
                        field.set(rowObj, "");
                        break;
                }
            }
            OnDataChange();
            notifyDataSource();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    //装载一行数据
    protected void dataToRow(JSONObject row, Object rowObj) throws Exception {
        try {
            Field[] fields = rowObj.getClass().getDeclaredFields();
            for (int i = 0, j = 0; i < fields.length; i++) {
                Field field = fields[i];
                String fieldName = field.getName();
                if (row.has(fieldName)) {
                    field.setAccessible(true);
                    switch (CUtil.FieldType(field.getType().getSimpleName())) {
                        case DataType.INT:
                            field.setInt(rowObj, row.getInt(fieldName));
                            break;
                        case DataType.LONG:
                            field.setLong(rowObj, row.getLong(fieldName));
                            break;
                        case DataType.SHORT:
                            field.setShort(rowObj, (short) (row.getInt(fieldName)));
                            break;
                        case DataType.DOUBLE:
                            field.setDouble(rowObj, row.getDouble(fieldName));
                            break;
                        default:
                            field.set(rowObj, row.get(fieldName));
                            break;
                    }
                    j++;
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    protected void doLoadRowDataFrom(JSONObject jsonObject) throws Exception {
        dataToRow(jsonObject, getRow());
    }

    public void loadRowDataFrom(JSONObject jsonObject) throws Exception {
        doLoadRowDataFrom(jsonObject);
        OnDataChange();
        notifyDataSource();
    }

    protected void getFields(Class<?> cls) {
        try {
            if (mFields.isEmpty()) {
                initField(cls);
            }
        } catch (Exception ex) {
            Log.e(CDataSet.class.getName(), ex.getMessage());
        }
    }


    public CField findField(String fieldName) {
        CField field = null;
        int k = mFieldNames.indexOf(fieldName);
        if (k > -1) {
            field = mFields.get(k);
        }
        return field;
    }

    protected abstract DataBean createRowObject(boolean isAppend);

    protected abstract void loadRowData(JSONObject row) throws Exception;

    protected void OnLoadData() throws Exception {
        __dataIsChange = false;
    }

    //从JsonArray装载数据,isAppend表示不清除以前的数据
    public boolean loadData(JSONArray rows, boolean isAppend) {
        mErrorCode = -1;
        try {
            //if(!isAppend) data.clear();  //先清除以前的数据
            __mData.clear();
            mRecNo = 0;
            for (int i = 0; i < rows.length(); i++) {
                loadRowData(rows.getJSONObject(i));
            }
            mErrorCode = 0;
            mRecNo = (getRecordCount() == 0) ? 0 : 1;
            OnLoadData();
            OnDataChange();
            notifyMDataSet(this, false);
            notifyDataSource();
        } catch (Exception e) {
            mErrorCode = -1;
            mErrorMsg = e.getMessage();
        }
        return mErrorCode == 0;
    }

    public void OnDataChange() {
    }

    public void trigerEvent(int event) {
        notifyDataSource();
    }

    public void trigerEventByValue(int event, int n, String value) {
        notifyDataSource();
    }

    //子数据调用，通知父数据集数据变化
    public void notifyMDataSet(CDataSet dataSet, boolean isLoad) {
        if (__MDataSet != null) __MDataSet.notifyMDataSet(this, isLoad);
    }

    public void notifyMDataSet() {
        if (__MDataSet != null) __MDataSet.notifyMDataSet(this, false);
    }

    protected boolean BeforeInsert() {
        return true;
    }

    protected void AfterInsert() throws Exception {
    }

    protected abstract void doInsert();

    private void innerInsert() throws Exception {
        if (__mData.isEmpty()) {
            if (BeforeInsert()) doInsert();
            AfterInsert();
        }
    }

    public void insert() {
        try {
            if (BeforeInsert()) doInsert();
            AfterInsert();
            __dataIsChange = true;
            OnDataChange();
            notifyDataSource();
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), ex.getMessage());
        }
    }

    //清空数据集
    public void emptyDataSet() {
        __mData.clear();
        mRecNo = 0;
        __dataIsChange = false;
        OnDataChange();
    }

    public int getRecordCount() {
        return __mData.size();
    }

    public boolean isEmpty() {
        return getRecordCount() == 0;
    }

    public boolean isNull(String fieldName) {
        boolean result = true;
        try {
            if (!isEmpty()) {
                Object obj = ((DataBean) __mData.get(mRecNo - 1)).get(fieldName);
                result = obj != null;
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
        }
        return result;
    }

    public String getString(String fieldName) {
        String result = "";
        try {
            if (!isEmpty()) {
                result = ((DataBean) __mData.get(mRecNo - 1)).getString(fieldName);
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
        }
        return result;
    }

    public String getString(String fieldName, int nLClen, String afxChar) {
        String result = getString(fieldName);
        int clen = result.length();
        if (clen > nLClen) {
            result = result.substring(nLClen);
            result = CUtil.StringOfChar(afxChar, nLClen) + result;
        }
        return result;
    }

    public String $(String fieldName) {
        return getString(fieldName);
    }

    public String $(String fieldName, int nLClen, String afxChar) {
        return getString(fieldName, nLClen, afxChar);
    }

    public String getRightString(String fieldName, int nClen) {
        String result = getString(fieldName);
        int clen = result.length();
        if (clen > nClen) {
            result = result.substring(clen - nClen);
        }
        return result;
    }

    public int getInt(String fieldName) {
        int result = 0;
        try {
            if (!isEmpty()) {
                result = ((DataBean) __mData.get(mRecNo - 1)).getInt(fieldName);
            }
        } catch (Exception e) {
            Log.e("getInt", e.getMessage());
        }
        return result;
    }

    public int $i(String fieldName) throws Exception {
        return getInt(fieldName);
    }

    public long getLong(String fieldName) throws Exception {
        long result = 0;
        try {
            if (!isEmpty()) {
                result = ((DataBean) __mData.get(mRecNo - 1)).getLong(fieldName);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return result;
    }

    public double getDouble(String fieldName) throws Exception {
        double result = 0;
        try {
            if (!isEmpty()) {
                result = ((DataBean) __mData.get(mRecNo - 1)).getDouble(fieldName);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return CUtil.roundEx(result, 4);
    }

    public double getDoubleSign(String fieldName) throws Exception {
        double result = getDouble(fieldName);
        if (result > 0) result = -result;
        return result;
    }

    public String getDouble(String fieldName, String fmt, int width, boolean __zeroToSpace)
            throws Exception {
        double v = getDouble(fieldName);
        String result = CUtil.formatNumber(v, fmt, __zeroToSpace);
        if (!result.equals("") && width > 0) {
            int rlen = result.length();
            if (rlen < width) result = CUtil.StringOfChar(" ", width - rlen) + result;
        }
        return result;
    }

    public String getDouble(String fieldName, String fmt) throws Exception {
        return getDouble(fieldName, fmt, 0, false);
    }

    public String getDouble(String fieldName, String fmt, boolean __zeroToSpace) throws Exception {
        return getDouble(fieldName, fmt, 0, __zeroToSpace);
    }

    public String getDouble(String fieldName, String fmt, int width) throws Exception {
        return getDouble(fieldName, fmt, width, false);
    }

    public void setNull(String fieldName) throws Exception {
        try {
            innerInsert();
            if (!isEmpty()) {
                ((DataBean) __mData.get(mRecNo - 1)).setNull(fieldName);
                notifyDataSource(fieldName);
                __dataIsChange = true;
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void setString(String fieldName, String value) throws Exception {
        try {
            innerInsert();
            if (!isEmpty()) {
                ((DataBean) __mData.get(mRecNo - 1)).setString(fieldName, value);
                notifyDataSource(fieldName);
                __dataIsChange = true;
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void setStringAllRow(String fieldName, String value) throws Exception {
        try {
            innerInsert();
            if (!isEmpty()) {
                int omRecNo = mRecNo;
                for (int i = 0; i < __mData.size(); i++) {
                    ((DataBean) __mData.get(i)).setString(fieldName, value);
                }
                mRecNo = omRecNo;
                __dataIsChange = true;
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void setInt(String fieldName, int value) throws Exception {
        try {
            innerInsert();
            if (!isEmpty()) {
                ((DataBean) __mData.get(mRecNo - 1)).setInt(fieldName, value);
                notifyDataSource(fieldName);
                __dataIsChange = true;
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void setIntAllRow(String fieldName, int value) throws Exception {
        try {
            innerInsert();
            if (!isEmpty()) {
                int omRecNo = mRecNo;
                for (int i = 0; i < __mData.size(); i++) {
                    ((DataBean) __mData.get(i)).setInt(fieldName, value);
                }
                __dataIsChange = true;
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void setLong(String fieldName, long value) throws Exception {
        try {
            innerInsert();
            if (!isEmpty()) {
                ((DataBean) __mData.get(mRecNo - 1)).setLong(fieldName, value);
                notifyDataSource(fieldName);
                __dataIsChange = true;
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void setDouble(String fieldName, double value) throws Exception {
        double result = 0;
        try {
            innerInsert();
            if (!isEmpty()) {
                ((DataBean) __mData.get(mRecNo - 1)).setDouble(fieldName, value);
                notifyDataSource(fieldName);
                __dataIsChange = true;
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public int getSumInt(String fieldName) throws Exception {
        int result = 0;
        try {
            if (!isEmpty()) {
                for (int i = 0; i < __mData.size(); i++) {
                    result += ((DataBean) __mData.get(i)).getInt(fieldName);
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return result;
    }

    public double getSumDouble(String fieldName) throws Exception {
        double result = 0;
        try {
            if (!isEmpty()) {
                for (int i = 0; i < __mData.size(); i++) {
                    result = CUtil.add(result, ((DataBean) __mData.get(i)).getDouble(fieldName));
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return result;
    }

    public String getSumDouble(String fieldName, String fmt) throws Exception {
        double v = getSumDouble(fieldName);
        return CUtil.formatNumber(v, fmt, false);
    }

    public int getMaxInt(String fieldName) throws Exception {
        int result = 0;
        try {
            if (!isEmpty()) {
                for (int i = 0; i < __mData.size(); i++) {
                    int n = ((DataBean) __mData.get(i)).getInt(fieldName);
                    result = (result < n) ? n : result;
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return result + 1;
    }

    public double getMaxDouble(String fieldName) throws Exception {
        double result = 0;
        try {
            if (!isEmpty()) {
                for (int i = 0; i < __mData.size(); i++) {
                    double n = ((DataBean) __mData.get(i)).getDouble(fieldName);
                    result = (result < n) ? n : result;
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return result;
    }

    public void setPropValue(String fieldName, String value) throws Exception {
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                switch (CUtil.FieldType(field.getType().getSimpleName())) {
                    case DataType.INT:
                        field.setInt(this, Integer.parseInt(value));
                        break;
                    case DataType.LONG:
                        field.setLong(this, Long.parseLong(value));
                        break;
                    case DataType.DOUBLE:
                        field.setDouble(this, Double.parseDouble(value));
                        break;
                    default:
                        field.set(this, value);
                        break;
                }
                notifyCalFieldChange(fieldName);
            } else {
                throw new Exception("字段" + fieldName + "不存在");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void setPropValue(String fieldName, int value) throws Exception {
        setPropValue(fieldName, value + "");
    }

    public void setPropValue(String fieldName, long value) throws Exception {
        setPropValue(fieldName, value + "");
    }

    public void setPropValue(String fieldName, double value) throws Exception {
        setPropValue(fieldName, value + "");
    }

    /*记录生成json格式数据.bBrace=true,含两端的大括号*/
    private String rowToJsonText(Object rowObj, boolean bBrace) throws Exception {
        return ((DataBean) rowObj).rowToJsonText(mFields, bBrace);
    }

    public String getRowJson(boolean bBrace) throws Exception {
        String result = "";
        try {
            if (!isEmpty()) {
                result = rowToJsonText(getRow(mRecNo), bBrace);
            } else if (bBrace)
                result = "{" + result + "}";
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return result;
    }

    /*获取数据集数据的json格式*/
    public String getJson() throws Exception {
        String result = "";
        try {
            if (!isEmpty()) {
                String[] buf = new String[__mData.size()];
                for (int i = 1; i <= __mData.size(); i++) {
                    buf[i - 1] = rowToJsonText(getRow(i), true);
                }
                result = CUtil.joinArray(buf, ",");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return "[" + result + "]";
    }

    public int getNextRowid() {
        return ++__rowid;
    }

    public void copyFrom(DataBean sdb, String[] noFields) throws Exception {
        try {
            if (isEmpty()) {
                doInsert();
            }
            ((DataBean) __mData.get(mRecNo - 1)).copyFrom(sdb, noFields);
            notifyMDataSet(this, false);
            notifyDataSource();
            OnDataChange();
            __dataIsChange = true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void cloneData(CDataSet dSet, String[] noFields) throws Exception {
        try {
            int rcount = dSet.getRecordCount();
            for (int i = 0; i < rcount; i++) {
                doInsert();
                ((DataBean) __mData.get(mRecNo - 1)).copyFrom(dSet.getRow(i + 1), noFields);
            }
            __dataIsChange = true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void shareData(double value0, double value1, double value2) {
    }
}
