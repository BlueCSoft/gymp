package com.pax.pay.cup.test;

import org.json.JSONException;
import org.json.JSONObject;

public class Util {

    public static String parseResp(String json) {
        try {
            JSONObject result = new JSONObject(json);
            String total = "";
            String temp;

            //聚合新增以下
            try {
                temp = result.getString("jhMerchId");
                total += "聚合商户编号：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("orderAmount");
                total += "订单总金额 ：" + temp + "\n";
            } catch (JSONException e) {

            }

            
            try {
                temp = result.getString("refundID");
                total += "退款平台单号：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("transactionId");
                total += "平台订单号：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("outTransNo");
                total += "商户订单号：" + temp + "\n";
            } catch (JSONException e) {

            }
            try {
                temp = result.getString("outRefundNo");
                total += "商户退款单号：" + temp + "\n";
            } catch (JSONException e) {

            }

           //聚合新增以上
            try {
                temp = result.getString("appId");
                total += "应用ID ：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("rspCode");
                total += "应答码 ：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("merchName");
                total += "商户名称 ：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("merchId");
                total += "商户编号：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("termId");
                total += "终端编号 ：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("cardNo");
                total += "卡号 ：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("voucherNo");
                total += "凭证号：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("batchNo");
                total += "批次号 ：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("isserCode");
                total += "发卡行号：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("acqCode");
                total += "收单行号 ：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("authNo");
                total += "授权码 ：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("refNo");
                total += "参考号：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("transTime");
                total += "交易时间 ：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("transDate");
                total += "交易日期 ：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("transAmount");
                total += "交易金额 ：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("origAuthNo");
                total += "原授权码 ：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("origVoucherNo");
                total += "原凭证号 ：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("origRefNo");
                total += "原参考号 ：" + temp + "\n";
            } catch (JSONException e) {

            }
            
            try {
                temp = result.getString("c2b");
                total += "支付码号 ：" + temp + "\n";
            } catch (JSONException e) {

            }

            try {
                temp = result.getString("c2bVoucherNo");
                total += "付款凭证号 ：" + temp + "\n";
            } catch (JSONException e) {

            }
            try {
                temp = result.getString("origC2bVoucherNo");
                total += "原付款凭证号 ：" + temp + "\n";
            } catch (JSONException e) {

            }

            return total;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

    }
}
