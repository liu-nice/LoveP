/*
 * Copyright (c) 2019 -Goertek -All rights reserved.
 */
package com.goertek.aitutu.mvp.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goertek.aitutu.R;
import com.goertek.aitutu.util.BitmapUtils;
import com.goertek.aitutu.util.PhotoUtils;
import com.goertek.arm.base.BaseActivity;
import com.goertek.arm.di.component.AppComponent;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * description:RevolveActivity is revolveImage
 *
 * @author weipeng
 * @version 1.0
 */
public class RevolveActivity extends BaseActivity {

    /**
     * 旋转90度
     */
    private static final int DEGREES_90 = 90;

    /**
     * 旋转-90度
     */
    private static final int DEGREES_FU_90 = -90;

    public static final String FILE_PATH = "file_path";

    /**
     * Toolbar
     */
    @BindView(R.id.activity_edit_revolve_toolbar)
    Toolbar mToolbar;
    /**
     * 编辑图片
     */
    @BindView(R.id.activity_edit_image)
    public ImageView mEditImage;

    /**
     * 重置图片
     */
    @BindView(R.id.activity_edit_reset)
    public TextView mEditReset;

    /**
     * 左旋转
     */
    @BindView(R.id.activity_edit_left)
    public ImageView mEditLeft;

    /**
     * 右旋转
     */
    @BindView(R.id.activity_edit_right)
    public ImageView mEditRight;

    /**
     * 左右旋转
     */
    @BindView(R.id.activity_edit_left_right)
    public ImageView mEditLeftRight;

    /**
     * 上下旋转
     */
    @BindView(R.id.activity_edit_top_bottom)
    public ImageView mEditTopBottom;

    /**
     * 原图
     */
    private Bitmap srcBitmap;

    /**
     * 新图
     */
    private Bitmap newBitmap;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_edit_revolve;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initToolbar();
        String filePath = getIntent().getStringExtra(FILE_PATH);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int imageWidth = metrics.widthPixels / 2;
        int imageHeight = metrics.heightPixels / 2;
        srcBitmap = BitmapUtils.getSampledBitmap(filePath,imageWidth,imageHeight);
        newBitmap = srcBitmap;
        mEditImage.setImageBitmap(newBitmap);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initToolbar() {
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
                    Intent intent = getIntent();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    newBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                    byte[] bitmapByte = baos.toByteArray();
                    intent.putExtra("imgbitmap",bitmapByte);
                    setResult(RESULT_OK,intent);
                    finish();
                    break;
            }
            return true;
        }
    };

    /**
     * 图片编辑的事件处理
     *
     * @param view
     */
    @OnClick({R.id.activity_edit_reset,R.id.activity_edit_left,R.id.activity_edit_right,
            R.id.activity_edit_left_right,R.id.activity_edit_top_bottom})
    public void editEvent(View view) {
        switch (view.getId()) {
            case R.id.activity_edit_reset:
                newBitmap = srcBitmap;
                mEditImage.setImageBitmap(newBitmap);
                break;
            case R.id.activity_edit_left:
                newBitmap = PhotoUtils.rotateImage(newBitmap,DEGREES_FU_90);
                mEditImage.setImageBitmap(newBitmap);
                break;
            case R.id.activity_edit_right:
                newBitmap = PhotoUtils.rotateImage(newBitmap,DEGREES_90);
                mEditImage.setImageBitmap(newBitmap);
                break;
            case R.id.activity_edit_left_right:
                newBitmap = PhotoUtils.reverseImage(newBitmap,-1,1);
                mEditImage.setImageBitmap(newBitmap);
                break;
            case R.id.activity_edit_top_bottom:
                newBitmap = PhotoUtils.reverseImage(newBitmap,1,-1);
                mEditImage.setImageBitmap(newBitmap);
                break;
            default:
                break;
        }
    }
}
