package com.synics.gymp.cash;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.activity.QueryActivity;
import com.synics.gymp.adapter.SimpleExAdapter;
import com.synics.gymp.bluec.CBill;
import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.COrigBill;

import java.util.ArrayList;
import java.util.Map;

public class NofinishiBillActivity extends QueryActivity {

    @Override
    protected void createConditionLayout() {
        tv_title.setText("挂起订单列表");
        getViewByViewTag(getWindow().getDecorView(), "querybutton").setVisibility(View.GONE);
        tvTotal.setVisibility(View.GONE);

        View toolView = LayoutInflater.from(this).inflate(R.layout.tool_button_okcancel, null, false);
        lvBottoPanel.addView(toolView);

        ((TextView) toolView.findViewWithTag("confirm")).setText("取单");
        ((TextView) toolView.findViewWithTag("cancel")).setText("撤单");
    }

    @Override
    protected void createAdapter() {
        listitem = new ArrayList<Map<String, Object>>();
        adapter = new SimpleExAdapter(getApplicationContext(), listitem, R.layout.item_nofinishi_bill,
                new String[]{"recno", "billid", "vipid", "totalamount", "billdate", "statename","payatt"},
                new int[]{R.id.id_item_nofinishi_recno, R.id.id_item_nofinishi_billid, R.id.id_item_nofinishi_vipid,
                        R.id.id_item_nofinishi_totalamount, R.id.id_item_nofinishi_billdate, R.id.id_item_nofinishi_billstate,
                R.id.id_item_nofinishi_payatt});
        lvlist.setAdapter(adapter);
        lvlist.setOnItemClickListener(onItemClickListener);
    }

    @Override
    protected void doQuery(View source) {
        CHttpUtil.Post("icommon/query.jsp", new String[]{
                "sqlid", "R_NOFINISHIBILL", "rq", CGlobalData.workdate}, this, 0);
    }

    @Override
    protected void doHandleData(CJson json) {
        try {
            jsonArrayToListMap(json.getJsonObj().getJSONArray("data"));
            tvTotalTxt.setText(String.format("共%d条记录", json.getJsonObj().getJSONArray("data").length()));
        } catch (Exception e) {

        }
    }

    @Override
    protected void doResponse(int requestCode, CJson json) {
        switch (requestCode) {
            case CConfig.HTTPEVENT_BILLCANCEL: //撤单成功
                close(CConfig.RETURNEVENT_BILLCANCEL);
                break;
            case CConfig.HTTPEVENT_BILLINFO:   //读入单据成功
                COrigBill.get().loadFromJsonObj(json.getJsonObj());
                close(CConfig.RETURNEVENT_TONOFINISHI);
                break;
        }
    }

    //取单
    public void doComfirm(View source) {
        if(!adapter.isEmpty()) {
            int i = adapter.getSelectedPosition();
            if (i >= 0) {
                Map<String, Object> row = listitem.get(i);
                int state = Integer.parseInt(row.get("billstate").toString());
                String billkey = row.get("billkey").toString();
                showProgress("读取订单...");
                CHttpUtil.Post("iymp/billinfo.jsp", new String[]{
                        "billkey", billkey, "nissaved", "1"}, this, CConfig.HTTPEVENT_BILLINFO);
            }
        }
    }

    //撤单
    public void doCancel(View source) {
        if(!adapter.isEmpty()) {
            int i = adapter.getSelectedPosition();
            if (i >= 0) {
                Map<String, Object> row = listitem.get(i);
                int state = Integer.parseInt(row.get("billstate").toString());
                String billkey = row.get("billkey").toString();
                String billid = row.get("billid").toString();
                askBox("确定撤销当前订单吗?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == -1) {
                            try {
                                showProgress("");
                                CHttpUtil.Post("iymp/billundo.jsp", new String[]{
                                        "billkey", billkey, "billid", billid
                                }, NofinishiBillActivity.this, CConfig.HTTPEVENT_BILLCANCEL);
                            } catch (Exception ex) {
                                toast(ex.getMessage());
                            }
                        }
                    }
                });
            }
        }
    }
}
