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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2019/7/1.
 */

public class LoginoutQuery extends QueryActivity {
    Spinner spSyy;
    TextView inpRq;

    protected void createConditionLayout() {
        tv_title.setText("签到签退日报");
        getViewByViewTag(getWindow().getDecorView(),"querybutton").setVisibility(View.GONE);

        View toolView = LayoutInflater.from(this).inflate(R.layout.tool_loginout_query, null, false);

        spSyy = (Spinner) toolView.findViewById(R.id.id_tool_loginout_query_syy);
        spSyy.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                android.R.id.text1, CGlobalData.getSalesWithAll()));
        spSyy.setSelection(0,true);
        spSyy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                execQuery(null);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        inpRq = (TextView) toolView.findViewById(R.id.id_tool_loginout_query_rq);
        inpRq.setText(CGlobalData.workdate);
        inpRq.setOnClickListener(new DateClickListener());

        topTool.addView(toolView);

    }
    @Override
    protected void createAdapter() {
        listitem = new ArrayList<Map<String, Object>>();
        adapter = new SimpleExAdapter(getApplicationContext(), listitem, R.layout.item_loginout_query,
                new String[]{"recno", "cashierid", "cashier", "lstyle", "lmode", "dtime"},
                new int[]{R.id.id_loginout_query_recno, R.id.id_loginout_query_dm, R.id.id_loginout_query_mc,
                        R.id.id_loginout_query_lx, R.id.id_loginout_query_fs,R.id.id_loginout_query_sj});
        lvlist.setAdapter(adapter);
        lvlist.setOnItemClickListener(onItemClickListener);
    }

    @Override
    protected void dateConditionChange(View view){
        execQuery(view);
    }

    @Override
    protected void doQuery(View source) {
        CHttpUtil.Post("icommon/query.jsp", new String[]{
                "sqlid", "R_LOGINQUERY",
                "rq", inpRq.getText().toString(),
                "cashierid", CGlobalData.getSalesIdByPosition(spSyy.getSelectedItemPosition()-1)}, this, 0);
    }

    @Override
    protected void doHandleData(CJson json) {
        try {
            jsonArrayToListMap(json.getJsonObj().getJSONArray("data"));
            tvTotalTxt.setText(String.format("共%d条记录",json.getJsonObj().getJSONArray("data").length()));
        } catch (Exception e) {

        }
    }
}
