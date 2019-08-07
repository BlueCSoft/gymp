package com.synics.gymp.cash;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivityEx;
import com.synics.gymp.adapter.DatabeanAdapter;
import com.synics.gymp.bluec.CBillHead;
import com.synics.gymp.bluec.CBillProducts;
import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.bluec.CDataSource;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.COrigBill;
import com.synics.gymp.bluec.CPrint;
import com.synics.gymp.bluec.DataBean;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2019/7/10.
 */

public class CashReceiptPash extends BaseActivityEx implements DatabeanAdapter.DACallback {

    ListView lvGoodslist;

    private CBillHead mBillHead = COrigBill.get().getMbillHead();
    private CBillProducts mBillProducts = COrigBill.get().getMbillProducts();
    private CDataSource dsBillHead = new CDataSource();
    private CDataSource dsBillProducts = new CDataSource();

    CashReceiptPash.GoodsAdapter mAdapter = null;

    @Override
    protected int getLayoutId() {
        return R.layout.activify_receiptpash;
    }

    @Override
    protected void initViewAfter() {
        dsBillHead.setOnSetFieldText(new CDataSource.OnSetFieldText() {
            @Override
            public String onSetText(int viewId, String fieldName, String defaultValue) {
                try {
                    if (fieldName.equals("viptype")&&!mBillHead.getString("viptype").equals("")) {
                        defaultValue = CGlobalData.getViptypeName(mBillHead.getString("viptype"));
                    }
                    if (fieldName.equals("billdate")) {
                        defaultValue = defaultValue.substring(0, 10);
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

        mAdapter = new CashReceiptPash.GoodsAdapter(this, mBillProducts.getRows(), new CashReceiptPash.GoodsBind(), this);

        lvGoodslist = (ListView) findViewByTag("goodslist");
        lvGoodslist.setAdapter(mAdapter);
        lvGoodslist.setOnItemClickListener(mAdapter);
    }

    @Override
    protected void onBeforeDestroy() {
        mBillHead.removeDataSource(dsBillHead);
        mBillProducts.removeDataSource(dsBillProducts);
    }

    //列表按钮点击
    @Override
    public void onDaClick(View v) {

    }

    public static class GoodsAdapter extends DatabeanAdapter<DataBean>
            implements AdapterView.OnItemClickListener {
        private int mSelectedPosition;

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

        @BindView(R.id.id_cash_goodlist_ysj)
        TextView mYsj;
        @BindView(R.id.id_cash_goodlist_zk)
        TextView mZk;

        @BindView(R.id.id_cash_goodlist_sj)
        TextView mSj;

        @Override
        public void bindData(DatabeanAdapter parent, Context context, int position, View view, DataBean row) {
            try {
                mXsm.setText(row.getString("poscode"));
                mPm.setText(row.getString("posname"));
                mSl.setText(row.getString("quantity"));
                mYsj.setText(row.getString("iprice"));

                String hyzk = "",dpzk = "";
                double zkje = row.getDouble("vipdiscount");
                double zkl = row.getDouble("vipdiscountrate");
                int zkfs = row.getInt("vipdiscountatt");

                if(zkje!=0){
                    hyzk = "会员"+zkje+"("+((zkfs==0)? zkl+"%":"-"+zkl)+")";
                }

                zkje = row.getDouble("counterdiscount");
                zkl = row.getDouble("counterdiscountrate");
                zkfs = row.getInt("counterdiscountatt");
                if(zkje!=0){
                    dpzk = "单品"+zkje+"("+((zkfs==0)? zkl+"%":"-"+zkl)+")";
                }

                if(row.getInt("discountsort")==0){
                    dpzk = dpzk+" "+hyzk;
                }else{
                    dpzk = hyzk+" "+dpzk;
                }
                mZk.setText(dpzk.trim());
                mSj.setText(row.getString("price"));
            } catch (Exception ex) {

            }
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_cash_receipt;
        }
    }

    public void doPrint(View source){
        try {
            showProgress("");
            CHttpUtil.Post("iymp/billpashpay.jsp", new String[]{
                    "billkey", mBillHead.getString("billkey"), "billid", mBillHead.getString("billid")
            }, this, CConfig.HTTPEVENT_CASHPASH);
        }catch (Exception ex){
            toastLong(ex.getMessage());
        }
    }

    @Override
    protected void handleHttp(int requestCode, CJson json) {
        try {
            switch (requestCode) {
                case CConfig.HTTPEVENT_CASHPASH:
                    CPrint.printRecerptPash(COrigBill.get());
                    successBox("提示", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==-1) {
                                close(RESULT_OK);
                            }
                        }
                    });
                    break;
            }
        } catch (Exception ex) {
            toastLong(ex.getMessage());
        }
    }
}
