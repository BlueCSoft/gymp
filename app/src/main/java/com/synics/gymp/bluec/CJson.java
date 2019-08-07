package com.synics.gymp.bluec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 * 连接中间层类
 */

public class CJson extends CObject {

    protected JSONObject __gJsonObj = null;
    protected JSONArray __gJsonArray = null;
    protected List<JSONArray> __gList = null;

    public CJson() {
    }

    public JSONObject getJsonObj() {
        return __gJsonObj;
    }
    public JSONArray getJsonArray() {
        return __gJsonArray;
    }

    public static String stringArrayToJson(String[] vArray) {
        String result = "";
        try {
            JSONObject o = new JSONObject();
            for (int i = 0; i < vArray.length; i += 2)
                o.put(vArray[i], vArray[i + 1]);
            result = o.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    //检查数据项是否存在

    public boolean checkItemExists(String itemName) {
        return (__gJsonObj != null) && __gJsonObj.has(itemName);
    }

    //获取数组行数
    public int getRowCount() {
        return (__gJsonArray == null) ? 0 : __gJsonArray.length();
    }

    //获取字符串
    public String getString(String key, String defaultValue) {
        String Result = "";
        try {
            Result = (__gJsonObj.isNull(key)) ? defaultValue : __gJsonObj.getString(key);
        } catch (JSONException e) {
            Result = defaultValue;
        }
        return Result;
    }

    public String getString(String key) {
        return getString(key, "0");
    }

    public String getString(int index, String key, String defaultValue) {
        String Result = "";
        try {
            Result = (__gJsonArray.getJSONObject(index).isNull(key)) ? defaultValue
                : __gJsonArray.getJSONObject(index).getString(key);
        } catch (JSONException e) {
            Result = defaultValue;
        }
        return Result;
    }

    public String getString(int index, String key) {
        return getString(index,key,"");
    }

    //获取字符串
    public String getStringEx(String key, String defaultValue) {
        String Result = "";
        try {
            JSONObject data = __gJsonObj.getJSONObject("data");
            Result = (data.has(key)&&!data.isNull(key)) ? data.getString(key):defaultValue;
        } catch (JSONException e) {
            Result = defaultValue;
        }
        return Result;
    }

    public String getStringEx(String key) {
        return getStringEx(key, "");
    }

    //获取整数
    public int getInt(String key, int defaultValue) {
        int Result = 0;
        try {
            Result = __gJsonObj.getInt(key);
        } catch (JSONException e) {
            Result = defaultValue;
        }
        return Result;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(int index, String key, int defaultValue) {
        int Result = 0;
        try {
            Result = __gJsonArray.getJSONObject(index).getInt(key);
        } catch (JSONException e) {
            Result = defaultValue;
        }
        return Result;
    }

    public int getInt(int index, String key) {
        return getInt(index,key,0);
    }

    //获取整数
    public int getIntEx(String key, int defaultValue) {
        int Result = 0;
        try {
            JSONObject data = __gJsonObj.getJSONObject("data");
            Result = data.getInt(key);
        } catch (JSONException e) {
            Result = defaultValue;
        }
        return Result;
    }

    public int getIntEx(String key) {
        return getIntEx(key, 0);
    }

    //获取浮点数
    public double getDouble(String key, double defaultValue) {
        double Result = 0;
        try {
            Result = __gJsonObj.getDouble(key);
        } catch (JSONException e) {
            Result = defaultValue;
        }
        return Result;
    }

    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    public double getDouble(int index, String key, double defaultValue) {
        double Result = 0;
        try {
            Result = __gJsonArray.getJSONObject(index).getDouble(key);
        } catch (JSONException e) {
            Result = defaultValue;
        }
        return Result;
    }

    public double getDouble(int index, String key) {
        return getDouble(index,key,0);
    }

    public double getDoubleEx(String key, double defaultValue) {
        double Result = 0;
        try {
            Result = __gJsonObj.getJSONObject("data").getDouble(key);
        } catch (JSONException e) {
            Result = defaultValue;
        }
        return Result;
    }

    public double getDoubleEx(String key) {
        return getDoubleEx(key, 0);
    }

    public boolean moveRecord(int rowIndex) {
        if (__gList == null || rowIndex >= __gList.size()) {
            return false;
        } else {
            __gJsonArray = __gList.get(rowIndex);
        }
        return true;
    }

    /*处理数据,子类继承*/
    protected boolean precessData() {
        return true;
    }

    /**
     * 检查返回的标记
     *
     * @return,成功时返回true
     */
    private boolean checkResult() {
        boolean result = false;
        try {
            if (__gJsonObj == null) {
                return false;
            } else if (!__gJsonObj.getString("code").equals("0")) {
                mErrorMsg = __gJsonObj.getString("msg");
                return false;
            } else if (!__gJsonObj.getString("sub_code").equals("0")) {
                mErrorMsg = __gJsonObj.getString("sub_msg");
                return false;
            }
            mErrorCode = 0;
            result = precessData();
        } catch (Exception ex) {
            mErrorMsg = ex.getMessage();
            result = false;
        }
        return result;
    }

    /*构造json对象*/
    public boolean toJsonObject(String jsonText) {
        mErrorCode = -1;
        __gJsonObj = null;
        __gJsonArray = null;
        __gList = null;
        try {
            __gJsonObj = new JSONObject(jsonText);
            if(__gJsonObj.has("data")){
                if(__gJsonObj.get("data") instanceof JSONArray){
                    __gJsonArray = __gJsonObj.getJSONArray("data");
                }
            }
            return checkResult();
        } catch (Exception ex) {
            mErrorMsg = ex.getMessage();
        }
        return mErrorCode == 0;
    }
}
