/*
 * Copyright  2016 - Goertek- All rights reserved.
 */
package com.goertek.aitutu.camera.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.goertek.aitutu.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * describition :
 *
 * @author ;falzy.ning
 * @version :1.0.0
 * @since : 2019/10/12 14:47
 */
public class BottomDialogView extends Dialog {
    //当前显示布局
    private View view;

    //上下文
    private Context context;

    //butterknife实例
    private Unbinder unbinder;

    //这里的view其实可以替换直接传layout过来的 因为各种原因没传(lan)
    public BottomDialogView(Context context, int resId) {
        super(context, R.style.BottomDialog);
        this.context = context;
        this.view = LayoutInflater.from(context).inflate(resId, null);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setDimAmount(0f);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }

    /**
     * 解绑
     */
    protected void unbinder() {
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
