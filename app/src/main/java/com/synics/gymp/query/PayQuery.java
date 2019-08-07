package com.synics.gymp.query;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.synics.gymp.R;
import com.synics.gymp.activity.QueryActivity;
import com.synics.gymp.adapter.SimpleExAdapter;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;

import java.util.ArrayList;
import java.util.Map;

import butterknife.OnClick;


/**
 * Created by Administrator on 2019/6/30.
 */

public class PayQuery extends QueryActivity implements View.OnClickListener{
    EditText etInp;
    ImageView ivInpCr;
    ImageView ivScan;
    Spinner spZffs;
    protected void createConditionLayout() {
        tv_title.setText("支付查询");
        getViewByViewTag(getWindow().getDecorView(),"querybutton").setVisibility(View.GONE);

        View toolView = LayoutInflater.from(this).inflate(R.layout.tool_pay_query, null, false);

        etInp = (EditText)toolView.findViewById(R.id.id_tool_pay_query_inp);
        ivInpCr = (ImageView)toolView.findViewById(R.id.id_tool_pay_query_cr);
        ivScan = (ImageView)toolView.findViewById(R.id.id_tool_pay_query_scan);
        spZffs = (Spinner) toolView.findViewById(R.id.id_tool_pay_query_zffs);
        topTool.addView(toolView);

        ivInpCr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etInp.getText().toString().trim().length()<7){
                    toast("请输入订单号或付款卡号(至少后7位)");
                }else{
                    execQuery(null);
                }
            }
        });
    }

    @Override
    protected void createAdapter() {
        listitem = new ArrayList<Map<String, Object>>();
        adapter = new SimpleExAdapter(getApplicationContext(), listitem, R.layout.item_pay_query,
                new String[]{"recno", "billid", "paymentname", "amount", "paymenttime"},
                new int[]{R.id.id_billdayreport_recno, R.id.id_billdayreport_billid, R.id.id_payquery_zffs,
                        R.id.id_payquery_zfje, R.id.id_payquery_zfsj});
        lvlist.setAdapter(adapter);
    }

    @Override
    protected void doQuery(View source) {
        CHttpUtil.Post("icommon/query.jsp", new String[]{
                "sqlid", "R_PAYLIST",
                "payno", "",
                "tno", etInp.getText().toString().trim()}, this, 0);
    }

    @Override
    protected void doHandleData(CJson json) {
        try {
            jsonArrayToListMap(json.getJsonObj().getJSONArray("data"));
            tvTotalTxt.setText(String.format("共%d笔支付数据",json.getJsonObj().getJSONArray("data").length()));
        } catch (Exception e) {

        }
    }

    @Nullable
    @OnClick({})
    public void onClick(View view) {
    }
}
