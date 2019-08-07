package com.synics.gymp.cash;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivityEx;
import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.COrigBill;
import com.synics.gymp.bluec.CUtil;

public class CashqueryvipActivity extends BaseActivityEx {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cashqueryvip;
    }

    @Override
    protected void initViewAfter() {
        try {
            Intent i = getIntent();
            CHttpUtil.Post("icommon/getvipinfo.jsp", new String[]{"vipid", i.getStringExtra("kh")
            }, this, CConfig.HTTPEVENT_VIPQUERY);
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), ex.getMessage());
        }
    }

    @Override
    protected void handleHttp(int requestCode, CJson json) {
        View view = getWindow().getDecorView();
        setValueByViewTag(view, "vipid", json.getStringEx("sno"));
        setValueByViewTag(view, "viptype", json.getStringEx("typename"));
        setValueByViewTag(view, "vipname", CUtil.showSubStr(json.getStringEx("sname"), 1));
        setValueByViewTag(view, "ypoints", json.getStringEx("apoints"));
        setValueByViewTag(view, "apoints", json.getStringEx("points"));
        setValueByViewTag(view, "yxrq", json.getStringEx("yxq"));

        getViewByViewTag(view, "isyx").setVisibility((json.getIntEx("isyx") == 1) ? View.VISIBLE : View.GONE);
        getViewByViewTag(view, "iswx").setVisibility((json.getIntEx("isyx") == 0) ? View.VISIBLE : View.GONE);
    }
}
