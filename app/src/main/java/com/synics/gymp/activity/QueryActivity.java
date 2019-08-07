package com.synics.gymp.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.adapter.SimpleExAdapter;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.CUtil;
import com.synics.gymp.iface.IConditionSelect;
import com.synics.gymp.iface.IDataLoadCallback;
import com.synics.gymp.window.ConditionDialog;
import com.synics.gymp.window.ConditionWindow;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

public class QueryActivity extends BaseActivity implements IDataLoadCallback, IConditionSelect {

    @BindView(R.id.id_common_query_body)
    protected LinearLayout lvbody;

    @BindView(R.id.id_common_query_none)
    protected LinearLayout lvnone;

    @BindView(R.id.id_common_query_title)
    public TextView tv_title;

    @BindView(R.id.id_common_query_header)
    protected RelativeLayout topPanel;

    @BindView(R.id.id_common_query_tool)
    protected LinearLayout topTool;

    @BindView(R.id.id_common_query_list)
    protected ListView lvlist;

    @BindView(R.id.id_common_query_total)
    protected LinearLayout tvTotal;

    @BindView(R.id.id_common_query_total_txt)
    protected TextView tvTotalTxt;

    @BindView(R.id.id_common_query_bottompanel)
    protected LinearLayout lvBottoPanel;

    protected String[] queryParams = null;        //  查询条件

    protected List<Map<String, Object>> listitem;  //  列表结果数据
    protected List<JSONObject> mData;              //  JSON列表数据
    protected Map<String, Object> msumData;        //  数字字段合计数据
    protected String[] sumfields = null;          //  合计字段

    protected SimpleExAdapter adapter;               //  默认适配器
    protected View conditionView = null;          //  条件选择输入视图

    protected TextView viewForSelectDate = null;
    protected AdapterView.OnItemClickListener onItemClickListener = null;
    protected String selectDate = "";

    @Override
    protected void initStart() {
        onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QueryActivity.this.onItemClick(view, position);
                adapter.setSelected(position, view);
            }
        };
    }

    protected void dateConditionChange(View view) {
    }

    protected void openDateDialog(TextView source,String title, String minDate) {
        viewForSelectDate = source;
        Calendar cale1 = Calendar.getInstance();
        selectDate = (source == null || source.getText().toString().equals("")) ? CUtil.getOrigDate() : source.getText().toString();
        String[] ds = selectDate.toString().split("-");
        cale1.set(Integer.parseInt(ds[0]), Integer.parseInt(ds[1]) - 1, Integer.parseInt(ds[2]));

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                selectDate = String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth);
                if (viewForSelectDate != null) {
                    viewForSelectDate.setText(selectDate);
                }
                dateConditionChange(viewForSelectDate);
            }
        }, cale1.get(Calendar.YEAR), cale1.get(Calendar.MONTH), cale1.get(Calendar.DAY_OF_MONTH));
        if (!minDate.equals("")) {
            ds = minDate.toString().split("-");
            cale1.set(Integer.parseInt(ds[0]), Integer.parseInt(ds[1]) - 1, Integer.parseInt(ds[2]));
            datePickerDialog.getDatePicker().setMinDate(cale1.getTime().getTime());
        }
        if(!title.equals("")) {
            datePickerDialog.setTitle(title);
        }else{
            datePickerDialog.setTitle("选择日期");
        }
        datePickerDialog.show();
    }

    protected void openDateDialog(TextView source,String title) {
        this.openDateDialog(source, title, "");
    }

    protected void openDateDialog(TextView source) {
        this.openDateDialog(source, "", "");
    }

    public class DateClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            openDateDialog((TextView) v);
        }
    }

    protected int getLayoutId() {
        return R.layout.activity_query;
    }

    /*创建条件选择视图*/
    protected void createConditionLayout() {
        //conditionView = LayoutInflater.from(getApplicationContext()).inflate(0, null, false);
    }

    /*创建适配器*/
    protected void createAdapter() {
        //listitem = new ArrayList<Map<String, Object>>();
        //lvlist.setAdapter(adapter);
    }

    protected String getSum(String fieldName, String fmt, Boolean __zeroToSpace) {
        return msumData.containsKey(fieldName) ?
                CUtil.formatNumber(Double.parseDouble(msumData.get(fieldName).toString()), fmt, __zeroToSpace) :
                ((__zeroToSpace) ? "" : "0");
    }

    protected String getSum(String fieldName, String fmt) {
        return getSum(fieldName, fmt, false);
    }

    protected String getSum(String fieldName) {
        return getSum(fieldName, "", false);
    }

    /*数据设置*/
    protected void jsonArrayToList(JSONArray data) {
        try {
            mData.clear();

            Boolean hasSum = sumfields != null;
            double[] sums = null;
            if (hasSum) {
                msumData.clear();
                sums = new double[sumfields.length];
                for (int k = 0; k < sumfields.length; k++) {
                    sums[k] = 0;
                }
            }

            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = data.getJSONObject(i);
                mData.add(obj);
                if (hasSum) {
                    for (int j = 0; j < sumfields.length; j++) {
                        sums[j] += ((obj.has(sumfields[j]) && !obj.getString(sumfields[j]).equals("")) ? obj.getDouble(sumfields[j]) : 0);
                    }
                }
            }

            if (hasSum) {
                for (int k = 0; k < sumfields.length; k++) {
                    msumData.put(sumfields[k], sums[k]);
                }
            }
        } catch (Exception e) {

        }
    }

    protected void jsonArrayToListMap(JSONArray data) {
        try {
            listitem.clear();

            Boolean hasSum = sumfields != null;
            double[] sums = null;
            if (hasSum) {
                msumData.clear();
                sums = new double[sumfields.length];
                for (int k = 0; k < sumfields.length; k++) {
                    sums[k] = 0;
                }
            }

            for (int i = 0; i < data.length(); i++) {
                Map<String, Object> item = new HashMap<String, Object>();
                JSONObject obj = data.getJSONObject(i);
                Iterator<?> it = obj.keys();

                while (it.hasNext()) {//遍历JSONObject
                    String key = (String) it.next().toString();
                    item.put(key, obj.get(key));
                }
                listitem.add(item);
                if (hasSum) {
                    for (int j = 0; j < sumfields.length; j++) {
                        sums[j] += ((obj.has(sumfields[j]) && !obj.getString(sumfields[j]).equals("")) ? obj.getDouble(sumfields[j]) : 0);
                    }
                }
            }

            if (hasSum) {
                for (int k = 0; k < sumfields.length; k++) {
                    msumData.put(sumfields[k], sums[k]);
                }
            }
        } catch (Exception e) {

        }
    }

    /*打开查询条件窗口*/
    public void openCondition(View source) {
        if (conditionView != null) {
            new ConditionDialog(conditionView, this, null);
            //new ConditionWindow(conditionView, this, topPanel);
        }
    }

    /*执行查询*/
    protected void execQuery(View source) {
        showProgress("");
        doQuery(source);
    }

    protected void doQuery(View source) {
        CHttpUtil.Post("icommon/query.jsp", queryParams, this, 0);
    }

    @Override
    protected void initView() {
        msumData = new HashMap<>();
        createConditionLayout();
        createAdapter();
        doQuery(null);
    }

    // 选中某项
    public void onItemClick(View view, int position) {
    }

    protected void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    protected void doHandleData(CJson json) {
    }

    private void handleData(CJson json) {
        doHandleData(json);
        try {
            if (json.getJsonObj().getJSONArray("data").length() == 0) {
                lvbody.setVisibility(View.GONE);
                lvnone.setVisibility(View.VISIBLE);
            } else {
                lvbody.setVisibility(View.VISIBLE);
                lvnone.setVisibility(View.GONE);
            }
        } catch (Exception ex) {

        }
        notifyDataSetChanged();
    }

    protected void doResponse(int requestCode, CJson json) {
    }

    protected void setQueryParams() {
    }

    @Override
    public void conditionSelect(int requestCode, Boolean isConfirm, View source, int position, String value) {
        setQueryParams();
        execQuery(null);
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
                    if (requestCode == 0) {
                        handleData(json);
                    } else {
                        doResponse(requestCode, json);
                    }
                } else {
                    toast(json.getErrorMsg());
                }
            }
        } catch (Exception ex) {
            toastLong(ex.getMessage());
        }
    }
}
