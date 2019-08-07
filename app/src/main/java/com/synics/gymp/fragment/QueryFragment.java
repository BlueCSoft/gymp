package com.synics.gymp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synics.gymp.R;
import com.synics.gymp.query.BillDayReport;
import com.synics.gymp.query.LoginoutQuery;
import com.synics.gymp.query.PayQuery;
import com.synics.gymp.query.SellDayReport;

import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class QueryFragment extends BaseFragment implements View.OnClickListener{


    public QueryFragment() {
        // Required empty public constructor
    }

    protected int getLayoutId(){
        return R.layout.fragment_query;
    }

    @OnClick({R.id.id_query_daybillrep,R.id.id_query_daysellrep,R.id.id_query_payquery,R.id.id_query_logquery,
            R.id.id_query_goodsquery})
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.id_query_daybillrep:
                callActivity(getActivity(), BillDayReport.class);
                break;
            case R.id.id_query_daysellrep:
                callActivity(getActivity(), SellDayReport.class);
                break;
            case R.id.id_query_payquery:
                callActivity(getActivity(), PayQuery.class);
                break;
            case R.id.id_query_logquery:
                callActivity(getActivity(), LoginoutQuery.class);
                break;
            case R.id.id_query_goodsquery:
                break;
        }
    }
}
