package com.synics.gymp.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2019/7/12.
 */

public class SimpleExAdapter extends SimpleAdapter {
    private int selectedPosition = -1;
    private View selectedView = null;

    public SimpleExAdapter(Context context, List<? extends Map<String, ?>> data,
                           @LayoutRes int resource, String[] from, @IdRes int[] to) {
        super(context, data, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (selectedPosition == position) {
            view.setBackgroundColor(0xfffdf292);
            selectedView = view;
        } else {
            view.setBackgroundColor(0xffffffff);
        }
        return view;
    }

    public void setSelected(int position, View view) {
        if (selectedView != null) {
            selectedView.setBackgroundColor(0xffffffff);
        }
        selectedPosition = position;
        selectedView = view;
        selectedView.setBackgroundColor(0xfffdf292);
    }

    @Override
    public void notifyDataSetChanged() {
        selectedView = null;
        selectedPosition = 0;
        super.notifyDataSetChanged();
    }

    public int getSelectedPosition(){
        return selectedPosition;
    }
}
