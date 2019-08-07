package com.synics.gymp.window;

import android.view.LayoutInflater;
import android.view.View;

import com.synics.gymp.activity.BaseActivity;

/**
 * Created by Administrator on 2019/7/6.
 */

public class OpenWindow {
    public OpenWindow(int layoutId, BaseActivity parent, View topPanel) {
        View contentView = LayoutInflater.from(parent).inflate(layoutId, null, false);
        new ConditionWindow(contentView,parent,topPanel);
    }
}
