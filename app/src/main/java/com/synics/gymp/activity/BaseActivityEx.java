package com.synics.gymp.activity;

import com.synics.gymp.bluec.CJson;
import com.synics.gymp.iface.IDataLoadCallback;

/**
 * Created by Administrator on 2019/7/3.
 */

public abstract class BaseActivityEx extends BaseActivity implements IDataLoadCallback {

    protected void handleHttp(int requestCode, CJson json) {
    }

    protected void handleEvent(int requestCode, Object data) {
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
                    handleHttp(requestCode, json);
                } else {
                    msgBox(json.getErrorMsg());
                }
            } else {
                handleEvent(requestCode, data);
            }
        } catch (Exception ex) {
            msgBox(ex.getMessage());
        }
    }
}
