package com.synics.gymp.setting;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.iface.IDataLoadCallback;

import butterknife.BindView;

public class ParamsetActivity extends BaseActivity  implements IDataLoadCallback {

    @BindView(R.id.id_set_param_exit)
    ImageView tv_param_exit;

    @BindView(R.id.id_set_param_locks)
    ListView lvlocks;
    @BindView(R.id.id_set_param_round)
    ListView lvround;

    AdapterView.OnItemClickListener ItemClickEvent = null;
    int selectValue = 0;
    protected int getLayoutId() {
        return R.layout.activity_paramset;
    }

    @Override
    protected void initView() {
        tv_param_exit.setOnClickListener(closeEvent);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lockscreen_mins,android.R.layout.simple_list_item_single_choice );
        lvlocks.setAdapter(adapter);
        lvlocks.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvlocks.setItemChecked(CGlobalData.lockscreen,true);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.round_scale,android.R.layout.simple_list_item_single_choice );
        lvround.setAdapter(adapter1);
        lvround.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvround.setItemChecked(CGlobalData.roundbit,true);

        ItemClickEvent = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selectValue = arg2;
                if(arg0.getId()==R.id.id_set_param_locks){
                    updateStatus("SET_COUNTER_LOCKS",0,arg2);
                }else{
                    updateStatus("SET_COUNTER_ROUND",1,arg2);
                }
            }
        };

        lvlocks.setOnItemClickListener(ItemClickEvent);
        lvround.setOnItemClickListener(ItemClickEvent);
    }

    private void updateStatus(String sqlid,int requestCode,int value){
        showProgress("");
        CHttpUtil.Post("icommon/update.jsp",
                new String[]{"sqlid",sqlid,"value",value+""},
                this,
                requestCode);
    }

    public void dataLoadCallback(boolean isHttpCall, int requestCode, int responseCode, Object data){
        hideProgress();
        if(responseCode<0){
            toastLong(data.toString());
            return;
        }
        try {
            CJson json = new CJson();
            if (json.toJsonObject(data.toString())) {
                if(requestCode==0){  //更新锁屏时间
                    CGlobalData.lockscreen = selectValue;
                }else{                //更新四舍五入
                    CGlobalData.roundbit = selectValue;
                }
            }else{
                toastLong(json.getErrorMsg());
            }
        }catch (Exception ex){
            toastLong(ex.getMessage());
        }
    }
}
