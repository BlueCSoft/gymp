package com.synics.gymp.setting;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.iface.IDataLoadCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class PoscodesetActivity extends BaseActivity implements IDataLoadCallback {

    @BindView(R.id.id_set_poscode_exit)
    ImageView tv_poscode_exit;

    @BindView(R.id.id_set_poscode_csel)
    TextView tv_poscode_csel;
    @BindView(R.id.id_set_poscode_sels)
    TextView tv_poscode_sels;

    @BindView(R.id.id_set_poscode_lvleft)
    ListView lvleft;
    @BindView(R.id.id_set_poscode_lvright)
    ListView lvright;

    List<Map<String, Object>> listitem_l;
    SimpleAdapter adapter_l;
    List<Map<String, Object>> listitem_r;
    SimpleAdapter adapter_r;

    protected int getLayoutId() {
        return R.layout.activity_poscodeset;
    }

    @Override
    protected void initView() {
        tv_poscode_exit.setOnClickListener(closeEvent);

        listitem_l = new ArrayList<Map<String, Object>>();
        listitem_r = new ArrayList<Map<String, Object>>();

        adapter_l = new SimpleAdapter
                (this, listitem_l, R.layout.poscodeset_item,
                        new String[]{"sno", "sname"},
                        new int[]{R.id.id_poscodeset_item_code, R.id.id_poscodeset_item_name});
        adapter_r = new SimpleAdapter
                (this, listitem_r, R.layout.poscodeset_item,
                        new String[]{"sno", "sname"},
                        new int[]{R.id.id_poscodeset_item_code, R.id.id_poscodeset_item_name});

        lvleft.setAdapter(adapter_l);
        lvright.setAdapter(adapter_r);

        lvleft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                listitem_r.add(listitem_l.get(position));
                listitem_l.remove(position);
                updateStatus();
            }
        });
        lvright.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                listitem_l.add(listitem_r.get(position));
                listitem_r.remove(position);
                updateStatus();
            }
        });

    }

    private void updateStatus(){
        adapter_l.notifyDataSetChanged();
        adapter_r.notifyDataSetChanged();
        tv_poscode_csel.setText(listitem_l.size()+"");
        tv_poscode_sels.setText(listitem_r.size()+"");
    }
    @Override
    public void onStart() {
        super.onStart();
        showProgress("");
        CHttpUtil.Post("icommon/query.jsp", new String[]{"sqlid", "GET_COUNTER_PCODE"}, this, CConfig.HTTPEVENT_DEFAULT);
    }

    public void dataLoadCallback(boolean isHttpCall, int requestCode, int responseCode, Object data) {
        hideProgress();
        if (responseCode < 0) {
            toastLong(data.toString());
            return;
        }
        try {
            if (isHttpCall) {
                CJson json = new CJson();
                if (json.toJsonObject(data.toString())) {
                    listitem_l.clear();
                    listitem_r.clear();
                    JSONArray poscodes = json.getJsonObj().getJSONArray("data");
                    for (int i = 0; i < poscodes.length(); i++) {
                        JSONObject row = poscodes.getJSONObject(i);
                        Map<String, Object> item = new HashMap<String, Object>();
                        item.put("sno", row.getString("sno"));
                        item.put("sname", row.getString("sname"));
                        listitem_l.add(item);
                    }
                    updateStatus();
                }
            }
        } catch (Exception ex) {
            toastLong(ex.getMessage());
        }
    }
}
