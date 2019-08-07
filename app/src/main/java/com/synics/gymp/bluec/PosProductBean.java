package com.synics.gymp.bluec;

import java.io.Serializable;

/**
 * 收银商品数据 JavaBean
 * Created by ashkin on 2016/9/13.
 */
public class PosProductBean extends DataBean implements Serializable {
    private long billkey;
    private int seq;
    private String poscode;
    private String posname;
    private String poscodetype;
    private String barcode;
    private String productcode;
    private String productname;
    private String catalog;
    private String stype;
    private String spec;
    private String brand;
    private String groupno;
    private String color;
    private String nsize;
    private String unit;
    private double packsize;
    private double oprice;
    private double iprice;
    private double quantity;
    private double totalamount;
    private String promoteno;
    private int discountsort = 0;
    private int counterdiscountatt;
    private String counterdiscountrate;
    private double counterdiscountmaxrate = 80;
    private double counterdiscount;
    private int ccountershareatt = 0;
    private String csupplysharerate;
    private double csupplyshare;
    private double cstoreshare;
    private int vipdiscountatt = 1;
    private String vipdiscountrate;
    private double vipdiscountmaxrate = 80;
    private double vipdiscount;
    private double totaldiscount;
    private double tsupplyshare;
    private double tstoreshare;
    private double sumdiscount;
    private double price;
    private double amount;
    private double fquantity;
    private double famount;
    private int ispj;
    private double popzk = 0;
    private double rulezk = 0;

    /*检查数据是否合法,合法就返回空串*/
    public void checkData() throws Exception {
        if (poscode.equals("") || posname.equals("")) {
            throw new Exception("请输入商品码");
        } else if (iprice <= 0) {
            throw new Exception("商品原价必须大于0");
        }
        if (iprice <= 0) {
            throw new Exception("商品原价必须大于0");
        }
        double f = strToDouble(counterdiscountrate);
        int n = counterdiscountatt;
        if (f < 0) {
            throw new Exception("单品打折不能输入负数");
        }
        if (n == 0 && f != 0 && (f < counterdiscountmaxrate || f > 100)) {
            throw new Exception("单品折扣率必须在" + counterdiscountmaxrate + "-100之间");
        }

        f = strToDouble(csupplysharerate);
        //n = strToInt(ccountershareatt);
        if (f < 0 || f > 100) {
            throw new Exception("折扣分摊比例应该是0-100之间");
        }

        f = strToDouble(vipdiscountrate);
        n = vipdiscountatt;
        if (f < 0) {
            throw new Exception("会员打折不能输入负数");
        }
        if (n == 0 && f != 0 && (f < vipdiscountmaxrate || f > 100)) {
            throw new Exception("会员折扣率必须在" + vipdiscountmaxrate + "-100之间");
        }
        if (amount < 0) {
            throw new Exception("打折合计金额不能大于商品金额");
        }
    }

    /*数据发生变化事件,用来计算字段的值*/
    private void OnCalField() {
        //计算折扣
        double counterrate = (strToDouble(counterdiscountrate) == 0 && counterdiscountatt == 0) ? 100.0 : strToDouble(counterdiscountrate);
        double viprate = (strToDouble(vipdiscountrate) == 0 && vipdiscountatt == 0) ? 100.0 : strToDouble(vipdiscountrate);

        totalamount = CUtil.mul(quantity, iprice, CGlobalData.digiNum);     // 合计金额
        double totalamount0 = 0;
        if (discountsort == 0) {                // 先专柜后会员打折
            if (counterdiscountatt == 0) {   // 专柜打折率
                counterdiscount = CUtil.mul(totalamount, 1 - counterrate / 100.0, CGlobalData.digiNum);
            } else {
                counterdiscount = CUtil.mul(counterrate, quantity, CGlobalData.digiNum);
            }
            totalamount0 = CUtil.sub(totalamount, counterdiscount);
            /* 暂时不能修改
            if (vipdiscountatt == 0) {   // 会员打折率
                vipdiscount = CUtil.mul(totalamount0, 1 - viprate / 100.0, CGlobalData.digiNum);
            } else {
                vipdiscount = CUtil.mul(viprate, quantity, CGlobalData.digiNum);
            }*/
        } else {
            /* 暂时不能修改
            if (vipdiscountatt == 0) {   // 会员打折率
                vipdiscount = CUtil.mul(totalamount, 1 - viprate / 100.0, CGlobalData.digiNum);
            } else {
                vipdiscount = CUtil.mul(viprate, quantity, CGlobalData.digiNum);
            }*/
            totalamount0 = CUtil.sub(totalamount, vipdiscount);
            if (counterdiscountatt == 0) {   // 专柜打折率
                counterdiscount = CUtil.mul(totalamount0, 1 - counterrate / 100.0, CGlobalData.digiNum);
            } else {
                counterdiscount = CUtil.mul(counterrate, quantity, CGlobalData.digiNum);
            }
        }
        sumdiscount = CUtil.add(counterdiscount + vipdiscount + popzk + rulezk, totaldiscount);
        amount = CUtil.sub(totalamount, sumdiscount);  //应收
        price = CUtil.divide(amount, quantity, 2);
        //计算折扣分摊
        switch (ccountershareatt) {
            case 0:
                csupplyshare = counterdiscount;
                break;
            case 1:
                csupplyshare = 0;
                break;
            case 2:
                csupplyshare = CUtil.mul(counterdiscount, strToDouble(csupplysharerate) / 100.0, 1);
                break;
        }
        cstoreshare = CUtil.sub(counterdiscount, csupplyshare);
    }

    @Override
    public Boolean OnFieldChange(String fieldName) {
        if (fieldName == null || fieldName.equals("quantity") || fieldName.equals("iprice") || fieldName.equals("discountsort") ||
                fieldName.equals("counterdiscountatt") || fieldName.equals("counterdiscountrate") || fieldName.equals("ccountershareatt") ||
                fieldName.equals("csupplysharerate") || fieldName.equals("vipdiscountatt") || fieldName.equals("vipdiscountrate") ||
                fieldName.equals("vipdiscount")) {
            OnCalField();
            return true;
        }
        return false;
    }
}
