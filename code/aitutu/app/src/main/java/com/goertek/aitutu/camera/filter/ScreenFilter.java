package com.goertek.aitutu.camera.filter;

import android.content.Context;

import com.goertek.aitutu.R;

/**
 * 负责往屏幕上渲染
 */
public class ScreenFilter extends AbstractFilter{

    public ScreenFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.base_frag);
    }

}
