package com.synics.gymp.trans;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.synics.gymp.R;
import com.synics.gymp.activity.QueryActivity;
import com.synics.gymp.adapter.CustomAdapter;

import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.COrigBill;
import com.synics.gymp.bluec.CPrint;
import com.synics.gymp.cash.BillinfoActivify;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

public class BillTransActivity extends QueryActivity implements CustomAdapter.CACallback {

    TextView tvrq1;
    TextView tvrq2;
    Spinner spywy;
    Spinner spzt;

    private OrderTransactionAdapter mAdapter;

    @Override
    protected void createConditionLayout() {
        tv_title.setText("订单办理");
        conditionView = LayoutInflater.from(this).inflate(R.layout.condition_billtrans, null, false);

        tvrq1 = (TextView) conditionView.findViewById(R.id.id_condition_billtrans_rq1);
        tvrq1.setText(CGlobalData.workdate);
        tvrq1.setOnClickListener(new DateClickListener());

        tvrq2 = (TextView) conditionView.findViewById(R.id.id_condition_billtrans_rq2);
        tvrq2.setOnClickListener(new DateClickListener());
        tvrq2.setText(CGlobalData.workdate);

        spywy = (Spinner) conditionView.findViewById(R.id.id_condition_billtrans_ywy);

        spywy.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                android.R.id.text1, CGlobalData.getSalesWithAll()));
        spzt = (Spinner) conditionView.findViewById(R.id.id_condition_billtrans_zt);
    }

    @Override
    protected void createAdapter() {
        mData = new ArrayList<>();
        mAdapter = new OrderTransactionAdapter(this, mData, new OrdersDailyBind(), this);
        lvlist.setAdapter(mAdapter);
        lvlist.setOnItemClickListener(mAdapter);
    }

    @Override
    protected void doQuery(View source) {
        CHttpUtil.Post("icommon/query.jsp", new String[]{
                "sqlid", "R_BILLLIST",
                "rq1", tvrq1.getText().toString(),
                "rq2", tvrq2.getText().toString(),
                "salesid", CGlobalData.getSalesIdByPosition(spywy.getSelectedItemPosition() - 1),
                "status", ""}, this, 0);
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

        @BindView(R.id.id_order_query_status)
        TextView mStatusTv;
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

        @BindView(R.id.id_order_query_bdxp)
        TextView mBdxpBt;
        @BindView(R.id.id_order_query_bdewm)
        TextView mBdewmBt;
        @BindView(R.id.id_order_query_info)
        TextView mInfoBt;
        @BindView(R.id.id_order_query_xxbl)
        TextView mXxblBt;

        @Override
        public void bindData(CustomAdapter parent, Context context, int position, View view, JSONObject json) {
            try {
                mItemPositionTv.setText(String.format(Locale.CHINA, "%d", position + 1));
                mOrderNoTv.setText(json.getString("billid"));
                mAmountTv.setText(String.format(Locale.CHINA, "%.1f", json.getDouble("amount")));
                mStatusTv.setText(json.getString("statename"));
                mCashierTv.setText(json.getString("cashier"));
                mSalesTv.setText(json.getString("sales"));
                mslTv.setText(json.getString("quantity"));
                mPayWayTv.setText((json.getInt("paidbycash") == 1) ? "款台支付" : "专柜支付");
                mDateTv.setText(json.getString("billdate"));

                String billkey = json.getString("billkey");
                mBdxpBt.setOnClickListener(parent);
                mBdxpBt.setTag(billkey);
                mBdewmBt.setOnClickListener(parent);
                mBdewmBt.setTag(billkey);
                mInfoBt.setOnClickListener(parent);
                mInfoBt.setTag(billkey);
                mXxblBt.setOnClickListener(parent);
                mXxblBt.setTag(billkey);
            } catch (Exception ex) {

            }
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_order_query;
        }
    }

    @Override
    protected void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
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

    protected void doResponse(int requestCode, CJson json) {
        try {
            switch (requestCode) {
                case CConfig.HTTPEVENT_PRINTBILL:  //补打小票
                    COrigBill.get().loadFromJsonObj(json.getJsonObj());
                    CPrint.printRecerpt(COrigBill.get());
                    break;
                case CConfig.HTTPEVENT_PRINTQBAR: //补打二维码
                    COrigBill.get().loadFromJsonObj(json.getJsonObj());
                    CPrint.printRecerptPash(COrigBill.get());
                    break;
            }
        } catch (Exception ex) {
            toast(ex.getMessage());
        }
    }

    @Override
    public void onDaClick(View v) {
        try {
            String billkey = v.getTag().toString();
            switch (v.getId()) {
                case R.id.id_order_query_bdxp:  //补打小票
                    showProgress("");
                    CHttpUtil.Post("iymp/billinfo.jsp", new String[]{
                            "billkey", billkey,  "isprint", "1"}, this, CConfig.HTTPEVENT_PRINTBILL);
                    break;
                case R.id.id_order_query_bdewm: //补打二维码
                    showProgress("");
                    CHttpUtil.Post("iymp/billinfo.jsp", new String[]{
                            "billkey", billkey }, this, CConfig.HTTPEVENT_PRINTQBAR);
                    break;
                case R.id.id_order_query_info:  //订单详情
                    callActivity(this, BillinfoActivify.class, new String[]{"billkey=" + billkey});
                    break;
                case R.id.id_order_query_xxbl:  //商品补录
                    callActivity(this, BillInfoInpActivify.class, new String[]{"billkey=" + billkey});
                    break;
            }
        } catch (Exception ex) {
            toast(ex.getMessage());
        }
    }
}
