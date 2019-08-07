package com.synics.gymp.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.synics.gymp.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    protected ProgressDialog mDialog = null;
    private Unbinder unbinder = null;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected int getLayoutId() {
        return 0;
    }

    protected void initView(View view) {
    }

    ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(getLayoutId(), container, false);
        //返回一个Unbinder值（进行解绑），注意这里的this不能使用getActivity()
        unbinder = ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * 显示进度条
     *
     * @param msg
     */
    public void showProgress(String msg) {
        if (mDialog == null) {
            mDialog = new android.app.ProgressDialog(getActivity());
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
        }
        mDialog.setMessage((msg.equals("")) ? "请稍后..." : msg);
        mDialog.show();
    }

    /**
     * 隐藏进度条
     */
    public void hideProgress() {
        if (mDialog != null) mDialog.hide();
    }

    protected void msgBox(String title, String message, String[] btInfo,
                          DialogInterface.OnClickListener OnClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        for (int i = 0; i < btInfo.length; i++) {
            switch (i) {
                case 0:
                    builder.setPositiveButton(btInfo[i], OnClick);
                    break;
                case 1:
                    builder.setNegativeButton(btInfo[i], OnClick);
                    break;
                case 2:
                    builder.setNeutralButton(btInfo[i], OnClick);
                    break;
            }
        }
        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void msgBox(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("确定",null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void msgBox(String message) {
        msgBox("提示",message);
    }

    protected void callActivity(Context packageContext, Class<?> cls, String[] keyValues, int reqCode) {
        Intent intent = new Intent(packageContext, cls);

        if (keyValues != null) {
            for (int i = 0; i < keyValues.length; i++) {
                String[] v = keyValues[i].split("\\=");
                intent.putExtra(v[0], v[1]);
            }
        }
        if (reqCode == 0) {
            ((Activity) packageContext).startActivity(intent);
        } else {
            ((Activity) packageContext).startActivityForResult(intent, reqCode);
        }
    }

    protected void callActivity(Context packageContext, Class<?> cls, String[] keyValues) {
        Intent intent = new Intent(packageContext, cls);

        if (keyValues != null) {
            for (int i = 0; i < keyValues.length; i++) {
                String[] v = keyValues[i].split("\\=");
                intent.putExtra(v[0], v[1]);
            }
        }
        ((Activity) packageContext).startActivity(intent);
    }

    protected void callActivity(Context packageContext, Class<?> cls, int reqCode) {
        Intent intent = new Intent(packageContext, cls);
        ((Activity) packageContext).startActivityForResult(intent, reqCode);
    }

    protected void callActivity(Context packageContext, Class<?> cls) {
        Intent intent = new Intent(packageContext, cls);
        ((Activity) packageContext).startActivity(intent);
    }

    protected void close() {
        getActivity().finish();
    }

    protected void close(int resultCode) {
        getActivity().setResult(resultCode);
        getActivity().finish();
    }

    protected void toast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    protected void toastLong(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }
}
