/*
 * Copyright (c) 2019 -Goertek -All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

/**
 * desciption 贴纸
 *
 * @author Davin
 * @version 1.0
 * @since 2019-10-15
 */
public class StickerActivity extends BaseActivity {

    public static final String FILE_PATH = "file_path";

    public static final int REQUEST_REVOLVE_CODE = 0x0011;

    public static final int NUMBER_255 = 255;

    /**
     * toolbar
     */
    @BindView(R.id.activity_sticker_toolbar)
    Toolbar mToolbar;

    /**
     * stickerview
     */
    @BindView(R.id.activity_sticker_stickerview)
    StickerView mStickerView;

    /**
     * view
     */
    @BindView(R.id.activity_sticker_imageview)
    ImageView mImageView;

    /**
     * 贴纸资源view
     */
    @BindView(R.id.activity_sticker_recycleview)
    RecyclerView mRecycleview;

    /**
     * module
     */
    @BindView(R.id.activity_sticker_horizontalscollview)
    HorizontalScrollView mStickerHsw;

    /**
     * 旋转
     */
    @BindView(R.id.activity_sticker_revolve)
    TextView mStickerRevolve;

    /**
     * 贴纸view
     */
    @BindView(R.id.activity_sticker_sticker)
    TextView mStickerSticker;

    /**
     * 贴纸功能主布局
     */
    @BindView(R.id.activity_sticker_parentview)
    LinearLayout mStickerParentView;

    /**
     * seekbar
     */
    @BindView(R.id.activity_sticker_seekbar)
    SeekBar mSeekBar;

    /**
     * 橡皮檫
     */
    @BindView(R.id.activity_sticker_eraser)
    TextView mEraserView;

    /**
     * 底部返回键
     */
    @BindView(R.id.activity_sticker_recycleview_leftback)
    TextView mBottomLeftBack;

    /**
     * 展示图片控件宽、高
     */
    private int imageWidth, imageHeight;

    /**
     * 需要编辑图片路径
     */
    private String filePath;
    /**
     * 主图
     */
    private Bitmap mMainBitmap;
    /**
     * 贴纸adapter
     */
    private StickersAdapter stickersAdapter;

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_image_edit_save:
                    new SavePicToFileTask().execute();
                    break;
                default:
                    break;
            }
            return true;
        }
    };


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

    /**
     * 初始化recycleview
     */
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
                DrawableSticker drawableSticker = new DrawableSticker(ContextCompat.getDrawable(StickerActivity.this, imageSource));
                mStickerView.addSticker(drawableSticker);
            }
        });
    }

    /**
     * 初始化seekbar
     */
    private void initSeekBarEvent() {
        //设置贴纸透明度(0~255)
        mSeekBar.setMax(NUMBER_255);
        mSeekBar.setProgress(NUMBER_255);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

    /**
     * 初始化toolbar
     */
    private void initToobar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationOnClickListener(view -> finish());
        mToolbar.setOnMenuItemClickListener(onMenuItemClick);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_edit, menu);
        return true;
    }

    /**
     * 保存处理后的图片
     */
    private class SavePicToFileTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            File file = FileUtils.getNewFile(StickerActivity.this, "Sticker");
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

    @OnClick({R.id.activity_sticker_revolve,R.id.activity_sticker_sticker,R.id.activity_sticker_eraser,R.id.activity_sticker_recycleview_leftback})
    public void stickerEvent(View view) {
        switch (view.getId()) {
            case R.id.activity_sticker_revolve:
                startActivityForResult(new Intent(this,RevolveActivity.class).
                        putExtra(RevolveActivity.FILE_PATH,filePath),REQUEST_REVOLVE_CODE);
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
            case R.id.activity_sticker_eraser:
                //开启橡皮檫
                //第一步:去掉边框,仅仅保留bitmap  第二步:使用画笔画(橡皮檫模式)
                mStickerView.openEraserMode();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_REVOLVE_CODE && data != null) {
            byte[] bis = data.getByteArrayExtra("imgbitmap");
            mMainBitmap = BitmapFactory.decodeByteArray(bis,0,bis.length);
            mImageView.setImageBitmap(mMainBitmap);
        }
    }
}
