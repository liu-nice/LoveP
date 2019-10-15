/*
 * Copyright  2016 - Goertek- All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.goertek.aitutu.R;
import com.goertek.arm.base.BaseActivity;
import com.goertek.arm.di.component.AppComponent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImage3x3ConvolutionFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageAddBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageAlphaBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageDivideBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter;

/**
 * description:FilterActivity  is saveImage
 *
 * @author libin
 * @version 1.0
 */

public class FilterActivity extends BaseActivity {


    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.llayout_contioner)
    LinearLayout llayoutContioner;
    @BindView(R.id.tab_bar)
    HorizontalScrollView tabBar;
    @BindView(R.id.pick_image)
    ImageButton pickImage;
    @BindView(R.id.save)
    ImageButton save;


    private Bitmap bitmap = null;

    private GPUImageFilter[] arr = {new GPUImageGrayscaleFilter(), new GPUImage3x3ConvolutionFilter(),
            new GPUImageAddBlendFilter(), new GPUImageAlphaBlendFilter(), new GPUImageBrightnessFilter(),
            new GPUImageDivideBlendFilter()};
    private GPUImage gpuImage;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_filter;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        openNativeImage();
        List<ImageView> mList = new ArrayList();
        for (int i = 0; i < 5; i++) {
            ImageView mImageView = new ImageView(this);
            mImageView.setId(i);
            mImageView.setImageResource(R.drawable.cicrcle);

            mImageView.setOnClickListener(new MyListener(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //为小圆点左右添加间距
            params.leftMargin = 20;
            params.rightMargin = 20;
            llayoutContioner.addView(mImageView, params);
            mList.add(mImageView);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private class MyListener implements View.OnClickListener {
        private final int position;

        public MyListener(int i) {
            this.position = i;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();

            gpuImage.setFilter(arr[id]);
            bitmap = gpuImage.getBitmapWithFilterApplied();
            //显示处理后的图片
            imageView.setImageBitmap(bitmap);

        }

    }

    private void openNativeImage() {

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample);
        imageView.setImageBitmap(bitmap);
        gpuImage = new GPUImage(FilterActivity.this);
        gpuImage.setImage(bitmap);


    }

}
