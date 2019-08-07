package com.synics.gymp.bluec;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;
import com.pax.face.printer.PrinterTester;
import com.synics.gymp.R;
import com.synics.gymp.util.CQrbar;

/**
 * Created by Administrator on 2019/7/11.
 */

public class CPrint extends CObject {
    private static String divStr = "--------------------------------";
    private static int maxChar = 32;

    //打印款台收银凭证
    public static void printRecerptPash(CBill mBill) throws Exception {
        try {
            CBillHead mBillHead = mBill.getMbillHead();
            CBillProducts mBillProducts = mBill.getMbillProducts();

            PrinterTester printer = PrinterTester.getInstance();
            printer.init();
            //设置字符集
            printer.fontSet(EFontTypeAscii.FONT_16_24, EFontTypeExtCode.FONT_24_24);
            printer.spaceSet((byte) 0, (byte) 0);
            printer.leftIndents((short) 0);
            printer.setGray(1);

            printer.printStr(CUtil.formatAlign("广州友谊商店", maxChar, 1));
            printer.printStr("交易号:" + mBillHead.$("billid"));
            printer.printStr("收银机:" + mBillHead.$("counterid"));
            printer.printStr("收银员:" + mBillHead.$("cashier"));
            printer.printStr("**************销售**************");
            printer.printStr("    " + mBillHead.$("billdate"));
            printer.printStr(divStr);
            for (int i = 0; i < mBillProducts.getRecordCount(); i++) {
                mBillProducts.moveRecord(i + 1);
                printer.printStr(CUtil.formatAlign(mBillProducts.$("poscode"), mBillProducts.$("iprice"), maxChar));
                printer.printStr("  " + mBillProducts.$("posname") + "×" + mBillProducts.$("quantity") + mBillProducts.$("unit"));
                if (mBillProducts.getDouble("totaldiscount") != 0) {
                    printer.printStr("  折扣" + mBillProducts.$("totaldiscount"));
                }
            }
            printer.printStr(divStr);
            printer.printStr(CUtil.formatAlign("合计件数:", mBillHead.$("quantity"), maxChar));
            if (!mBillHead.$("vipid").equals("")) {
                printer.printStr(CGlobalData.getViptypeName(mBillHead.$("viptype")) + mBillHead.$("vipid"));
            }
            printer.printStr(divStr);
            printer.printStr(CUtil.formatAlign("原价合计:", mBillHead.$("totalamount"), maxChar));
            printer.printStr(CUtil.formatAlign("折扣合计:", mBillHead.$("totaldiscount"), maxChar));
            printer.printStr(CUtil.formatAlign("应付:", mBillHead.$("amount"), maxChar));
            Bitmap bitmap = CQrbar.encodeAsBitmap(mBillHead.$("billid")+","+mBillHead.$("billkey"), BarcodeFormat.QR_CODE, 320, 320);
            printer.leftIndents((short) 30);
            printer.printBitmap(bitmap);
            //printer.fontSet(EFontTypeAscii.FONT_16_32, EFontTypeExtCode.FONT_32_32);
            printer.leftIndents((short) 0);
            printer.printStr("请拿本凭证到收款台扫码付款！");
            printer.step(120);
            final String status = PrinterTester.getInstance().start();
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    //打印小票
    public static void printRecerpt(CBill mBill) throws Exception {
        try {
            CBillHead mBillHead = mBill.getMbillHead();
            CBillProducts mBillProducts = mBill.getMbillProducts();
            CBillPayment mBillPayment = mBill.getMbillPayment();
            int paylength = mBillPayment.getRecordCount();
            PrinterTester printer = PrinterTester.getInstance();
            printer.init();
            //设置字符集
            printer.fontSet(EFontTypeAscii.FONT_16_24, EFontTypeExtCode.FONT_24_24);
            //设置字间距，行间距
            printer.spaceSet((byte) 0, (byte) 5);
            printer.leftIndents((short) 0);
            printer.setGray(1);
            printer.printStr(CUtil.formatAlign("广州友谊商店", maxChar, 1));
            printer.printStr("交易号:" + mBillHead.$("billid"));
            if (mBillHead.getInt("printcount") > 0) {
                printer.printStr("    ***重新打印票据***");
            }
            printer.printStr("收银机:" + mBillHead.$("counterid"));
            printer.printStr("收银员:" + mBillHead.$("cashier"));
            printer.printStr("**************销售**************");
            printer.printStr("    " + mBillHead.$("billdate"));
            printer.printStr(divStr);
            for (int i = 0; i < mBillProducts.getRecordCount(); i++) {
                mBillProducts.moveRecord(i + 1);
                printer.printStr(CUtil.formatAlign(mBillProducts.$("poscode"), mBillProducts.$("iprice"), maxChar));
                printer.printStr("  " + mBillProducts.$("posname") + "×" + mBillProducts.$("quantity") + mBillProducts.$("unit"));
                if (mBillProducts.getDouble("totaldiscount") != 0) {
                    printer.printStr("  折扣" + mBillProducts.$("totaldiscount"));
                }
            }
            printer.printStr(divStr);
            printer.printStr(CUtil.formatAlign("合计件数:", mBillHead.$("quantity"), maxChar));
            if (!mBillHead.$("vipid").equals("")) {
                printer.printStr(CGlobalData.getViptypeName(mBillHead.$("viptype")) + mBillHead.$("vipid"));
            }
            printer.printStr(divStr);
            printer.printStr(CUtil.formatAlign("原价合计:", mBillHead.$("totalamount"), maxChar));
            printer.printStr(CUtil.formatAlign("折扣合计:", mBillHead.$("totaldiscount"), maxChar));
            printer.printStr(CUtil.formatAlign("应付:", mBillHead.$("amount"), maxChar));
            printer.printStr(divStr);
            printer.printStr("支付明细:");
            if (mBillPayment.exists("03")) {   //打印银联支付
                do {
                    if (mBillPayment.$("paymentcode").equals("03")) {
                        printer.printStr(CUtil.formatAlign(mBillPayment.$("trade_channel"), mBillPayment.$("amount"), maxChar));
                        printer.printStr("卡号："+mBillPayment.$("reqcode"));
                    }
                    mBillPayment.next();
                } while (!mBillPayment.isEnd());
            }
            if (mBillPayment.exists("0301") || mBillPayment.exists("0204")) {   //打印聚合支付
                do {
                    if (mBillPayment.$("paymentcode").equals("0301") || mBillPayment.$("paymentcode").equals("0204"))
                        printer.printStr(CUtil.formatAlign(mBillPayment.$("paymentname"), mBillPayment.$("amount"), maxChar));
                    mBillPayment.next();
                } while (!mBillPayment.isEnd());
            }
            if (mBillPayment.exists("05")) {   //打印礼券支付
                int sl = 0;
                double je = 0;
                String sname = mBillPayment.$("paymentname");
                do {
                    if (mBillPayment.$("paymentcode").equals("05")) {
                        sl += 1;
                        je += mBillPayment.getDouble("amount");
                    }
                    mBillPayment.next();
                } while (!mBillPayment.isEnd());
                printer.printStr(CUtil.formatAlign(sname + "×" + sl, CUtil.roundEx(je, 1), maxChar));
            }
            if (mBillPayment.exists("04")) {   //打印积分支付
                int sl = 0;
                double je = 0;
                String sname = mBillPayment.$("paymentname");
                printer.printStr("    卡号            消费额  余额");
                do {
                    if (mBillPayment.$("paymentcode").equals("04")) {
                        sl += 1;
                        je += mBillPayment.getDouble("amount");
                        printer.printStr(CUtil.formatAlign(mBillPayment.$("reqcode"),
                                String.format("%7.1f%6.1f", mBillPayment.getDouble("amount"), mBillPayment.getDouble("balance")), maxChar));
                    }
                    mBillPayment.next();
                } while (!mBillPayment.isEnd());
                printer.printStr(CUtil.formatAlign("  "+sname + "合计：", CUtil.roundEx(je, 1), maxChar));
            }
            //打印二维码
            printer.leftIndents((short) 30);
            Bitmap bitmap = CQrbar.encodeAsBitmap(mBillHead.$("billid")+","+mBillHead.$("billkey"), BarcodeFormat.QR_CODE, 320, 320);
            printer.printBitmap(bitmap);
            printer.leftIndents((short) 0);
            printer.printStr(CGlobalData.receiptnote);
            printer.step(120);
            final String status = PrinterTester.getInstance().start();
            Log.i("printbill", status);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }
}
