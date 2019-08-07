package com.synics.gymp.query;

/**
 * Created by Administrator on 2019/6/30.
 */

import android.view.LayoutInflater;
import android.view.View;
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
import com.synics.gymp.trans.BillTransActivity;

import java.util.ArrayList;
import java.util.Map;

public class SellDayReport extends QueryActivity {

    TextView tvrq1;
    TextView tvrq2;
    Spinner spywy;
    Spinner spxslx;

    TextView tvSumSl;
    TextView tvSumJe;
    TextView tvSumZk;
    TextView tvSumSs;

    @Override
    protected void createConditionLayout() {
        tv_title.setText("销售日报");
        conditionView = LayoutInflater.from(this).inflate(R.layout.condition_selldayreport, null, false);

        View sumView = LayoutInflater.from(this).inflate(R.layout.item_sell_dayreportsum, null, false);
        tvSumSl = (TextView)sumView.findViewById(R.id.id_selldayreport_sum_sl);
        tvSumJe = (TextView)sumView.findViewById(R.id.id_selldayreport_sum_je);
        tvSumZk = (TextView)sumView.findViewById(R.id.id_selldayreport_sum_zk);
        tvSumSs = (TextView)sumView.findViewById(R.id.id_selldayreport_sum_ss);
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
        adapter = new SimpleExAdapter(getApplicationContext(), listitem, R.layout.item_sell_dayreport,
                new String[]{"recno", "productcode", "productname", "billdate", "bs", "quantity",
                        "total", "discount", "amount", "price"},
                new int[]{R.id.id_selldayreport_recno, R.id.id_selldayreport_pm, R.id.id_selldayreport_dm,
                        R.id.id_selldayreport_rq, R.id.id_selldayreport_bs, R.id.id_selldayreport_sl,
                        R.id.id_selldayreport_je, R.id.id_selldayreport_zk, R.id.id_selldayreport_ys,
                        R.id.id_selldayreport_dj});
        lvlist.setAdapter(adapter);
        sumfields = new String[]{"bs", "quantity", "total", "discount", "amount"};
    }

    @Override
    protected void doQuery(View source) {
        CHttpUtil.Post("icommon/query.jsp", new String[]{
                "sqlid", "R_SALEREPORT",
                "rq1", tvrq1.getText().toString(),
                "rq2", tvrq2.getText().toString(),
                "salesid", CGlobalData.getSalesIdByPosition(spywy.getSelectedItemPosition() - 1),
                "xslx", ""}, this, 0);
    }

    @Override
    protected void doHandleData(CJson json) {
        try {
            jsonArrayToListMap(json.getJsonObj().getJSONArray("data"));
            tvSumSl.setText(getSum("quantity"));
            tvSumJe.setText(getSum("total"));
            tvSumZk.setText(getSum("discount"));
            tvSumSs.setText(getSum("amount"));
        } catch (Exception e) {

        }
    }
}
