package com.synics.gymp.bluec;

/**
 * Created by Administrator on 2016/12/25.
 */

public class CPaySum {
    private String paymentcode;
    private String paymentname;
    private double amount;
    private double avalues;

    public CPaySum(String paymentcode, String paymentname, double amount, double avalues) {
        this.paymentcode = paymentcode;
        this.paymentname = paymentname;
        this.amount = amount;
        this.avalues = avalues;
    }

    public String getPaymentcode() {
        return paymentcode;
    }

    public void setPaymentcode(String paymentcode) {
        this.paymentcode = paymentcode;
    }

    public String getPaymentname() {
        return paymentname;
    }

    public void setPaymentname(String paymentname) {
        this.paymentname = paymentname;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAvalues() {
        return avalues;
    }

    public void setAvalues(double avalues) {
        this.avalues = avalues;
    }
}
