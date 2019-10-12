/*
 * Copyright (c) 2019 -Goertek -All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.activity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.goertek.aitutu.R;
import com.goertek.aitutu.mvp.ui.adapter.StickersAdapter;
import com.goertek.aitutu.mvp.ui.custom.sticker.DrawableSticker;
import com.goertek.aitutu.mvp.ui.custom.sticker.Sticker;
import com.goertek.aitutu.mvp.ui.custom.sticker.StickerView;
import com.goertek.aitutu.util.BitmapUtils;
import com.goertek.aitutu.util.FileUtils;
import com.goertek.aitutu.util.StringUtils;
import com.goertek.arm.base.BaseActivity;
import com.goertek.arm.di.component.AppComponent;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

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

    @BindView(R.id.activity_sticker_horizontalscollview)
    HorizontalScrollView mStickerHsw;

    @BindView(R.id.activity_sticker_revolve)
    TextView mStickerRevolve;

    @BindView(R.id.activity_sticker_sticker)
    TextView mStickerSticker;

    @BindView(R.id.activity_sticker_parentview)
    LinearLayout mStickerParentView;

    @BindView(R.id.activity_sticker_seekbar)
    SeekBar mSeekBar;

    @BindView(R.id.activity_sticker_recycleview_leftback)
    TextView mBottomLeftBack;

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
        initSeekBarEvent();
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
                DrawableSticker drawableSticker = new DrawableSticker(ContextCompat.getDrawable(StickerActivity.this,imageSource));
                mStickerView.addSticker(drawableSticker);
            }
        });
    }

    private void initSeekBarEvent() {
        //设置贴纸透明度(0~255)
        mSeekBar.setMax(255);
        mSeekBar.setProgress(255);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser) {
                Sticker currentSticker = mStickerView.getCurrentSticker();
                if (currentSticker != null) {
                    currentSticker.setAlpha(progress);
                    mStickerView.invalidate();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
                    new SavePicToFileTask().execute();
                    break;
            }
            return true;
        }
    };

    /**
     * 保存处理后的图片
     */
    private class SavePicToFileTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            File file = FileUtils.getNewFile(StickerActivity.this,"Sticker");
            if (file != null) {
                mStickerView.save(file);
            }
            return "success";
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            if (StringUtils.isEmpty(fileName)) {
                return;
            }
            finish();
        }
    }

    @OnClick({R.id.activity_sticker_revolve,R.id.activity_sticker_sticker,R.id.activity_sticker_recycleview_leftback})
    public void stickerEvent(View view) {
        switch (view.getId()) {
            case R.id.activity_sticker_revolve:

                break;
            case R.id.activity_sticker_sticker:
                //显示贴纸布局
                mStickerHsw.setVisibility(View.GONE);
                mStickerParentView.setVisibility(View.VISIBLE);
                break;
            case R.id.activity_sticker_recycleview_leftback:
                //隐藏贴纸布局
                mStickerParentView.setVisibility(View.GONE);
                mStickerHsw.setVisibility(View.VISIBLE);
                break;
        }
    }
}
