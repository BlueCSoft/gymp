package com.synics.gymp.iface;

import android.view.View;

/**
 * Created by Administrator on 2019/06/22.
 */

public interface IConditionSelect {
    void conditionSelect(int requestCode, Boolean isConfirm, View source, int position, String value);
    Boolean conditionCheck(int requestCode,Boolean isConfirm, View source, int position, String value);
}
