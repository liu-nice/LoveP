/*
 * Copyright  2016 - Goertek- All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.goertek.aitutu.R;
import com.goertek.arm.base.BaseActivity;
import com.goertek.arm.di.component.AppComponent;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * description:FilterActivity  is saveImage
 *
 * @author libin
 * @version 1.0
 */

public class FilterActivity extends BaseActivity {


    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.llayout_contioner)
    LinearLayout llayoutContioner;
    @BindView(R.id.tab_bar)
    HorizontalScrollView tabBar;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_filter;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
