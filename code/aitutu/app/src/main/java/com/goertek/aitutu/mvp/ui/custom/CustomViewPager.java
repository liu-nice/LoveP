/*
 * Copyright (c) 2019 -Goertek -All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * 禁用ViewPager滑动事件
 */
public class CustomViewPager extends ViewPager {

    private boolean isCanScroll = false;

    public CustomViewPager(Context context) {
        super(context);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        isCanScroll = true;
        super.setCurrentItem(item, smoothScroll);
        isCanScroll = false;
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, false);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        if (isCanScroll) {
            super.scrollTo(x, y);
        }
    }
}
