package com.synics.gymp.trans;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.activity.QueryActivity;
import com.synics.gymp.adapter.CustomAdapter;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.CUtil;
import com.synics.gymp.cash.BillinfoActivify;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by Administrator on 2019/7/30.
 */

public class BillToErpActivity extends QueryActivity  implements CustomAdapter.CACallback{
    private OrderTransactionAdapter mAdapter;
    int mposition = 0;
    String mbillkey = "";
    String mbelongdate = "";
    @Override
    protected void createConditionLayout() {
        tv_title.setText("订单上传");
        mData = new ArrayList<>();
        mAdapter = new OrderTransactionAdapter(this, mData, new OrdersDailyBind(), this);
        lvlist.setAdapter(mAdapter);
        lvlist.setOnItemClickListener(mAdapter);
    }

    public static class OrderTransactionAdapter extends CustomAdapter<JSONObject>
            implements AdapterView.OnItemClickListener {

        OrderTransactionAdapter(QueryActivity queryActivity,
                                List<JSONObject> data, Bind<JSONObject> bind, CACallback mCallback) {
            super(queryActivity, data, bind, mCallback);
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
    public static class OrdersDailyBind extends CustomAdapter.Bind<JSONObject> {
        @BindView(R.id.id_order_query_recno)
        TextView mItemPositionTv;
        @BindView(R.id.id_order_query_billid)
        TextView mOrderNoTv;
        @BindView(R.id.id_order_query_pname)
        TextView mPName;

        @BindView(R.id.id_order_query_billtype)
        TextView mBilltypeTv;
        @BindView(R.id.id_order_query_sl)
        TextView mslTv;

        @BindView(R.id.id_order_query_pay)
        TextView mPayWayTv;
        @BindView(R.id.id_order_query_amount)
        TextView mAmountTv;

        @BindView(R.id.id_order_query_billdate)
        TextView mDateTv;
        @BindView(R.id.id_order_query_cashier)
        TextView mCashierTv;
        @BindView(R.id.id_order_query_sales)
        TextView mSalesTv;

        @BindView(R.id.id_order_toerp_info)
        TextView mInfoBt;
        @BindView(R.id.id_order_toerp_do)
        TextView mOkBt;

        @Override
        public void bindData(CustomAdapter parent, Context context, int position, View view, JSONObject json) {
            try {
                mItemPositionTv.setText(String.format(Locale.CHINA, "%d", position + 1));
                mOrderNoTv.setText(json.getString("billid"));
                mAmountTv.setText(String.format(Locale.CHINA, "%.1f", json.getDouble("amount")));
                mBilltypeTv.setText((json.getInt("billtype") == 1) ? "预售订单" : "现售订单");
                mCashierTv.setText(json.getString("cashier"));
                mSalesTv.setText(json.getString("sales"));
                mslTv.setText(json.getString("quantity"));
                mPayWayTv.setText((json.getInt("paidbycash") == 1) ? "款台支付" : "专柜支付");
                mDateTv.setText(json.getString("billdate"));

                String billkey = json.getString("billkey");
                mInfoBt.setOnClickListener(parent);
                mInfoBt.setTag(position);
                mOkBt.setOnClickListener(parent);
                mOkBt.setTag(position);
            } catch (Exception ex) {

            }
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_order_toerp;
        }
    }

    @Override
    protected void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    protected void dateConditionChange(View view) {
        showProgress("正在上传...");
        int k = mbelongdate.indexOf(" ");
        mbelongdate = selectDate+mbelongdate.substring(k);
        CHttpUtil.Post("ierp/sendbilltoerp.jsp", new String[]{"billkey", mbillkey,"belongdate",mbelongdate}, this, 1);
    }

    @Override
    public void onDaClick(View v) {
        try {
            mposition = Integer.parseInt(v.getTag().toString());
            mbillkey = mData.get(mposition).getString("billkey");
            mbelongdate = mData.get(mposition).getString("billdate");
            switch (v.getId()) {
                case R.id.id_order_toerp_info:  //订单详情
                    callActivity(this, BillinfoActivify.class, new String[]{"billkey=" + mbillkey});
                    break;
                case R.id.id_order_toerp_do:    //订单上传
                    if(mData.get(mposition).getInt("billtype")==1){  //预售
                        int k = mbelongdate.indexOf(" ");
                        openDateDialog(null,"请选择交货日期",mbelongdate.substring(0,k));
                    }else {
                        showProgress("正在上传...");                   //现售
                        CHttpUtil.Post("ierp/sendbilltoerp.jsp", new String[]{"billkey", mbillkey}, this, 1);
                    }
                    break;
            }
        } catch (Exception ex) {
            toast(ex.getMessage());
        }
    }

    @Override
    protected void doQuery(View source) {
        CHttpUtil.Post("icommon/query.jsp", new String[]{
                "sqlid", "R_BILLTOERP"}, this, 0);
    }

    @Override
    protected void doHandleData(CJson json) {
        try {
            jsonArrayToList(json.getJsonObj().getJSONArray("data"));
            int n = json.getJsonObj().getInt("recordcount");
            tvTotalTxt.setText((n == 0) ? "没有满足条件的订单" : String.format("总共%d张订单", n));
        } catch (Exception e) {

        }
    }

    @Override
    protected void doResponse(int requestCode , CJson json) {
        toast("订单成功上传");
        doQuery(null);
    }
    @Override
    public void openCondition(View source) {
        doQuery(source);
    }
}
