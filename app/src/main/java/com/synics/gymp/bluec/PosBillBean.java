package com.synics.gymp.bluec;

import com.synics.gymp.bluec.DataBean;

import java.io.Serializable;

/**
 * 收银订单表头 JavaBean
 * Created by Administrator on 2016/10/30.
 */

public class PosBillBean extends DataBean implements Serializable {
    private long billkey;
    private int arecno;
    private int crecno;
    private String billid;
    private String yssbillid;
    private String erpbillid;
    private int isminus;
    private int billtype;
    private long obillkey;
    private String orgid;
    private String storeid;
    private String counterid;
    private String mposid;
    private String supplyid;
    private String cashierid;
    private String cashier;
    private String salesid;
    private String sales;
    private String billdate;
    private String belongdate;
    private String stime;
    private int clienttype;
    private String viptype;
    private String vipid;
    private String customer;
    private String mtel;
    private int sex;
    private int age;
    private double pointsgain;
    private double points;
    private int bs;
    private double quantity;
    private double totalamount;
    private double vipdiscount;
    private double counterdiscount;
    private double cstoreshare;
    private double csupplyshare;
    private String totalpromoteno;
    private double totaldiscount;
    private int totaldisatt;
    private double totaldiscountrate;
    private double totalmaxdiscountrate;
    private int totaldisshareatt;
    private double tsupplysharerate;
    private double tsupplyshare;
    private double tstoreshare;
    private double sumdiscount;
    private double amount;
    private int isyss;
    private int paidbycash;
    private int printcount;
    private int couponsent;
    private int billstate;
    private double sysy;
    private int issaved;
    private String lastuptime;
    private int ispj;
    private double fquantity;
    private double famount;
    private String viptypename;
    private String erpcashid;
    private double popzk = 0;
    private double rulezk = 0;
    private String managerid;
    private String manager;
    private String paycounterid;
    private String payerpcashid;
}
