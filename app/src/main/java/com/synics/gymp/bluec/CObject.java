package com.synics.gymp.bluec;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/30.
 */

public class CObject implements Serializable {
    protected int mErrorCode = 0;       //异常代码
    protected String mErrorMsg = "";    //异常信息

    public int getErrorCode() {
        return mErrorCode;
    }
    public String getErrorMsg() {
        return mErrorMsg;
    }
}
