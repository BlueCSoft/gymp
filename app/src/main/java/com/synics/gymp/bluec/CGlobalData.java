package com.synics.gymp.bluec;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Administrator on 2016/10/25.
 */

public class CGlobalData {
    public static int mErrorCode = 0;       //异常代码
    public static String mErrorMsg = "";    //异常信息

    private static CGlobalData sGlobalData;

    public static String time;                //当前时间
    public static String origbillid;          //当前订单号
    public static long origbillkey;          //当前订单key
    public static int billtype;              //单据类型,0-销售,1-预售
    public static int osid = 0;
    public static String orgid = "";
    public static String org = "";
    public static int ssid = 0;
    public static String storeid = "";
    public static String store = "";
    public static int csid = 0;
    public static String counterid = "";
    public static String counter = "";
    public static int sid = 0;
    public static String cashierid = "";
    public static String cashier = "";
    public static String supplyid = "";      //供应商编号

    public static int lockscreen = 0;
    public static int roundbit = 0;
    public static String receiptnote = "";   //小票页脚说明
    public static String erpcashid = "";     //erp系统收银号

    public static int ispaycash = 0;
    public static int billgoodsnum = 0;      //当前订单商品数
    public static String workdate = "2019-07-01";
    public static String paramdef1;
    public static String paramdef2;
    public static String paramdef3;
    public static String paramdef4;
    public static String paramdef5;

    public static List<CObjInfo> saleslist;     //营业员表
    public static List<CObjInfo> poscodelist;   //操作码表
    public static List<CObjInfo> billstatelist; //订单状态表
    public static List<CObjInfo> paymentlist;   //支付方式表
    public static List<CObjInfo> viptypelist;   //卡类型表

    public static List<String> ticketHead = null; //小票页头
    public static List<String> ticketBody = null;  //小票联数
    public static List<String> ticketFoot = null; //小票页脚

    public static int paramCashierAndPay = -3; //收银界面返回录单界面码
    public static int resultResult = -3;
    public static String numFmt = "0.0";     //默认数字格式
    public static int digiNum = 1;             //保留小数位数

    public static String salesid = "";         //营业员代码和名称
    public static String sales = "";
    public static String managerid = "";       //授权人代码
    public static String managername = "";     //授权人名称

    public static Intent returnData;           //返回数据

    public static CGlobalData get(Context context) {
        if (sGlobalData == null) {
            sGlobalData = new CGlobalData(context);
        }
        return sGlobalData;
    }

    private CGlobalData(Context context) {
        poscodelist = new ArrayList<CObjInfo>();
        saleslist = new ArrayList<CObjInfo>();
        billstatelist = new ArrayList<CObjInfo>();
        paymentlist = new ArrayList<CObjInfo>();
        viptypelist = new ArrayList<CObjInfo>();

        ticketHead = new ArrayList<String>();
        ticketBody = new ArrayList<String>();
        ticketFoot = new ArrayList<String>();
    }

    //清空操作码和营业员
    public void clear() {
        poscodelist.clear();
        saleslist.clear();
        billstatelist.clear();
        paymentlist.clear();
        viptypelist.clear();
    }

    public void setSales(CObjInfo salesInfo) {
        this.saleslist.add(salesInfo);
    }

    public void setPoscode(CObjInfo posCode) {
        this.poscodelist.add(posCode);
    }

    public void setBillstates(CObjInfo stateInfo) {
        this.billstatelist.add(stateInfo);
    }

    public void setPeyments(CObjInfo paymentInfo) {
        this.paymentlist.add(paymentInfo);
    }

    public void setViptypes(CObjInfo viptypeInfo) {
        this.viptypelist.add(viptypeInfo);
    }

    /*判断一个输入的操作码是否存在*/
    public static boolean posCodeIsExists(String posCode) {
        boolean isExists = false;
        for (int i = 0; !isExists && i < poscodelist.size(); i++)
            isExists = posCode.equals(poscodelist.get(i).getObjName())||posCode.equals(poscodelist.get(i).getBarCode());
        return isExists;
    }

    /*获取操作码*/
    public static String getCodeByInpCode(String code) {
        String result = "";
        for (int i = 0; i < poscodelist.size(); i++)
            if(code.equals(poscodelist.get(i).getObjName())||code.equals(poscodelist.get(i).getBarCode())){
                result = poscodelist.get(i).getObjName();
                break;
            }
        return result;
    }

    public static int getCodePosition(String shortname) {
        int result = 0;
        for (int i = 0; i < poscodelist.size(); i++)
            if(shortname.equals(poscodelist.get(i).getShowName())){
                result = i;
                break;
            }
        return result;
    }
    /*根据条件过滤获取满足条件的操作码组*/
    public String[] getFilterPosCode(String inpPosCode) {
        Vector<String> v = new Vector<String>();
        for (int i = 0; i < poscodelist.size(); i++) {
            String s = poscodelist.get(i).getObjName();
            if (s.indexOf(inpPosCode) == 0) v.add(s);
        }

        String[] result = null;
        if (v.size() >= 0) {
            result = new String[v.size()];
            v.copyInto(result);
        }
        return result;
    }

    /*获取操作码列表*/
    public static String[] getPoscodes() {
        String[] codes = new String[poscodelist.size()];
        for (int i = 0; i < poscodelist.size(); i++)
            codes[i] = poscodelist.get(i).getObjName();
        return codes;
    }

    public static String[] getPosnames() {
        String[] names = new String[poscodelist.size()];
        for (int i = 0; i < poscodelist.size(); i++)
            names[i] = poscodelist.get(i).getShowName();
        return names;
    }

    /*获取操作码全码*/
    public static String getLongPoscode(String sCode) {
        String lCode = "";
        for (int i = 0; i < poscodelist.size(); i++)
            if (sCode.equals(poscodelist.get(i).getObjName())) {
                lCode = poscodelist.get(i).getObjId();
            }
        return lCode;
    }

    /*判断一个输入的操作码是否存在*/
    public static boolean salesCodeIsExists(String salesCode) {
        boolean isExists = false;
        for (int i = 0; !isExists && i < saleslist.size(); i++)
            isExists = salesCode.equals(saleslist.get(i).getObjId());
        return isExists;
    }

    /*根据营业员名称返回营业员代码*/
    public String getSalesIdByName(String salesname) {
        String salesid = "";
        for (int i = 0; i < saleslist.size(); i++)
            if (salesname.equals(saleslist.get(i).getObjName())) {
                salesid = saleslist.get(i).getObjId();
                break;
            }
        return salesid;
    }

    /*获取营业员列表*/
    public static String[] getSales() {
        String[] sales = new String[saleslist.size()];
        for (int i = 0; i < saleslist.size(); i++)
            sales[i] = saleslist.get(i).getObjName();
        return sales;
    }

    public static String[] getSalesWithAll() {
        String[] sales = new String[saleslist.size()+1];
        sales[0] = "全部";
        for (int i = 0; i < saleslist.size(); i++)
            sales[i+1] = saleslist.get(i).getObjName();
        return sales;
    }
    /*根据营业员代码返回位置*/
    public static int getSalesPosition(String salesCode) {
        for (int i = 0; i < saleslist.size(); i++)
            if (salesCode.equals(saleslist.get(i).getObjId())) {
                return i;
            }
        return 0;
    }

    public static String getSalesIdByPosition(int position) {
        if (position < saleslist.size() && position >= 0) return saleslist.get(position).getObjId();
        return "";
    }

    /*卡类型操作*/
    public static String[] getViptypes() {
        String[] bufs = new String[viptypelist.size()];
        for (int i = 0; i < viptypelist.size(); i++)
            bufs[i] = viptypelist.get(i).getObjName();
        return bufs;
    }

    public static String[] getViptypesAll() {
        String[] bufs = new String[viptypelist.size()+1];
        bufs[0] = "全部";
        for (int i = 0; i < viptypelist.size(); i++)
            bufs[i+1] = viptypelist.get(i).getObjName();
        return bufs;
    }

    public static String getViptypeName(String sno) {
        for (int i = 0; i < viptypelist.size(); i++)
            if (sno.equals(viptypelist.get(i).getObjId())) {
                return viptypelist.get(i).getObjName();
            }
        return "";
    }

    public static int getViptypePosition(String sno) {
        for (int i = 0; i < viptypelist.size(); i++)
            if (sno.equals(viptypelist.get(i).getObjId())) {
                return i;
            }
        return 0;
    }

    public static String getViptypeIdByPosition(int position) {
        if (position < viptypelist.size() && position >= 0) return viptypelist.get(position).getObjId();
        return "";
    }

    public static String[] getPayments(boolean withAll) {
        int size = paymentlist.size();
        int j = 0;
        if(withAll){
            size++;
        }
        String[] names = new String[size];
        if(withAll){
            names[0] = "全部";
            j++;
        }
        for (int i = 0; i < paymentlist.size(); i++)
            names[j++] = paymentlist.get(i).getObjName();
        return names;
    }

    public static String[] getPaymentsTo() {
        int size = paymentlist.size();
        int j = 0;
        String[] names = new String[size-1];
        for (int i = 1; i < paymentlist.size(); i++)
            names[j++] = paymentlist.get(i).getObjName();
        return names;
    }

    public static String getPaymentIdByPosition(int position) {
        if (position < paymentlist.size() && position >= 0) return paymentlist.get(position).getObjId();
        return "";
    }

    public static String getPaymentNameByPosition(int position) {
        if (position < paymentlist.size() && position >= 0) return paymentlist.get(position).getObjName();
        return "";
    }

    public static String getBillStateName(String billstate ) {
        for (int i = 0; i < billstatelist.size(); i++)
            if (billstate.equals(billstatelist.get(i).getObjId())) {
                return billstatelist.get(i).getObjName();
            }
        return "";
    }

    public static void setValues(JSONObject obj) throws Exception {
        origbillkey = obj.getLong("origbillkey");
        origbillid = obj.getString("origbillid");
    }

    public static String getTicketHead(){
        String result = "友谊移动POS收银小票\n";
        if(ticketHead.size()>0){
            result = "";
            for(int i=0;i<ticketHead.size();i++)
                result += ticketHead.get(i).toString()+"\n";
        }
        return result;
    }

    public static String getTicketFoot(){
        String result = "请妥善保管好此联电脑小票，" + "\n"
                      + "以此作退换商品与办卡的凭证，" + "\n"
                      + "可在购物当月内到购物门店开具发票。" + "\n"
                      + "欢迎再次光临！" + "\n";
        if(ticketFoot.size()>0){
            result = "";
            for(int i=0;i<ticketFoot.size();i++)
                result += ticketFoot.get(i).toString()+"\n";
        }
        return result;
    }

    public static String[] getTicketBody(){
        String[] result = null;
        if(ticketBody.size()>0){
            result = new String[ticketBody.size()];
            for(int i=0;i<ticketBody.size();i++)
                result[i] = ticketBody.get(i).toString();
        }else{
            result = new String[1];
            result[0] = "第一联：顾客联";
        }
        return result;
    }

    public static String getReqCode(){
        return CConfig.SMPOSID+ CUtil.getSTime();
    }
}
