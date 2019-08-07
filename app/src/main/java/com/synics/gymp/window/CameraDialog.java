package com.synics.gymp.window;

import android.view.View;
import android.view.ViewGroup;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.camera.CameraPreview;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2019/7/11.
 */

public class CameraDialog extends ConditionDialog implements CameraPreview.Callback {
    private CameraPreview mPreview;

    public CameraDialog(int requestCode, int layoutId, BaseActivity parent, View topPanel) {
        super(requestCode, layoutId, parent, topPanel);
    }

    @Override
    protected void initData(View contentView, BaseActivity parent) {
        mPreview = new CameraPreview(parent);
        mPreview.setCallback(this);
        ViewGroup viewGroup = ButterKnife.findById(contentView, R.id.id_camera_preview);
        viewGroup.addView(mPreview);
    }

    public void onResult(String barcode) {
        dialog.dismiss();
    }
}
