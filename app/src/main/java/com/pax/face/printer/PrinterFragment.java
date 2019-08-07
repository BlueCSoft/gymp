package com.pax.face.printer;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.pax.face.util.BackListAdapter;
import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;

public class PrinterFragment extends BaseActivity implements OnItemClickListener {
    private LinearLayout screenLayout;
    private GridView consoleGridView;
    private BackListAdapter adapter;

    @Override
    protected int getLayoutId(){
        return R.layout.fragment_main;
    }

    //初始化视图
    @Override
    protected void initView() {
        screenLayout = (LinearLayout) findViewById(R.id.fragment_screen);
        consoleGridView = (GridView) findViewById(R.id.fragment_gridview);
        adapter = new BackListAdapter(Arrays.asList(getResources().getStringArray(R.array.Printer)), this);
        consoleGridView.setAdapter(adapter);
        consoleGridView.setOnItemClickListener(this);
    }


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.setPos(position);
        adapter.notifyDataSetChanged();
        switch (position) {
            case 0:
                callActivity(this,PrintStrFragment.class);
                break;
            case 1:
                callActivity(this,CutPaperFragment.class);
                break;
            default:
                break;
        }
    }
}
