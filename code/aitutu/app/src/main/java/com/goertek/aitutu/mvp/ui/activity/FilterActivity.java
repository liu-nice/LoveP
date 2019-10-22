/*
 * Copyright  2016 - Goertek- All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goertek.aitutu.R;
import com.goertek.aitutu.camera.util.FileUtil;
import com.goertek.arm.base.BaseActivity;
import com.goertek.arm.di.component.AppComponent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImage3x3ConvolutionFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageAlphaBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageDissolveBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageKuwaharaFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSoftLightBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSphereRefractionFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToonFilter;


/**
 * description:FilterActivity  is saveImage
 *
 * @author libin
 * @version 1.0
 */

public class FilterActivity extends BaseActivity {


    @BindView(R.id.pre_image_view)
    ImageView imageView;
    @BindView(R.id.hor_scrollview)
    HorizontalScrollView tabBar;
    @BindView(R.id.pick_images)
    ImageButton pickImage;
    @BindView(R.id.save_picture)
    ImageButton savePicture;
    @BindView(R.id.lLayout_view)
    LinearLayout lLayoutView;

    private Bitmap bitmap = null;

    private GPUImageFilter[] filters = {new GPUImageGaussianBlurFilter(), new GPUImageSharpenFilter(),
            new GPUImageSketchFilter(), new GPUImageDissolveBlendFilter(), new GPUImageSoftLightBlendFilter(),
            new GPUImage3x3ConvolutionFilter(), new GPUImageSphereRefractionFilter(), new GPUImageToonFilter(),
            new GPUImageAlphaBlendFilter(), new GPUImageKuwaharaFilter()};
    private GPUImage gpuImage;

    private String[] imageTexts = {"模糊", "锐化", "素描", "溶解", "柔光", "卷积", "折射", "卡通", "透明", "绿波"};

    @OnClick({R.id.save_picture, R.id.pick_images})
    public void editFilter(View view) {
        switch (view.getId()) {
            case R.id.save_picture:
                // 保存图片
                saveBitmap(bitmap);
                Toast.makeText(this, "图片保存成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pick_images:
                finish();
                break;
            default:
                break;
        }
    }

    private void saveBitmap(Bitmap bitmap) {

        String picName = FileUtil.getCurrentPicName();
        String path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "111/";
        File filepath = new File(path);
        if (!filepath.exists()) {
            filepath.mkdir();
        }
        File file = new File(path + picName);

        FileOutputStream bos = null;
        try {
            bos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        //将图片压缩到流中
        notifyUpdateAlbum(FilterActivity.this, path + picName, bitmap);

    }

    private static void notifyUpdateAlbum(Context context, String fileName, Bitmap mBitMap) {
        MediaStore.Images.Media.insertImage(context.getContentResolver(),
                mBitMap, fileName, null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(fileName));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }


    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_filter;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        String filePath = getIntent().getStringExtra(StickerActivity.FILE_PATH);
        openNativeImage(filePath);

        for (int i = 0; i < 10; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            LinearLayout linearLayout = new LinearLayout(this);
            params.leftMargin = 10;
            params.rightMargin = 10;
            linearLayout.setLayoutParams(params);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            ImageView mImageView = new ImageView(this);
            gpuImage.setFilter(filters[i]);
            bitmap = gpuImage.getBitmapWithFilterApplied();
            Bitmap bitmap1 = ThumbnailUtils.extractThumbnail(this.bitmap, 200, 200);
            mImageView.setImageBitmap(bitmap1);
            linearLayout.setOnClickListener(new MyListener(i));

            TextView mTextView = new TextView(this);
            mTextView.setText(imageTexts[i]);
            mTextView.setGravity(Gravity.CENTER);
            //mTextView.setLayoutParams(params);
            linearLayout.addView(mImageView);
            linearLayout.addView(mTextView);
            lLayoutView.addView(linearLayout, params);

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    private class MyListener implements View.OnClickListener {


        int position;

        public MyListener(int i) {
            this.position = i;
        }

        @Override
        public void onClick(View v) {
            gpuImage.setFilter(filters[position]);
            bitmap = gpuImage.getBitmapWithFilterApplied();
            //显示处理后的图片
            imageView.setImageBitmap(bitmap);
        }

    }

    private void openNativeImage(String filepath) {

        bitmap = BitmapFactory.decodeFile(filepath);
        imageView.setImageBitmap(bitmap);
        gpuImage = new GPUImage(FilterActivity.this);
        gpuImage.setImage(bitmap);


    }
}
