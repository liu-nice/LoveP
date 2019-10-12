/*
 * Copyright (c) 2019 -Goertek -All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.goertek.aitutu.R;
import com.goertek.aitutu.mvp.ui.adapter.StickersAdapter;
import com.goertek.aitutu.mvp.ui.custom.sticker.StickerView;
import com.goertek.aitutu.mvp.ui.custom.sticker.TextSticker;
import com.goertek.aitutu.util.BitmapUtils;
import com.goertek.arm.base.BaseActivity;
import com.goertek.arm.di.component.AppComponent;

import butterknife.BindView;

public class StickerActivity extends BaseActivity {

    public static final String FILE_PATH = "file_path";

    @BindView(R.id.activity_sticker_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_sticker_stickerview)
    StickerView mStickerView;

    @BindView(R.id.activity_sticker_imageview)
    ImageView mImageView;

    @BindView(R.id.activity_sticker_recycleview)
    RecyclerView mRecycleview;

    // 展示图片控件宽、高
    private int imageWidth, imageHeight;

    //需要编辑图片路径
    public String filePath;

    private Bitmap mMainBitmap;

    private StickersAdapter stickersAdapter;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_sticker_layout;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initToobar();
        getBitmapFormFile();
        initRecycleView();
    }

    private void getBitmapFormFile() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels / 2;
        imageHeight = metrics.heightPixels / 2;
        //拍照或者选择某一张相册图片,目前先写死
        filePath = getIntent().getStringExtra(FILE_PATH);
        mMainBitmap = BitmapUtils.getSampledBitmap(filePath,imageWidth,imageHeight);
        mImageView.setImageBitmap(mMainBitmap);
    }

    private void initRecycleView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecycleview.setLayoutManager(mLayoutManager);
        stickersAdapter = new StickersAdapter(this);
        mRecycleview.setAdapter(stickersAdapter);
        stickersAdapter.setOnItemClickListener(new StickersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int imageSource) {
                //添加贴纸
                TextSticker sticker = new TextSticker(StickerActivity.this);
                sticker.setText("Hello, world!");
                sticker.setTextColor(Color.RED);
                sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
                sticker.resizeText();
                mStickerView.addSticker(sticker);
            }
        });
    }

    private void initToobar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationOnClickListener(view -> finish());
        mToolbar.setOnMenuItemClickListener(onMenuItemClick);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_edit,menu);
        return true;
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_image_edit_save:
                    break;
            }
            return true;
        }
    };
}
