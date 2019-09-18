package com.goertek.aitutu.mvp.ui.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import com.goertek.aitutu.R;
import com.goertek.aitutu.mvp.model.entity.PhotoBean;
import com.goertek.aitutu.mvp.ui.holder.UserItemHolder;
import com.goertek.arm.base.BaseHolder;
import com.goertek.arm.base.DefaultAdapter;

import java.util.List;

public class UserAdapter extends DefaultAdapter<PhotoBean> {
    public UserAdapter(List<PhotoBean> infos) {
        super(infos);
    }

    @NonNull
    @Override
    public BaseHolder<PhotoBean> getHolder(@NonNull View v, int viewType) {
        return new UserItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.recycle_list;
    }
}
