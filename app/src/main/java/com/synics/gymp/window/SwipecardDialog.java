package com.synics.gymp.window;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.pax.dal.IMag;
import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.activity.LoginActivity;
import com.synics.gymp.iface.IAuthorizeCallBack;

import com.pax.dal.entity.TrackData;
/**
 * Created by Administrator on 2019/8/5.
 */

public class SwipecardDialog extends ConditionDialog {
    TextView tvInfo;
    IAuthorizeCallBack mCallback = null;
    static MagReadThread magReadThread;

    static IMag iMag = LoginActivity.idal.getMag();

    public SwipecardDialog(int requestCode, BaseActivity parent, View topPanel, IAuthorizeCallBack mCallback){
        super(requestCode, R.layout.dialog_swipecard, parent, topPanel);
        this.mCallback = mCallback;
    }

    @Override
    protected void initData(View contentView, BaseActivity parent) {
        try {
            tvInfo = (TextView) contentView.findViewWithTag("info");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPaint(){
        if (magReadThread == null) {
            magReadThread = new MagReadThread();
            open();
            reset();
            magReadThread.start();
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    //tvInfo.setText(msg.obj.toString());
                    dismiss();
                    mCallback.onConfirm(requestCode,"",msg.obj.toString());
                    break;
                default:
                    break;
            }
        };
    };

    private void open() {
        try {
            iMag.open();
        } catch (Exception e) {
            tvInfo.setText(e.getMessage());
        }
    }

    private void close() {
        try {
            iMag.close();
        } catch (Exception e) {
            tvInfo.setText(e.getMessage());
        }
    }

    // Reset magnetic stripe card reader, and clear buffer of magnetic stripe card.
    private void reset() {
        try {
            iMag.reset();
        } catch (Exception e) {
            tvInfo.setText(e.getMessage());
        }
    }

    // Check whether a card is swiped
    private boolean isSwiped() {
        boolean b = false;
        try {
            b = iMag.isSwiped();
        } catch (Exception e) {
            tvInfo.setText(e.getMessage());
        }
        return b;
    }

    private TrackData read() {
        try {
            TrackData trackData = iMag.read();
            return trackData;
        } catch (Exception e) {
            tvInfo.setText(e.getMessage());
            return null;
        }
    }
    class MagReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!Thread.interrupted()) {
                if (isSwiped()) {
                    TrackData trackData = read();
                    if (trackData != null) {
                        String resStr = "";
                        if (trackData.getResultCode() == 0) {
                            Message.obtain(handler, 0, "读卡出现错误,请重新刷卡...").sendToTarget();
                            continue;
                        }
                        /*
                        if ((trackData.getResultCode() & 0x01) == 0x01) {
                            resStr += "第一道数据:" + trackData.getTrack1();
                        } */

                        if ((trackData.getResultCode() & 0x02) == 0x02) {
                            resStr = trackData.getTrack2();
                        }
                        /*
                        if ((trackData.getResultCode() & 0x04) == 0x04) {
                            resStr += "第三道数据:" + trackData.getTrack3();
                        }
                        */
                        Message.obtain(handler, 0, resStr).sendToTarget();
                    }
                    break;
                }
                SystemClock.sleep(100);
            }
        }
    }

    @Override
    protected void onClose(){
        if (magReadThread != null) {
            close();
            magReadThread.interrupt();
            magReadThread = null;
        }
    }
}
