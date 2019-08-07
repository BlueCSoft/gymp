package com.synics.gymp.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pax.face.scancodec.CodecActivity;
import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.bluec.CBillHead;
import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.COrigBill;
import com.synics.gymp.cash.PaymainActivity;
import com.synics.gymp.cash.PayrefundActivity;
import com.synics.gymp.iface.IAuthorizeCallBack;
import com.synics.gymp.trans.BillToErpActivity;
import com.synics.gymp.trans.BillTransActivity;
import com.synics.gymp.window.BillidInpDialog;

import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransFragment extends BaseFragmentEx implements View.OnClickListener {

    protected int getLayoutId() {
        return R.layout.fragment_trans;
    }

    private void loadBill(String billid) {
        showProgress("读取订单...");
        CHttpUtil.Post("iymp/billinfobyid.jsp", new String[]{
                "billid", billid}, this, CConfig.HTTPEVENT_BILLINFO);
    }

    private void doBillRefund() {
        new BillidInpDialog(0, (BaseActivity) getActivity(), null,
                new IAuthorizeCallBack() {
                    @Override
                    public void onConfirm(int requestCode, String uid, String uName) {
                        if (!uid.equals("")) {
                            loadBill(uid);
                        } else {
                            callActivity(getActivity(), CodecActivity.class, CConfig.CALLEVENT_SCANBILLID);
                        }
                    }
                });
    }

    @OnClick({R.id.id_tran_billdo, R.id.id_tran_billtoerp, R.id.id_tran_storedo, R.id.id_tran_billtorefund})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tran_billdo:
                callActivity(getActivity(), BillTransActivity.class);
                break;
            case R.id.id_tran_billtoerp:
                callActivity(getActivity(), BillToErpActivity.class);
                break;
            case R.id.id_tran_storedo:
                break;
            case R.id.id_tran_billtorefund:
                doBillRefund();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CConfig.CALLEVENT_SCANBILLID:
                if (resultCode == Activity.RESULT_OK) {
                    String[] codes = CGlobalData.returnData.getStringExtra("code").split(",");
                    loadBill(codes[0]);
                }
                break;
        }
    }

    @Override
    protected void handleHttp(int requestCode, CJson json) {
        try {
            switch (requestCode) {
                case CConfig.HTTPEVENT_BILLINFO:
                    COrigBill.get().loadFromJsonObj(json.getJsonObj());
                    CBillHead head = COrigBill.get().getMbillHead();
                    try {
                        if (head.getInt("billstate") < 2) {
                            throw new Exception("订单状态应该是完成支付以后");
                        }

                        callActivity(getActivity(), PayrefundActivity.class, CConfig.HTTPEVENT_BILLREFUND);
                    } catch (Exception ex) {
                        msgBox(ex.getMessage());
                    }
                    break;
            }
        } catch (Exception ex) {
            msgBox(ex.getMessage());
        }
    }
}
