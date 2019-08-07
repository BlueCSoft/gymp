package com.synics.gymp.bluec;

/**
 * 收银订支付数据 JavaBean
 * Created by Administrator on 2016/10/30.
 */

public class PosPaymentBean extends DataBean {
    private long billkey;
    private int seq;
    private long  payid;
    private String paymentcode;
    private String paymentname;
    private double amount;
    private double avalues;
    private double sysy;
    private double balance;
    private double erate;
    private String reqcode;
    private String reqtime;
    private String tradecode;
    private String paytime;
    private int paystatus;
    private int quantity;
    private double price;
    private int payatt;
    private int recatt;
    private String authuid;
    private String authuname;
    private String lsh;
    private String bank_type;
    private String trade_channel;

    public PosPaymentBean() {
    }

}
