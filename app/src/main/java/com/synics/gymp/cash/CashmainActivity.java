package com.synics.gymp.cash;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pax.face.scancodec.CodecActivity;
import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivityEx;
import com.synics.gymp.activity.QueryActivity;
import com.synics.gymp.adapter.DatabeanAdapter;
import com.synics.gymp.bluec.CBillHead;
import com.synics.gymp.bluec.CBillProducts;
import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.bluec.CDataSource;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.COrigBill;
import com.synics.gymp.bluec.DataBean;
import com.synics.gymp.iface.IAuthorizeCallBack;
import com.synics.gymp.window.AuthorizeDialog;
import com.synics.gymp.window.ContactDialog;
import com.synics.gymp.window.SalesLoginDialog;
import com.synics.gymp.window.TotalDisInpDialog;
import com.synics.gymp.window.VipInpDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CashmainActivity extends BaseActivityEx implements DatabeanAdapter.DACallback {
    @BindView(R.id.id_cash_main_customer_sp)
    LinearLayout llCustomer_sp;
    @BindView(R.id.id_cash_main_customer)
    LinearLayout llCustomer;

    @BindView(R.id.id_cash_main_goodslist)
    ListView lvGoodslist;

    private CBillHead mBillHead = COrigBill.get().getMbillHead();
    private CBillProducts mBillProducts = COrigBill.get().getMbillProducts();
    private CDataSource dsBillHead = new CDataSource();
    private CDataSource dsBillProducts = new CDataSource();

    GoodsAdapter mAdapter = null;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cashmain;
    }

    private void setSalesInfo() {
        if (!CGlobalData.salesid.equals("")) {
            ((TextView) findViewByTag("salesinfo")).setText(CGlobalData.sales);
        }
    }

    @Override
    protected void initViewAfter() {
        dsBillHead.setOnSetFieldText(new CDataSource.OnSetFieldText() {
            @Override
            public String onSetText(int viewId, String fieldName, String defaultValue) {
                try {
                    if (fieldName.equals("vipid") && !mBillHead.getString("vipid").equals("")) {
                        defaultValue = mBillHead.getString(fieldName).concat("(").concat(mBillHead.getString("viptypename")).concat(")");
                    }
                    if (fieldName.equals("billdate")) {
                        defaultValue = defaultValue.substring(0, 10);
                    }
                    if (fieldName.equals("customer") && !defaultValue.equals("")) {
                        defaultValue = mBillHead.getString(fieldName).concat("(").concat(mBillHead.getString("mtel")).concat(")");
                    }
                } catch (Exception ex) {
                }
                return defaultValue;
            }
        });
        mBillHead.addDataSource(dsBillHead);
        mBillProducts.addDataSource(dsBillProducts);
        getAllChildViews();
        dsBillHead.bindViews(allDatasetView);

        mAdapter = new GoodsAdapter(this, mBillProducts.getRows(), new GoodsBind(), this);
        lvGoodslist.setAdapter(mAdapter);
        lvGoodslist.setOnItemClickListener(mAdapter);

        setSalesInfo();
        ((TextView) findViewByTag("caption")).setText((mBillHead.getInt("billtype") == 0) ? "销售单" : "预售单");

        if (mBillHead.getInt("billtype") == 1) {
            llCustomer_sp.setVisibility(View.VISIBLE);
            llCustomer.setVisibility(View.VISIBLE);
        }
        if(CGlobalData.ispaycash==1){
            findViewById(R.id.id_cash_main_btktsk).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onBeforeDestroy() {
        mBillHead.removeDataSource(dsBillHead);
        mBillProducts.removeDataSource(dsBillProducts);
    }

    public void onSalesLogin(View source) {
        new SalesLoginDialog(CConfig.CALLEVENT_SALESLOGIN, this, null,
                new IAuthorizeCallBack() {
                    @Override
                    public void onConfirm(int requestCode, String uid, String uName) {
                        CGlobalData.salesid = uid;
                        CGlobalData.sales = uName;
                        setSalesInfo();
                        try {
                            mBillHead.setString("salesid", uid);
                            mBillHead.setString("sales", uName);
                        } catch (Exception ex) {

                        }
                        toPay(requestCode);
                    }
                });
    }

    /* 会员查询卡号输入窗口 */
    private void doOpenFindVipOp(int requestCode) {
        if (mBillProducts.isEmpty()) {
            new VipInpDialog(requestCode, this, null,
                    new IAuthorizeCallBack() {
                        @Override
                        public void onConfirm(int requestCode, String uid, String uName) {
                            if (!uid.equals("")) {
                                queryVip(requestCode, uid);
                            } else {
                                callActivity(CashmainActivity.this, CodecActivity.class, requestCode);
                            }
                        }
                    });
        } else {
            toast("录入商品后，不能再刷会员卡");
        }
    }

    public void openFindVipOp(View source) {
        doOpenFindVipOp(CConfig.HTTPEVENT_VIPQUERY);
    }

    /* 查询会员信息 */
    private void queryVip(int requestCode, String kh) {
        CHttpUtil.Post("icommon/getvipinfo.jsp", new String[]{"vipid", kh}, this, requestCode);
    }

    /* 显示会员信息 */
    private void doShowVipInfo(String kh) {
        callActivity(this, CashqueryvipActivity.class, new String[]{"kh=" + kh});
    }

    public void showVipInfo(View source) {
        if (mBillHead.$("vipid").equals("")) {
            doOpenFindVipOp(CConfig.HTTPEVENT_VIPQUERY1);
        } else {
            doShowVipInfo(mBillHead.$("vipid"));
        }
    }

    /* 录入商品 */
    private void doEditGoods(int recno) {
        callActivity(this, CasheditproductActivity.class, new String[]{"recno=" + recno}, CConfig.CALLEVENT_EDITGOODS);
    }

    public void addGoods(View source) {
        doEditGoods(0);
    }

    //计算ERP折扣
    public void doCalErpDiscount(View source) {
        try {
            if(!mBillProducts.isEmpty()) {
                String vsalelist = mBillProducts.getJson();
                showProgress("计算折扣...");
                CHttpUtil.Post("ierp/calerpdiscount.jsp", new String[]{"vcardid", mBillHead.$("vipid"),
                        "vcxsj", mBillHead.$("billdate"), "vsalelist", vsalelist}, this, CConfig.CALLEVENT_CALERPDIS);
            }
        } catch (Exception ex) {
            toast(ex.getMessage());
        }
    }

    private void doTotalDiscount(){
        new TotalDisInpDialog(CConfig.CALLEVENT_TOTALDIS, R.layout.dialog_totaldisinp, this, null);
    }
    //总价打折
    public void doTotalDiscount(View source) {
        new AuthorizeDialog(0, this, null,
                new IAuthorizeCallBack() {
                    @Override
                    public void onConfirm(int requestCode, String uid, String uName) {
                        doTotalDiscount();
                    }
                });
    }

    //撤单
    public void doCd(View source) {
        if (!mBillHead.isEmpty() && !mBillProducts.isEmpty()) {
            askBox("确定撤销当前订单吗?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == -1) {
                        try {
                            showProgress("");
                            COrigBill.get().cancelBill(CConfig.HTTPEVENT_BILLCANCEL, CashmainActivity.this);
                        } catch (Exception ex) {
                            toast(ex.getMessage());
                        }
                    }
                }
            });
        }
    }

    //挂单
    public void doGd(View source) {
        if (!mBillHead.isEmpty() && !mBillProducts.isEmpty()) {
            askBox("确定挂起当前订单吗?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == -1) {
                        try {
                            showProgress("");
                            COrigBill.get().applyUpdate(CConfig.HTTPEVENT_BILLUPDATEGD, 1, CashmainActivity.this);
                        } catch (Exception ex) {
                            toast(ex.getMessage());
                        }
                    }
                }
            });
        }
    }

    private void toPay(int requestCode) {
        showProgress("");
        try {
            COrigBill.get().applyUpdate(requestCode, 0, CashmainActivity.this);
        } catch (Exception ex) {
            toast(ex.getMessage());
        }
    }

    private void doPay(int requestCode) {
        if (!mBillHead.isEmpty() && !mBillProducts.isEmpty()) {
            if (CGlobalData.salesid.equals("")) {
                new SalesLoginDialog(CConfig.CALLEVENT_SALESLOGIN, this, null,
                        new IAuthorizeCallBack() {
                            @Override
                            public void onConfirm(int requestCode, String uid, String uName) {
                                CGlobalData.salesid = uid;
                                CGlobalData.sales = uName;
                                setSalesInfo();
                                try {
                                    mBillHead.setString("salesid", uid);
                                    mBillHead.setString("sales", uName);
                                } catch (Exception ex) {

                                }
                                toPay(requestCode);
                            }
                        });
            } else {
                toPay(requestCode);
            }
        }
    }

    //款台收款
    public void doKtsk(View source) {
        doPay(CConfig.HTTPEVENT_BILLUPDATEKT);
    }

    //专柜收款
    public void doPay(View source) {
        doPay(CConfig.HTTPEVENT_BILLUPDATESY);
    }

    //列表按钮点击
    @Override
    public void onDaClick(View v) {
        switch (v.getId()) {
            case R.id.id_cash_goodlist_ivedit:   //修改商品
                doEditGoods(Integer.parseInt(v.getTag().toString()) + 1);
                break;
            case R.id.id_cash_goodlist_ivdel:    //删除商品
                msgBox("", "要删除制定的商品码？", new String[]{"确定", "取消"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == -1) {
                            mBillProducts.delete(Integer.parseInt(v.getTag().toString()) + 1);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void handleHttp(int requestCode, CJson json) {
        try {
            switch (requestCode) {
                case CConfig.HTTPEVENT_VIPQUERY:
                case CConfig.HTTPEVENT_VIPQUERY1:
                    if (json.getInt("recordcount") > 0) {
                        if (json.getIntEx("isyx") == 1) {
                            mBillHead.setString("viptype", json.getStringEx("typeid"));
                            mBillHead.setString("viptypename", json.getStringEx("typename"));
                            mBillHead.setString("vipid", json.getStringEx("sno"));
                        } else {
                            toast("会员卡已过期");
                        }
                        if (requestCode == CConfig.HTTPEVENT_VIPQUERY1 || json.getIntEx("isyx") == 0) {
                            doShowVipInfo(json.getStringEx("sno"));
                        }
                    } else {
                        toast("找不到会员信息");
                    }
                    break;
                case CConfig.CALLEVENT_CALERPDIS:  //计算erp折扣返回
                    try {
                        JSONArray goods = json.getJsonObj().getJSONArray("goods");
                        for (int i = 0;i<goods.length();i++){
                            JSONObject o = goods.getJSONObject(i);
                            mBillProducts.moveRecord(i+1);
                            mBillProducts.setDouble("popzk",o.getDouble("popzk"));
                            mBillProducts.setDouble("rulezk",o.getDouble("rulezk"));
                            mBillProducts.setDouble("vipdiscount",o.getDouble("custzk"));
                        }
                        mBillProducts.notifyMDataSet();
                    }catch (Exception ex){
                        toast(ex.getMessage());
                    }
                    break;
                case CConfig.HTTPEVENT_BILLUPDATE:
                    break;
                case CConfig.HTTPEVENT_BILLUPDATEGD:
                case CConfig.HTTPEVENT_BILLCANCEL:
                    if (requestCode == CConfig.HTTPEVENT_BILLUPDATEGD) {   //挂单完成处理
                        COrigBill.get().emptyDataSet();  //清空本地订单数据集
                        close(RESULT_OK);
                    }
                    break;
                case CConfig.HTTPEVENT_BILLUPDATEKT:  //到款台收款打印凭证
                    callActivity(this, CashReceiptPash.class, CConfig.CALLEVENT_CASHPASH);
                    break;
                case CConfig.HTTPEVENT_BILLUPDATESY:  //专柜收银
                    callActivity(this, PaymainActivity.class, CConfig.CALLEVENT_CASHCOUNTER);
                    break;
            }
        } catch (Exception ex) {

        }
    }

    @Override
    protected void handleEvent(int requestCode, Object data) {
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CConfig.HTTPEVENT_VIPQUERY:
            case CConfig.HTTPEVENT_VIPQUERY1:
                if (resultCode == RESULT_OK) {
                    queryVip(CConfig.HTTPEVENT_VIPQUERY, data.getStringExtra("code"));
                }
                break;
            case CConfig.CALLEVENT_EDITGOODS:    //增加修改商品返回
                mAdapter.notifyDataSetChanged();
                break;
            case CConfig.CALLEVENT_CASHPASH:     //款台收银打印返回
                if (resultCode == RESULT_OK) {
                    close(RESULT_OK);
                }
                break;
            case CConfig.CALLEVENT_CASHCOUNTER:  //支付返回
                if (resultCode == RESULT_OK) {
                    close(RESULT_OK);
                }
        }
    }

    public static class GoodsAdapter extends DatabeanAdapter<DataBean>
            implements AdapterView.OnItemClickListener {
        private int mSelectedPosition;
        private QueryActivity mQueryActivity;

        GoodsAdapter(Activity activity, List<DataBean> data, Bind<DataBean> bind, DACallback callback) {
            super(activity, data, bind, callback);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            return view;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }

    /**
     * 绑定数据
     */
    public static class GoodsBind extends DatabeanAdapter.Bind<DataBean> {
        @BindView(R.id.id_cash_goodlist_xsm)
        TextView mXsm;
        @BindView(R.id.id_cash_goodlist_pm)
        TextView mPm;
        @BindView(R.id.id_cash_goodlist_sl)
        TextView mSl;
        @BindView(R.id.id_cash_goodlist_dw)
        TextView mDw;
        @BindView(R.id.id_cash_goodlist_ysj)
        TextView mYsj;
        @BindView(R.id.id_cash_goodlist_zk)
        TextView mZk;

        @BindView(R.id.id_cash_goodlist_sj)
        TextView mSj;

        @BindView(R.id.id_cash_goodlist_ivedit)
        ImageView mEdit;
        @BindView(R.id.id_cash_goodlist_ivdel)
        ImageView mDel;

        @Override
        public void bindData(DatabeanAdapter parent, Context context, int position, View view, DataBean row) {
            try {
                mXsm.setText(row.getString("poscode"));
                mPm.setText(row.getString("posname"));
                mSl.setText(row.getString("quantity"));
                mDw.setText(row.getString("unit"));
                mYsj.setText(row.getString("iprice"));

                String hyzk = "", dpzk = "";
                double zkje = row.getDouble("vipdiscount");
                double zkl = row.getDouble("vipdiscountrate");
                int zkfs = row.getInt("vipdiscountatt");

                if (zkje != 0) {
                    hyzk = "会员 -" + zkje + ((zkfs == 0) ? "(" + zkl + "%)" : "");
                }

                zkje = row.getDouble("counterdiscount");
                zkl = row.getDouble("counterdiscountrate");
                zkfs = row.getInt("counterdiscountatt");
                if (zkje != 0) {
                    dpzk = "单品 -" + zkje + ((zkfs == 0) ? "(" + zkl + "%)" : "");
                }

                if (row.getInt("discountsort") == 0) {
                    dpzk = dpzk + " " + hyzk;
                } else {
                    dpzk = hyzk + " " + dpzk;
                }
                mZk.setText(dpzk.trim());
                mSj.setText(row.getString("price"));
                mEdit.setOnClickListener(parent);
                mEdit.setTag(position);
                mDel.setOnClickListener(parent);
                mDel.setTag(position);
            } catch (Exception ex) {

            }
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_cash_goodlist;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //联系信息输入
    public void doContact(View source) {
        new ContactDialog(CConfig.CALLEVENT_CONTACTINP, this, null, mBillHead.$("customer"), mBillHead.$("mtel"),
                new IAuthorizeCallBack() {
                    @Override
                    public void onConfirm(int requestCode, String uid, String uName) {
                        try {
                            mBillHead.setString("mtel", uName);
                            mBillHead.setString("customer", uid);
                        } catch (Exception ex) {
                        }
                    }
                });
    }
}
