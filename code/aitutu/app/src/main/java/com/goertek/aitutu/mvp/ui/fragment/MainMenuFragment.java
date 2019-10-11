/*
 * Copyright (c) 2019 -Goertek -All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goertek.aitutu.R;
import com.goertek.aitutu.mvp.ui.activity.DoodleViewActivity;
import com.goertek.aitutu.mvp.ui.activity.RevolveActivity;
import com.goertek.aitutu.mvp.ui.custom.ModuleConfig;

public class MainMenuFragment extends BaseFragment implements View.OnClickListener {
    //主模块
    public static final int INDEX = ModuleConfig.INDEX_MAIN;
    //主view
    private View mainView;
    //贴图按钮
    private View stickerBtn;
    //滤镜按钮
    private View fliterBtn;
    //剪裁按钮
    private View cropBtn;
    //旋转按钮
    private View rotateBtn;
    //文字型贴图添加
    private View mTextBtn;
    //编辑按钮
    private View mPaintBtn;
    //美颜按钮
    private View mBeautyBtn;

    public static MainMenuFragment newInstance() {
        MainMenuFragment fragment = new MainMenuFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_main_menu, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        stickerBtn = mainView.findViewById(R.id.btn_stickers);
        fliterBtn = mainView.findViewById(R.id.btn_filter);
        cropBtn = mainView.findViewById(R.id.btn_crop);
        rotateBtn = mainView.findViewById(R.id.btn_rotate);
        mTextBtn = mainView.findViewById(R.id.btn_text);
        mPaintBtn = mainView.findViewById(R.id.btn_paint);
        mBeautyBtn = mainView.findViewById(R.id.btn_beauty);

        stickerBtn.setOnClickListener(this);
        fliterBtn.setOnClickListener(this);
        cropBtn.setOnClickListener(this);
        rotateBtn.setOnClickListener(this);
        mTextBtn.setOnClickListener(this);
        mPaintBtn.setOnClickListener(this);
        mBeautyBtn.setOnClickListener(this);
    }

    @Override
    public void onShow() {

    }

    @Override
    public void backToMain() {

    }

    @Override
    public void onClick(View v) {
        if (v == stickerBtn) {
            onStickClick();
        } else if (v == fliterBtn) {
            onFilterClick();
        } else if (v == cropBtn) {
            onCropClick();
        } else if (v == rotateBtn) {
            onRotateClick();
        } else if (v == mTextBtn) {
            onAddTextClick();
        } else if (v == mPaintBtn) {
            onPaintClick();
        } else if (v == mBeautyBtn) {
            onBeautyClick();
        }
    }

    /**
     * 贴图模式
     */
    private void onStickClick() {
        activity.mBottomViewPager.setCurrentItem(StickerFragment.INDEX);
        activity.mStickerFragment.onShow();
    }

    /**
     * 图片旋转模式
     */
    private void onRotateClick() {
        startActivityForResult(new Intent(activity,RevolveActivity.class).putExtra(RevolveActivity.FILE_PATH,activity.filePath),activity.EXTRA_REVOLVE);
    }

    /**
     * 滤镜模式
     */
    private void onFilterClick() {
    }

    /**
     * 裁剪模式
     */
    private void onCropClick() {
    }

    /**
     * 插入文字模式
     */
    private void onAddTextClick() {
    }

    /**
     * 自由绘制模式
     */
    private void onPaintClick() {
        Intent intent = new Intent(getActivity(), DoodleViewActivity.class);
        intent.putExtra(DoodleViewActivity.FILE_PATH ,activity.filePath);
        startActivity(intent);
    }

    /**
     * 美图
     */
    private void onBeautyClick() {
    }
}
