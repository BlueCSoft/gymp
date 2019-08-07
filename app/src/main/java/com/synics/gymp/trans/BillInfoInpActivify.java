package com.synics.gymp.trans;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivityEx;
import com.synics.gymp.adapter.JsonObjectAdapter;
import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.iface.IAuthorizeCallBack;
import com.synics.gymp.window.GoodsInpDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2019/7/31.
 */

public class BillInfoInpActivify extends BaseActivityEx implements JsonObjectAdapter.ItemClickCallback {
    ListView lvGoodslist;
    GoodsAdapter mAdapter = null;
    List<JSONObject> mData = new ArrayList<>();
    String[] savevalues = null;
    @Override
    protected int getLayoutId() {
        return R.layout.activify_infoinp;
    }

    @Override
    protected void initViewAfter() {
        mAdapter = new GoodsAdapter(this, mData, new GoodsBind(), this);
        lvGoodslist = (ListView) findViewByTag("goodslist");
        lvGoodslist.setAdapter(mAdapter);
        lvGoodslist.setOnItemClickListener(mAdapter);

        showProgress("");
        Intent iParam = getIntent();
        CHttpUtil.Post("iymp/billinfo.jsp", new String[]{
                "billkey", iParam.getStringExtra("billkey"), "nissaved", "0"}, this, 0);
    }

    //列表按钮点击
    @Override
    public void onItemClick(View v) {
        try {
            int position = Integer.parseInt(v.getTag().toString());
            JSONObject row = mData.get(position);
            new GoodsInpDialog(CConfig.CALLEVENT_GOODSINP, this, null, row.getString("productcode"),
                    row.getString("productname"), row.getString("spec"), row.getString("nsize"),
                    row.getString("color"),
                    new IAuthorizeCallBack() {
                        @Override
                        public void onConfirm(int requestCode, String uid, String uName) {
                            try {
                                savevalues = uName.split("~");
                                showProgress("");
                                CHttpUtil.Post("icommon/update.jsp", new String[]{
                                        "sqlid","SET_BILL_GOODSINFO",
                                        "billkey", row.getString("billkey"), "seq", row.getString("seq"),
                                        "code", savevalues[0], "name", savevalues[1], "spec", savevalues[2], "nsize", savevalues[3],
                                        "color", savevalues[4],}, BillInfoInpActivify.this, position+100);

                            } catch (Exception ex) {
                            }
                        }
                    });
        } catch (Exception ex) {

        }
    }

    public static class GoodsAdapter extends JsonObjectAdapter<JSONObject>
            implements AdapterView.OnItemClickListener {
        private int mSelectedPosition;

        GoodsAdapter(Activity activity, List<JSONObject> data, Bind<JSONObject> bind, ItemClickCallback callback) {
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
    public static class GoodsBind extends JsonObjectAdapter.Bind<JSONObject> {
        @BindView(R.id.id_order_infoinp_poscode)
        TextView mPoscode;
        @BindView(R.id.id_order_infoinp_posname)
        TextView mPosname;
        @BindView(R.id.id_order_infoinp_productcode)
        TextView mHh;

        @BindView(R.id.id_order_infoinp_productname)
        TextView mPm;
        @BindView(R.id.id_order_infoinp_spec)
        TextView mGg;

        @BindView(R.id.id_order_infoinp_nsize)
        TextView mCm;
        @BindView(R.id.id_order_infoinp_color)
        TextView mYs;

        @BindView(R.id.id_order_infoinp_btedit)
        TextView mbtEdit;

        @Override
        public void bindData(JsonObjectAdapter parent, Context context, int position, View view, JSONObject row) {
            try {
                mPoscode.setText(row.getString("poscode"));
                mPosname.setText(row.getString("posname"));

                mHh.setText(row.getString("productcode"));
                mPm.setText(row.getString("productname"));

                mGg.setText(row.getString("spec"));
                mCm.setText(row.getString("nsize"));
                mYs.setText(row.getString("color"));

                mbtEdit.setOnClickListener(parent);
                mbtEdit.setTag(position);
            } catch (Exception ex) {

            }
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_order_infoinp;
        }
    }

    @Override
    protected void handleHttp(int requestCode, CJson json) {
        try {
            switch(requestCode) {
                case 0:
                    JSONObject head = json.getJsonObj().getJSONArray("head").getJSONObject(0);

                    setValueByViewTag(new String[]{"billid", "billdate"},
                            head);
                    mData.clear();
                    JSONArray rows = json.getJsonObj().getJSONArray("products");
                    for (int i = 0; i < rows.length(); i++) {
                        mData.add(rows.getJSONObject(i));
                    }
                    mAdapter.notifyDataSetChanged();

                    break;
                default:
                    JSONObject row = mData.get(requestCode-100);
                    row.put("productcode",savevalues[0]);
                    row.put("productname",savevalues[1]);
                    row.put("spec",savevalues[2]);
                    row.put("nsize",savevalues[3]);
                    row.put("color",savevalues[4]);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        } catch (Exception ex) {
            toastLong(ex.getMessage());
        }
    }
}
