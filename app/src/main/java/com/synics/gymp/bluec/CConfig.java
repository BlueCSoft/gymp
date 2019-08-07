package com.synics.gymp.bluec;

import android.content.Context;
import com.synics.gymp.iface.IDataLoadCallback;

/**
 * 配置参数类
 * Created by Administrator on 2016/10/25.
 */

public class CConfig extends CObject {
    public static String SAPPID= "com.synics.gymp";                       //appid
    public static String SMPOSID = "00947";                                //终端号
    public static String SERVER_URL = "";                                   //中基层地址和端口号
    public static String SERVER_APP = "/y2m/bin/";
    public static boolean isDebug = false;                                //是否调试模式

    public static String APPIDNAME = "com.pax.up.stdgd";

    //事件定义,10001-19999是网络数据请求,20001-29999是数据集事件

    public final static int HTTPEVENT_DEFAULT= 8000;

    public final static int HTTPEVENT_LOGIN = 10001;            //登录事件
    public final static int HTTPEVENT_GETDAYBILLINFO = 10002;  //获取一张订单的明细
    public final static int HTTPEVENT_GETBILLKEYANID = 10003;  //获取订单KEY和ID
    public final static int HTTPEVENT_VIPQUERY = 11006;         //会员信息查询
    public final static int HTTPEVENT_VIPQUERY1 = 11007;
    public final static int CALLEVENT_SACNBILL = 11008;         //扫订单二维码

    public final static int HTTPEVENT_BILLINFO = 11999;         //获取一张订单信息
    public final static int CALLEVENT_CASHMAIN = 12000;         //打开收银
    public final static int HTTPEVENT_SELPOSCODE = 12001;       //选择操作码
    public final static int HTTPEVENT_GETPRODUCTINFO = 12002;  //获取和验证商品信息
    public final static int CALLEVENT_EDITGOODS = 12003;        //打开添加商品窗口
    public final static int CALLEVENT_SCANPOSCODE = 12004;      //扫销售码条码
    public final static int CALLEVENT_SCANPRODUCT = 12005;      //扫商品条码

    public final static int CALLEVENT_CALERPDIS= 12007;         //计算促销折扣
    public final static int CALLEVENT_TOTALDIS = 12009;         //修改总价折扣
    public final static int CALLEVENT_SALESLOGIN = 12011;
    public final static int CALLEVENT_CONTACTINP = 12013;      //联系信息输入
    public final static int CALLEVENT_GOODSINP = 12015;        //商品信息补录
    public final static int CALLEVENT_SCANBILLID = 12015;

    public final static int HTTPEVENT_BILLUPDATE = 13001;      //订单提交保存(到支付)
    public final static int HTTPEVENT_BILLUPDATEGD = 13002;    //订单提交保存(挂单)
    public final static int HTTPEVENT_BILLUPDATEKT = 13003;    //订单提交保存(款台)
    public final static int HTTPEVENT_BILLUPDATESY = 13004;    //订单提交保存(收银)
    public final static int HTTPEVENT_BILLCANCEL = 13005;      //订单撤销
    public final static int RETURNEVENT_BILLCANCEL = 13008;
    public final static int HTTPEVENT_BILLREFUND = 13009;
    public final static int CALLEVENT_CASHPASH = 13009;        //款台收款
    public final static int HTTPEVENT_CASHPASH = 13010;
    public final static int CALLEVENT_CASHCOUNTER = 13011;     //专柜收款
    public final static int HTTPEVENT_CASHCOUNTER = 13012;

    public final static int CALLEVENT_TONOFINISHI = 13020;     //打开挂单列表
    public final static int HTTPEVENT_TONOFINISHI = 13021;
    public final static int RETURNEVENT_TONOFINISHI = 13022;

    public final static int HTTPEVENT_PAYBANK = 16001;         //银联支付
    public final static int HTTPEVENT_PAYMARGE = 16002;        //聚合支付
    public final static int HTTPEVENT_PAYSCORE = 16004;        //积分卡支付
    public final static int HTTPEVENT_PAYCOUPON = 16005;
    public final static int HTTPEVENT_READSCORE = 16014;        //积分卡支付
    public final static int CALLEVENT_PAYSCORE = 16024;        //积分卡支付
    public final static int CALLEVENT_PAYCOUPON = 16024;

    public final static int HTTPEVENT_PAYOTHER = 16006;        //补录支付
    public final static int HTTPEVENT_PAY = 16007;             //现金支付
    public final static int HTTPEVENT_CONFIRM = 16010;         //支付完成，打印小票

    public final static int HTTPEVENT_PRINTBILL = 18001;       //补打小票
    public final static int HTTPEVENT_PRINTQBAR = 18002;       //补打二维码
}
