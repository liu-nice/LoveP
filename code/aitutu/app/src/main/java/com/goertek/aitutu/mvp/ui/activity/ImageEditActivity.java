/*
 * Copyright (c) 2019 -Goertek -All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.goertek.aitutu.R;
import com.goertek.aitutu.mvp.ui.custom.CustomViewPager;
import com.goertek.aitutu.mvp.ui.custom.StickerView;
import com.goertek.aitutu.mvp.ui.custom.imagezoom.ImageViewTouch;
import com.goertek.aitutu.mvp.ui.custom.imagezoom.ImageViewTouchBase;
import com.goertek.aitutu.mvp.ui.fragment.MainMenuFragment;
import com.goertek.aitutu.mvp.ui.fragment.StickerFragment;
import com.goertek.aitutu.util.BitmapUtils;
import com.goertek.aitutu.util.StringUtils;
import com.goertek.arm.base.BaseActivity;
import com.goertek.arm.di.component.AppComponent;

import butterknife.BindView;

public class ImageEditActivity extends BaseActivity {

    public static final String FILE_PATH = "file_path";
    public static final String EXTRA_OUTPUT = "extra_output";
    public static final int EXTRA_REVOLVE = 0x0011;

    @BindView(R.id.activity_edit_image_titlebar)
    Toolbar mToolbar;

    @BindView(R.id.activity_edit_image_viewpager)
    public CustomViewPager mBottomViewPager;

    private BottomPagerAdapter mBottomPagerAdapter;

    //贴图层View
    @BindView(R.id.activity_edit_image_fl_sticker)
    public StickerView mStickerView;
    //拖拽bitmap
    @BindView(R.id.activity_edit_image_fl_main)
    public ImageViewTouch mImageViewTouch;
    //底部显示的Bitmap
    private Bitmap mMainBitmap;
    //主菜单fragment
    private MainMenuFragment mMainMenuFragment;
    //贴纸fragment
    public StickerFragment mStickerFragment;

    public static final int MODE_NONE = 0;
    //贴图模式
    public static final int MODE_STICKERS = 1;
    //当前操作模式
    public int mode = MODE_NONE;
    //需要编辑图片路径
    public String filePath;
    //生成的新图片路径
    public String saveFilePath;
    // 展示图片控件宽、高
    private int imageWidth, imageHeight;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_image_edit;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initToobar();
        initFragment();
        getBitmapFormFile();
        initBottomPagerAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                    mStickerFragment.applyStickers();
                    break;
            }
            return true;
        }
    };

    private void initFragment() {
        mMainMenuFragment = MainMenuFragment.newInstance();
        mStickerFragment = StickerFragment.newInstance();
    }

    private void getBitmapFormFile() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels / 2;
        imageHeight = metrics.heightPixels / 2;
        //拍照或者选择某一张相册图片,目前先写死
        filePath = getIntent().getStringExtra(FILE_PATH);
        mMainBitmap = BitmapUtils.getSampledBitmap(filePath,imageWidth,imageHeight);
        mImageViewTouch.setImageBitmap(mMainBitmap);
        mImageViewTouch.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
    }

    public void changeMainBitmap(Bitmap newBitmap) {
        mMainBitmap = newBitmap;
        mImageViewTouch.setImageBitmap(mMainBitmap);
        mImageViewTouch.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        new SavePicToFileTask().execute(newBitmap);
    }

    /**
     * 保存处理后的图片
     */
    private class SavePicToFileTask extends AsyncTask<Bitmap, Void, String> {
        Bitmap bitmap;

        @Override
        protected String doInBackground(Bitmap... params) {
            String fileName = null;
//            try {
//                bitmap = params[0];
//                String picName = FileUtil.getCurrentPicName();
////                fileName = ImageUtils.saveToFile(FileUtils.getInstance().getPhotoSavedPath() + "/" + picName,false,bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e("weip","图片处理错误，请退出相机并重试");
//            }
            return fileName;
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

    private void initBottomPagerAdapter() {
        mBottomPagerAdapter = new BottomPagerAdapter(getSupportFragmentManager());
        mBottomViewPager.setAdapter(mBottomPagerAdapter);
    }

    private final class BottomPagerAdapter extends FragmentPagerAdapter {

        public BottomPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case MainMenuFragment.INDEX:// 主菜单
                    return mMainMenuFragment;
                case StickerFragment.INDEX:// 贴图
                    return mStickerFragment;
            }
            return MainMenuFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public Bitmap getMainBit() {
        return mMainBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        byte[] bytes = data.getByteArrayExtra("imgbitmap");
        mMainBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        mImageViewTouch.setImageBitmap(mMainBitmap);
    }
}
