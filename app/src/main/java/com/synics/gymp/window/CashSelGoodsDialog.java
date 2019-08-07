package com.synics.gymp.window;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CObjInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/7/6.
 */

public class CashSelGoodsDialog extends ConditionDialog {
    List<Map<String, Object>> listitem;
    SimpleAdapter adapter;
    ListView lvList;

    View.OnClickListener shortCutEvent = null;

    public CashSelGoodsDialog(int requestCode, int layoutId, BaseActivity parent, View topPanel) {
        super(requestCode, layoutId, parent, topPanel);
    }

    @Override
    protected void initData(View contentView, BaseActivity parent) {
        lvList = (ListView)contentView.findViewById(R.id.id_dialog_cashselgoods_list);
        listitem = new ArrayList<Map<String, Object>>();
        for(CObjInfo obj : CGlobalData.poscodelist){
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("sno", String.format("#%1s %s",obj.getShowName(),obj.getObjId()));
            item.put("sname", String.format("     %s",obj.getObjName()));
            listitem.add(item);
        }
        adapter = new SimpleAdapter
                (parent, listitem, R.layout.poscodeset_item,
                        new String[]{"sno", "sname"},
                        new int[]{R.id.id_poscodeset_item_code, R.id.id_poscodeset_item_name});
        lvList.setAdapter(adapter);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                parentView.conditionSelect(requestCode, true, view, position, listitem.get(position).get("sno").toString().substring(3));
            }
        });

        shortCutEvent = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n = CGlobalData.getCodePosition(((TextView)view).getText().toString());
                lvList.setSelection(n);
            }
        };

        ViewGroup vp = (ViewGroup)contentView.findViewById(R.id.id_dialog_cashselgoods_shortcuts);
        for (int i = 0; i < vp.getChildCount(); i++) {
            View viewchild = vp.getChildAt(i);
            viewchild.setOnClickListener(shortCutEvent);
        }
    }
}
