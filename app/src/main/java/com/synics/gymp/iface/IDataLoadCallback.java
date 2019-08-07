package com.synics.gymp.iface;

/**
 * Created by Administrator on 2019/06/22.
 */

public interface IDataLoadCallback {
    void dataLoadCallback(boolean isHttpCall, int requestCode, int responseCode, Object data);
}
