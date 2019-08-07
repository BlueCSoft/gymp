package com.synics.gymp.bluec;

import com.pax.dal.IDAL;
import com.synics.gymp.activity.LoginActivity;

/**
 * Created by Administrator on 2019/7/7.
 */

public class CIFace {
    public static IDAL getDal(){
        return LoginActivity.idal;
    }
}
