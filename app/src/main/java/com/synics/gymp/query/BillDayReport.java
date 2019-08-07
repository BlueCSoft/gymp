package com.synics.gymp.query;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.activity.QueryActivity;
import com.synics.gymp.adapter.SimpleExAdapter;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2019/6/30.
 */

public class BillDayReport extends QueryActivity{
    TextView tvrq1;
    TextView tvrq2;
    Spinner spywy;
    Spinner spxslx;

    TextView tvSumSl;
    TextView tvSumJe;
    TextView tvSumZk;
    TextView tvSumSs;

    protected void createConditionLayout() {
        tv_title.setText("订单日报");
        conditionView = LayoutInflater.from(this).inflate(R.layout.condition_selldayreport, null, false);

        //tvTotalTxt.setVisibility(View.GONE);
        View sumView = LayoutInflater.from(this).inflate(R.layout.item_bill_dayreportsum, null, false);
        tvSumSl = (TextView)sumView.findViewById(R.id.id_billdayreport_sum_sl);
        tvSumJe = (TextView)sumView.findViewById(R.id.id_billdayreport_sum_je);
        tvSumZk = (TextView)sumView.findViewById(R.id.id_billdayreport_sum_zk);
        tvSumSs = (TextView)sumView.findViewById(R.id.id_billdayreport_sum_ss);
        tvTotal.removeView(tvTotalTxt);
        tvTotal.addView(sumView,0);

        tvrq1 = (TextView) conditionView.findViewById(R.id.id_condition_selldayreport_rq1);
        tvrq1.setText(CGlobalData.workdate);
        tvrq1.setOnClickListener(new DateClickListener());

        tvrq2 = (TextView) conditionView.findViewById(R.id.id_condition_selldayreport_rq2);
        tvrq2.setOnClickListener(new DateClickListener());
        tvrq2.setText(CGlobalData.workdate);

        spywy = (Spinner) conditionView.findViewById(R.id.id_condition_selldayreport_ywy);

        spywy.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                android.R.id.text1, CGlobalData.getSalesWithAll()));
        spxslx = (Spinner) conditionView.findViewById(R.id.id_condition_selldayreport_xslx);
    }

    @Override
    protected void createAdapter() {
        listitem = new ArrayList<Map<String, Object>>();
        adapter = new SimpleExAdapter(getApplicationContext(), listitem, R.layout.item_bill_dayreport,
                new String[]{"recno", "billid", "productname", "statename", "billtype", "paidbycash",
                        "amount", "billdate", "cashier", "sales"},
                new int[]{R.id.id_billdayreport_recno, R.id.id_billdayreport_billid, R.id.id_billdayreport_pname,
                        R.id.id_billdayreport_status, R.id.id_billdayreport_billtype, R.id.id_billdayreport_pay,
                        R.id.id_billdayreport_amount, R.id.id_billdayreport_billdate, R.id.id_billdayreport_cashier,
                        R.id.id_billdayreport_sales});
        lvlist.setAdapter(adapter);
        lvlist.setOnItemClickListener(onItemClickListener);
        sumfields = new String[]{"total", "discount", "amount"};
    }

    @Override
    protected void doQuery(View source) {
        CHttpUtil.Post("icommon/query.jsp", new String[]{
                "sqlid", "R_BILLREPORT",
                "rq1", tvrq1.getText().toString(),
                "rq2", tvrq2.getText().toString(),
                "salesid", CGlobalData.getSalesIdByPosition(spywy.getSelectedItemPosition() - 1),
                "xslx", ""}, this, 0);
    }

    @Override
    protected void doHandleData(CJson json) {
        try {
            jsonArrayToListMap(json.getJsonObj().getJSONArray("data"));
            tvSumSl.setText(listitem.size()+"");
            tvSumJe.setText(getSum("total"));
            tvSumZk.setText(getSum("discount"));
            tvSumSs.setText(getSum("amount"));
        } catch (Exception e) {

        }
    }
}
