package com.synics.gymp.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pax.face.scancodec.CodecActivity;
import com.pax.pay.cup.test.MainActivity;
import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;
import com.synics.gymp.bluec.CBillHead;
import com.synics.gymp.bluec.CConfig;
import com.synics.gymp.bluec.CGlobalData;
import com.synics.gymp.bluec.CHttpUtil;
import com.synics.gymp.bluec.CJson;
import com.synics.gymp.bluec.CObject;
import com.synics.gymp.bluec.COrigBill;
import com.synics.gymp.cash.CashmainActivity;
import com.synics.gymp.cash.CashqueryvipActivity;
import com.synics.gymp.cash.NofinishiBillActivity;
import com.synics.gymp.cash.PaymainActivity;
import com.synics.gymp.cash.PayrefundActivity;
import com.synics.gymp.iface.IAuthorizeCallBack;
import com.synics.gymp.iface.IDataLoadCallback;
import com.synics.gymp.window.VipInpDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CashFragment extends BaseFragmentEx implements View.OnClickListener {
    @BindView(R.id.id_cash_mposid)
    TextView cash_mposid;
    @BindView(R.id.id_cash_sales)
    TextView cash_sales;
    @BindView(R.id.id_cash_counterid)
    TextView cash_counterid;
    @BindView(R.id.id_cash_counter)
    TextView cash_counter;

    @BindView(R.id.id_cash_paycash)
    TextView cash_paycash;
    @BindView(R.id.id_cash_paycash0)
    TextView cash_paycash0;

    @BindView(R.id.id_cash_sum_kd)
    TextView cash_sum_kd;
    @BindView(R.id.id_cash_sum_ys)
    TextView cash_sum_ys;
    @BindView(R.id.id_cash_sum_gd)
    TextView cash_sum_gd;
    @BindView(R.id.id_cash_sum_cd)
    TextView cash_sum_cd;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cash;
    }

    @Override
    protected void initView(View view) {
        cash_mposid.setText(CConfig.SMPOSID);
        cash_sales.setText(CGlobalData.cashierid + "(" + CGlobalData.cashier + ")");
        cash_counterid.setText(CGlobalData.counterid);
        cash_counter.setText(CGlobalData.counter);
        if (CGlobalData.ispaycash == 1) {
            cash_paycash0.setVisibility(View.GONE);
            cash_paycash.setVisibility(View.VISIBLE);
        }
        getInfo();
    }

    @OnClick({R.id.id_cash_sellbt, R.id.id_cash_presellbt, R.id.id_cash_yss, R.id.id_cash_paycash, R.id.id_cash_sum_gd0, R.id.id_cash_sum_gd,
            R.id.id_cash_vip_inp})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_cash_sellbt:
                getBillKeyAndId(0);
                break;
            case R.id.id_cash_presellbt:
                getBillKeyAndId(1);
                break;
            case R.id.id_cash_yss:
                //callActivity(getActivity(), MainActivity.class);
                toast("等待上线");
                break;
            case R.id.id_cash_paycash:
                callActivity(getActivity(), CodecActivity.class, CConfig.CALLEVENT_SACNBILL);
                break;
            case R.id.id_cash_sum_gd0:
            case R.id.id_cash_sum_gd:
                callActivity(getActivity(), NofinishiBillActivity.class, CConfig.CALLEVENT_TONOFINISHI);
                break;
            case R.id.id_cash_vip_inp:
                openFindVipOp(null);
                break;
        }
    }

    /* 查询会员信息 */
    private void queryVip(int requestCode, String kh) {
        CHttpUtil.Post("icommon/getvipinfo.jsp", new String[]{"vipid", kh}, this, requestCode);
    }

    /* 会员查询卡号输入窗口 */
    public void openFindVipOp(View source) {
        new VipInpDialog(CConfig.HTTPEVENT_VIPQUERY, (BaseActivity) getActivity(), null,
                new IAuthorizeCallBack() {
                    @Override
                    public void onConfirm(int requestCode, String uid, String uName) {
                        if (!uid.equals("")) {
                            queryVip(requestCode, uid);
                        } else {
                            callActivity(getActivity(), CodecActivity.class, CConfig.HTTPEVENT_VIPQUERY);
                        }
                    }
                });
    }

    /*获取今日订单情况*/
    private void getInfo() {
        showProgress("");
        CHttpUtil.Post("icommon/queryone.jsp", new String[]{"sqlid", "GET_CASH_SUM", "rq", CGlobalData.workdate}, this,
                CConfig.HTTPEVENT_GETDAYBILLINFO);
    }

    /*获取订单key和id,billtype=1预售*/
    private void getBillKeyAndId(int billtype) {
        showProgress("");
        CHttpUtil.Post("icommon/ympgetkeyandid.jsp", new String[]{"billtype", billtype + ""}, this,
                CConfig.HTTPEVENT_GETBILLKEYANID);
    }

    @Override
    protected void handleHttp(int requestCode, CJson json) {
        try {
            switch (requestCode) {
                case CConfig.HTTPEVENT_GETDAYBILLINFO:
                    cash_sum_kd.setText(json.getStringEx("xsds").concat(" 单"));
                    cash_sum_ys.setText(json.getStringEx("ysds").concat(" 单"));
                    cash_sum_gd.setText(json.getStringEx("gds").concat(" 单"));
                    cash_sum_cd.setText(json.getStringEx("cds").concat(" 单"));
                    break;
                case CConfig.HTTPEVENT_GETBILLKEYANID:
                    CGlobalData.origbillkey = json.getInt("billkey");
                    CGlobalData.origbillid = json.getString("billid");
                    CGlobalData.time = json.getString("time");
                    CGlobalData.billtype = json.getInt("billtype");
                    COrigBill.get().emptyDataSet();
                    COrigBill.get().getMbillHead().insert();
                    callActivity(getActivity(), CashmainActivity.class, CConfig.CALLEVENT_CASHMAIN);
                    break;
                case CConfig.HTTPEVENT_VIPQUERY:
                    callActivity(getActivity(), CashqueryvipActivity.class, new String[]{"kh=" + json.getStringEx("sno")});
                    break;
                case CConfig.HTTPEVENT_BILLINFO: //扫码读订单成功
                    COrigBill.get().loadFromJsonObj(json.getJsonObj());
                    /*
                    CBillHead head = COrigBill.get().getMbillHead();
                    if (head.getInt("billstate") != 1 || head.getInt("paidbycash") != 1) {
                        throw new Exception("订单不在待支付和现金收款请求状态");
                    }*/
                    callActivity(getActivity(), PaymainActivity.class, CConfig.CALLEVENT_CASHCOUNTER);
                    break;
            }
        } catch (Exception ex) {
            msgBox(ex.getMessage());
        }
    }

    @Override
    protected void handleEvent(int requestCode, Object data) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CConfig.CALLEVENT_TONOFINISHI:
                getInfo();
                if (resultCode == CConfig.RETURNEVENT_TONOFINISHI) {   //取单返回，直接打开单界面
                    int billstate = COrigBill.get().getMbillHead().getInt("billstate");
                    if (billstate < 0) {
                        callActivity(getActivity(), PayrefundActivity.class, CConfig.HTTPEVENT_BILLREFUND);
                    } else {
                        if (billstate >= 1) {
                            callActivity(getActivity(), PaymainActivity.class, CConfig.CALLEVENT_CASHCOUNTER);
                        } else {
                            callActivity(getActivity(), CashmainActivity.class, CConfig.CALLEVENT_CASHMAIN);
                        }
                    }
                }
                break;
            case CConfig.CALLEVENT_SACNBILL:  //扫订单二维码
                if (resultCode == Activity.RESULT_OK) {
                    String[] codes = CGlobalData.returnData.getStringExtra("code").split(",");
                    String billkey = codes[1];
                    showProgress("");
                    CHttpUtil.Post("iymp/billinfo.jsp", new String[]{
                            "billkey", billkey, "paycash", "1"}, this, CConfig.HTTPEVENT_BILLINFO);
                }
                break;
            default:
                getInfo();
                break;
        }
    }
}
