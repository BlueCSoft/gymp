package com.synics.gymp.setting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.iface.IDataLoadCallback;

import butterknife.BindView;
import butterknife.OnClick;

public class ChangePwdActivity extends BaseActivity implements IDataLoadCallback {
    @BindView(R.id.id_set_changepwd_exit)
    ImageView tv_exit;

    @BindView(R.id.id_set_changepwd_btok)
    TextView tv_btok;

    @BindView(R.id.id_set_changepwd_lv)
    ListView lvchangepwd;

    @BindView(R.id.id_set_changepwd_oldpwd)
    EditText et_oldpwd;
    @BindView(R.id.id_set_changepwd_newpwd)
    EditText et_newpwd;
    @BindView(R.id.id_set_changepwd_newpwdr)
    EditText et_newpwdr;

    String salesid = "";         //选中人员的id

    protected int getLayoutId() {
        return R.layout.activity_change_pwd;
    }

    protected void initView() {
        tv_exit.setOnClickListener(closeEvent);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_single_choice, CGlobalData.getSales());

        lvchangepwd.setAdapter(adapter);
        lvchangepwd.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        salesid = CGlobalData.cashierid;
        lvchangepwd.setItemChecked(CGlobalData.getSalesPosition(salesid), true);

        lvchangepwd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                salesid = CGlobalData.getSalesIdByPosition(arg2);
            }
        });

        tv_btok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePwd(et_oldpwd.getText().toString().trim(),
                        et_newpwd.getText().toString().trim(), et_newpwdr.getText().toString().trim());
            }
        });
    }

    private void changePwd(String opwd, String npwd, String npwdr) {
        if (opwd.equals("")) {
            toast("请输入原密码");
        } else {
            if (npwd.equals("")) {
                toast("请输入新密码");
            } else {
                if (!npwd.equals(npwdr)) {
                    toast("两次输入的新密码不一样");
                } else {
                    showProgress("");
                    CHttpUtil.Post("icommon/changesalespwd.jsp",
                            new String[]{"userid", salesid, "oldpwd", opwd, "newpwd", npwd},
                            this,
                            0);
                }
            }
        }
    }

    public void dataLoadCallback(boolean isHttpCall, int requestCode, int responseCode, Object data) {
        hideProgress();
        if (responseCode < 0) {
            toastLong(data.toString());
            return;
        }
        try {
            CJson json = new CJson();
            if(json.toJsonObject(data.toString())){
                toastLong("密码修改成功");
            }else{
                toastLong(json.getErrorMsg());
            }
        } catch (Exception ex) {
            toastLong(ex.getMessage());
        }
    }
}
