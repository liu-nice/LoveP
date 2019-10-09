/*
 * Copyright  2016 - Goertek- All rights reserved.
 */
package com.goertek.aitutu.camera.filter;

import android.content.Context;

import com.goertek.aitutu.R;

/**
 * describition :负责往屏幕上渲染
 *
 * @author ;falzy.ning
 * @version :1.0.0
 * @since : 2019/10/9 14:51
 */
public class ScreenFilter extends AbstractFilter{

    public ScreenFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.base_frag);
    }

}
