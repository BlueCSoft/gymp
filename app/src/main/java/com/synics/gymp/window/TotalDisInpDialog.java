package com.synics.gymp.window;

import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.bluec.CBillHead;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.COrigBill;
import com.synics.gymp.bluec.CUtil;

/**
 * Created by Administrator on 2019/7/9.
 */

public class TotalDisInpDialog extends ConditionDialog {
    RadioGroup mrgZkatt;
    EditText metZk;
    RadioGroup mrgFtatt;
    EditText metFt;

    public TotalDisInpDialog(int requestCode, int layoutId, BaseActivity parent, View topPanel) {
        super(requestCode, layoutId, parent, topPanel);
    }

    @Override
    protected void initData(View contentView, BaseActivity parent) {
        try {
            CBillHead mbillHead = COrigBill.get().getMbillHead();

            mrgZkatt = (RadioGroup) contentView.findViewById(R.id.id_totaldisinp_zkatt);
            metZk = (EditText) contentView.findViewById(R.id.id_totaldisinp_zk);
            mrgFtatt = (RadioGroup) contentView.findViewById(R.id.id_totaldisinp_ftatt);
            metFt = (EditText) contentView.findViewById(R.id.id_totaldisinp_ft);
            int n = mbillHead.getInt("totaldisatt");
            ((RadioButton) mrgZkatt.getChildAt(n)).setChecked(true);
            metZk.setText(mbillHead.getString("totaldiscountrate"));

            ((RadioButton) mrgFtatt.getChildAt(mbillHead.getInt("totaldisshareatt"))).setChecked(true);
            metFt.setText(mbillHead.getString("tsupplysharerate"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void doReturn(View v) {
        try {
            int zkatt = CUtil.getSelectedIndex(mrgZkatt);
            double nzk = Double.parseDouble(metZk.getText().toString());
            int ftatt = CUtil.getSelectedIndex(mrgFtatt);
            double nft = Double.parseDouble(metFt.getText().toString());
             if (nzk < 0) {
                toastLong("折扣率或折扣额不能小于0");
                return;
            }
            if (zkatt == 0 && nzk > 100) {
                toastLong("折扣率不能大于100");
                return;
            }
            if (nft < 0 || nft > 100) {
                toastLong("分摊比例必须在0-100之间");
                return;
            }
            if (ftatt == 2 && nft >= 100) {
                toastLong("共同分摊时厂商的比例必须小于100");
                return;
            }
            CBillHead mbillHead = COrigBill.get().getMbillHead();
            mbillHead.setString("managerid", CGlobalData.managerid);
            mbillHead.setString("manager", CGlobalData.managername);
            mbillHead.setDouble("totaldisatt", zkatt);
            mbillHead.setDouble("totaldiscountrate", nzk);
            mbillHead.setDouble("totaldisshareatt", ftatt);
            mbillHead.setDouble("tsupplysharerate", nft);
            mbillHead.calTotalDiscount();
            dialog.dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
