package com.synics.gymp.cash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivityEx;
import com.synics.gymp.adapter.JsonObjectAdapter;
import com.synics.gymp.bluec.CBillProducts;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.COrigBill;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by Administrator on 2019/7/27.
 */

public class BillinfoActivify extends BaseActivityEx {
    LinearLayout llGoodslist;
    LinearLayout llPayslist;

    @Override
    protected int getLayoutId() {
        return R.layout.activify_billinfo;
    }

    @Override
    protected void initViewAfter() {
        llGoodslist = (LinearLayout) findViewByTag("goodslist");
        llPayslist = (LinearLayout) findViewByTag("payslist");

        showProgress("读取订单...");
        Intent iParam = getIntent();
        CHttpUtil.Post("iymp/billinfoex.jsp", new String[]{
                "billkey", iParam.getStringExtra("billkey")}, this, 0);
    }

    @Override
    protected void handleHttp(int requestCode, CJson json) {
        try {
            COrigBill.get().loadFromJsonObj(json.getJsonObj());
            JSONObject head = json.getJsonObj().getJSONArray("head").getJSONObject(0);

            head.put("billstate", CGlobalData.getBillStateName(head.getString("billstate")));

            setValueByViewTag(new String[]{"billid", "billdate", "counterid", "cashier", "sales", "vipinfo", "customerinfo", "billstate",
                            "quantity", "totalamount", "vipdiscount", "counterdiscount", "popzk", "rulezk", "totaldiscount", "sumdiscount", "amount"},
                    head);

            COrigBill.get().getMbillProducts().setGoodsViewList(this, llGoodslist);
            COrigBill.get().getMbillPayment().setPayViewList(this, llPayslist);
        } catch (Exception ex) {
            toastLong(ex.getMessage());
        }
    }

}
