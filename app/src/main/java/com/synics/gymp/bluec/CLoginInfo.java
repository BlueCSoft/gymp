package com.synics.gymp.bluec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Administrator on 2019/06/22.
 */

public class CLoginInfo extends CJson {

    protected boolean precessData() {
        try {
            __gList = new ArrayList<>();
            __gList.add(__gJsonObj.getJSONArray("poscode"));
            __gList.add(__gJsonObj.getJSONArray("sales"));

            mErrorCode = 0;
        } catch (JSONException e) {
            mErrorCode = -1;
            mErrorMsg = e.getMessage();
        }
        return mErrorCode == 0;
    }

    /*保存到全局对象*/
    public boolean saveToGlobalData(String jsonText) {
        if (toJsonObject(jsonText)) {
            mErrorCode = -1;
            CGlobalData globalData = CGlobalData.get(null);
            String fieldName = "";
            try {
                Field[] fields = globalData.getClass().getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    fieldName = field.getName();

                    if (this.checkItemExists(fieldName)) {
                        field.setAccessible(true);
                        switch (CUtil.FieldType(field.getGenericType().toString())) {
                            case DataType.STRING:
                                field.set(globalData,this.getString(fieldName,""));
                                break;
                            case DataType.INT:
                                field.setInt(globalData,this.getInt(fieldName,0));
                                break;
                            case DataType.SHORT:
                                field.setInt(globalData,this.getInt(fieldName,0));
                                break;
                            case DataType.DOUBLE:
                                field.setDouble(globalData,this.getDouble(fieldName,0));
                                break;
                            case DataType.DATE:
                                break;
                            case DataType.BOOLEAN:
                                break;
                            default:
                                field.set(globalData, this.getString(fieldName, ""));
                                break;
                        }
                    }
                }
                CGlobalData.salesid = CGlobalData.cashierid;
                CGlobalData.sales = CGlobalData.cashier;
                globalData.clear();
                //设置操作码
                __gJsonArray = __gJsonObj.getJSONArray("poscode");
                for (int i = 0; i < getRowCount(); i++) {
                    String s = getString(i, "code");
                    globalData.setPoscode(new CObjInfo(getString(i, "code"),getString(i, "name"),getString(i, "shortcut"),"",
                            getDouble(i,"price"),getInt(i,"priceatt")));
                }

                //设置营业员
                __gJsonArray = __gJsonObj.getJSONArray("sales");
                for (int i = 0; i < getRowCount(); i++) {
                    globalData.setSales(new CObjInfo(getString(i, "sno"), getString(i, "sname")));
                }
                CGlobalData.salesid = "";
                CGlobalData.sales = "";

                if(__gJsonArray.length()<=1){
                    CGlobalData.salesid = CGlobalData.cashierid;
                    CGlobalData.sales = CGlobalData.cashier;
                }

                //设置订单状态
                __gJsonArray = __gJsonObj.getJSONArray("billstates");
                for (int i = 0; i < getRowCount(); i++) {
                    globalData.setBillstates(new CObjInfo(getString(i, "sno"), getString(i, "sname")));
                }

                //设置支付方式
                __gJsonArray = __gJsonObj.getJSONArray("payments");
                for (int i = 0; i < getRowCount(); i++) {
                    globalData.setPeyments(new CObjInfo(getString(i, "sno"), getString(i, "sname")));
                }

                //设置支付方式
                __gJsonArray = __gJsonObj.getJSONArray("viptypes");
                for (int i = 0; i < getRowCount(); i++) {
                    globalData.setViptypes(new CObjInfo(getString(i, "sno"), getString(i, "sname")));
                }
                //小票格式
                if (__gJsonObj.has("ticket")) {
                    JSONObject tobj = __gJsonObj.getJSONObject("ticket");
                    //小票头信息
                    CGlobalData.ticketHead.clear();
                    if (tobj.has("head")) {
                        JSONArray array = tobj.getJSONArray("head");
                        for (int i = 0; i < array.length(); i++)
                            CGlobalData.ticketHead.add(array.getJSONObject(i).getString("hnr"));
                    }

                    //小票体
                    CGlobalData.ticketBody.clear();
                    if (tobj.has("body")) {
                        JSONArray array = tobj.getJSONArray("body");
                        for (int i = 0; i < array.length(); i++)
                            CGlobalData.ticketBody.add(array.getJSONObject(i).getString("name"));
                    }

                    //小票脚信息
                    CGlobalData.ticketFoot.clear();
                    if (tobj.has("foot")) {
                        JSONArray array = tobj.getJSONArray("foot");
                        for (int i = 0; i < array.length(); i++)
                            CGlobalData.ticketFoot.add(array.getJSONObject(i).getString("hnr"));
                    }
                }
                CGlobalData.managerid = "";
                CGlobalData.managername = "";
                mErrorCode = 0;
            } catch (Exception e) {
                e.printStackTrace();
                mErrorCode = -1;
                mErrorMsg = fieldName + ":" + e.getMessage();
            }
        }
        return mErrorCode == 0;
    }
}
