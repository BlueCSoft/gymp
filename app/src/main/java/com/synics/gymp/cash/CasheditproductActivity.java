package com.synics.gymp.cash;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.pax.face.scancodec.CodecActivity;
import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivityEx;
import com.synics.gymp.bluec.CBillHead;
import com.synics.gymp.bluec.CBillProducts;
import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.bluec.CDataSource;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.COrigBill;
import com.synics.gymp.window.CameraDialog;
import com.synics.gymp.window.CashSelGoodsDialog;

public class CasheditproductActivity extends BaseActivityEx {
    int recno = 0;   //当前订单表商品记录号
    CBillHead mHead = COrigBill.get().getMbillHead();
    CBillProducts mProduct = new CBillProducts();
    CBillProducts mBillProducts = COrigBill.get().getMbillProducts();
    private CDataSource dsProduct = new CDataSource(this);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_casheditproduct;
    }

    @Override
    protected void initViewAfter() {
        Intent i = getIntent();
        recno = Integer.parseInt(i.getStringExtra("recno")); //获取需要修改的商品记录，0为新增商品
        try {
            if (recno > 0) {
                mProduct.copyFrom(mBillProducts.getRow(recno), null);
            }
        } catch (Exception ex) {
            toast(ex.getMessage());
        }
        if(!mHead.$("vipid").equals("")) {
            ((LinearLayout) findViewByTag("dsortoppanel")).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewByTag("vipoppanel")).setVisibility(View.VISIBLE);
        }
        mProduct.addDataSource(dsProduct);
        dsProduct.bindViews(getAllChildViews());
    }

    /*选择操作码*/
    public void onSelectCode(View source) {
        new CashSelGoodsDialog(CConfig.HTTPEVENT_SELPOSCODE, R.layout.dialog_cashselgoods, this, null);
    }

    @Override
    public void conditionSelect(int requestCode, Boolean isConfirm, View source, int position, String value) {
        try {
            switch (requestCode) {
                case CConfig.HTTPEVENT_SELPOSCODE:  //选择了销售码,获取商品信息
                    showProgress("");
                    CHttpUtil.Post("icommon/ygmpgetproductinfo.jsp", new String[]{
                            "code", value, "vipid", COrigBill.get().getMbillHead().$("vipid"), "stime", CGlobalData.time
                    }, this, CConfig.HTTPEVENT_GETPRODUCTINFO);
                    break;
            }
        } catch (Exception ex) {
            msgBox("", ex.getMessage());
        }
    }
    @Override
    public Boolean conditionCheck(int requestCode, Boolean isConfirm, View source, int position, String value) {
        return true;
    }

    @Override
    protected void doActivityResult(int requestCode,Intent data) {
        try {
            switch (requestCode) {
                case CConfig.CALLEVENT_SCANPOSCODE:     //扫描销售码条码
                    conditionSelect(CConfig.HTTPEVENT_SELPOSCODE,true,null,0,CGlobalData.returnData.getStringExtra("code"));
                    break;
                case CConfig.CALLEVENT_SCANPRODUCT:
                    mProduct.setString("productcode", CGlobalData.returnData.getStringExtra("code"));
                    break;
            }
        }catch (Exception ex){
            toast(ex.getMessage());
        }
    }
    /*商品确认*/
    public void doComfirm(View source) {
        try {
            if(mProduct.isEmpty()){
                toast("请输入商品信息");
                return;
            }
            dsProduct.updateData();
            mProduct.getRow().checkData();  //校验数据，不合法抛出异常
            if (recno == 0) {
                mBillProducts.insert();
            }
            mBillProducts.copyFrom(mProduct.getRow(), new String[]{"billkey", "seq"});
            close(CConfig.CALLEVENT_EDITGOODS);
        } catch (Exception ex) {
            toast(ex.getMessage());
        }
    }

    //减少数量
    public void decNumClick(View source) {
        try {
            dsProduct.updateData();
            if (!mProduct.isEmpty()&&mProduct.getDouble("quantity")-1>0) {
                mProduct.setDouble("quantity",mProduct.getDouble("quantity")-1);
            }
        } catch (Exception ex) {
            toast(ex.getMessage());
        }
    }

    //增加数量
    public void addNumClick(View source) {
        try {
            dsProduct.updateData();
            if (!mProduct.isEmpty()) {
                mProduct.setDouble("quantity",mProduct.getDouble("quantity")+1);
            }
        } catch (Exception ex) {
            toast(ex.getMessage());
        }
    }

    //销售码条码
    public void onScanCodeClick(View source){
        callActivity(this,CodecActivity.class,CConfig.CALLEVENT_SCANPOSCODE);
    }

    //扫货号条码
    public void onScanhhClick(View source){
        callActivity(this,CodecActivity.class,CConfig.CALLEVENT_SCANPRODUCT);
        //new CameraDialog(CConfig.CALLEVENT_SCANPRODUCT, R.layout.fragment_scan, this, null);
    }

    @Override
    protected void handleHttp(int requestCode, CJson json) {
        try {
            switch (requestCode) {
                case CConfig.HTTPEVENT_GETPRODUCTINFO:  //选择了销售码,获取商品信息
                    if(json.getInt("recordcount")>0){
                        mProduct.setDisableControls();
                        mProduct.setString("poscode",json.getStringEx("rcode"));
                        mProduct.setString("posname",json.getStringEx("rname"));
                        mProduct.setString("productcode",json.getStringEx("rbarcode"));
                        mProduct.setString("catalog",json.getStringEx("rdzxl"));
                        mProduct.setString("brand",json.getStringEx("rpp"));
                        mProduct.setString("stype",json.getStringEx("rtype"));
                        mProduct.setString("unit",json.getStringEx("runit"));
                        mProduct.setString("groupno",json.getStringEx("rgz"));
                        mProduct.setString("barcode",json.getStringEx("rbarcode"));
                        mProduct.setString("spec",json.getStringEx("rspec"));
                        mProduct.setDouble("packsize",json.getDoubleEx("rbzhl"));
                        mProduct.setDouble("oprice",json.getDoubleEx("rlsj"));
                        mProduct.setDouble("iprice",json.getDoubleEx("rlsj"));
                        mProduct.setDouble("counterdiscountmaxrate",json.getDoubleEx("rpopzk")*100);
                        mProduct.setDouble("vipdiscountmaxrate",json.getDoubleEx("rcustzk")*100);
                        mProduct.setEnableControls();

                        //判断价格是否可以修改
                        boolean canedit = json.getDoubleEx("rlsj")==0;
                        ((EditText)getViewByViewTag(getWindow().getDecorView(),"iprice")).setEnabled(canedit);

                        //光标跳转
                        String focusField = "";

                        if(mProduct.$("productcode").equals("")){
                            focusField = "productcode";
                        }else{
                          if(canedit){
                              focusField = "iprice";
                              ((EditText)getViewByViewTag(getWindow().getDecorView(),"iprice")).setText("");
                          }else{
                              focusField = "quantity";
                          }
                        }
                        EditText editText = ((EditText)getViewByViewTag(getWindow().getDecorView(),focusField));

                        editText.setFocusable(true);
                        editText.setFocusableInTouchMode(true);
                        editText.requestFocus();
                    }else{
                        toast("找不到商品信息");
                    }
                    break;
            }
        } catch (Exception ex) {
            msgBox("", ex.getMessage());
        }
    }

    protected boolean isDisabledBack() {
        return !mProduct.isEmpty();
    }
}
