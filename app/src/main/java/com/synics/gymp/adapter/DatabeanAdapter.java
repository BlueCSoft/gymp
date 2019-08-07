package com.synics.gymp.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

//import com.synics.gymp.bluec.PosProductBean;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2019/7/7.
 */

public class DatabeanAdapter<DataBean> extends BaseAdapter implements View.OnClickListener {
    protected Context mContext;
    private List<DataBean> mData;
    private DatabeanAdapter.Bind<DataBean> mBind;
    private DACallback mCallback = null;

    public interface DACallback {
        public void onDaClick(View v);
    }

    /**
     * @param context 上下文对象
     * @param data    数据对象
     * @param bind    绑定对象
     */
    public DatabeanAdapter(Context context, List<DataBean> data, DatabeanAdapter.Bind<DataBean> bind,
                           DACallback callback) {
        this.mContext = context;
        this.mData = data;
        this.mBind = bind;
        this.mCallback = callback;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mBind.getLayoutId(), parent, false);
        }

        mBind.bindView(convertView);
        mBind.bindData(this, mContext, position, convertView, mData.get(position));

        return convertView;
    }

    /**
     * 绑定类：将数据绑定到 View 中
     *
     * @param <D> JavaBean
     */
    public static abstract class Bind<DataBean> {
        public Bind() {
        }

        /**
         * 利用 ButterKnife 注解绑定控件
         *
         * @param view 布局对象
         */
        private void bindView(View view) {
            ButterKnife.bind(this, view);
        }

        /**
         * 抽象方法，用于将数据与控件进行绑定
         *
         * @param position 数据项
         * @param view     item
         * @param data     JavaBean
         */
        public abstract void bindData(DatabeanAdapter parent,Context context, int position, View view, DataBean row);

        /**
         * 返回需要绑定的布局
         *
         * @return 布局
         */
        @LayoutRes
        public abstract int getLayoutId();
    }

    @Override
    public void onClick(View v) {
       mCallback.onDaClick(v);
    }

}
