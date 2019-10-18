/*
 * Copyright  2016 - Goertek- All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.goertek.aitutu.R;
import com.goertek.arm.base.BaseActivity;
import com.goertek.arm.di.component.AppComponent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

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

import static com.goertek.aitutu.camera.util.FileUtil.getCurrentPicName;

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
    @BindView(R.id.pick_images)
    ImageButton pickImage;
    @BindView(R.id.save_picture)
    ImageButton savePicture;

    /**
     * 图片路径"image/*"
     */
    private static final String IMAGE_PATH_ALL = "image/*";

    /**
     * 选择图片常量 requestCode
     */
    private static final int REQUEST_PICK_IMAGE = 10011;

    /**
     * 保存图片常量 requestCode
     */
    private static final int REQUEST_SAF_PICK_IMAGE = 10012;


    private Bitmap bitmap = null;

    private GPUImageFilter[] arr = {new GPUImageGaussianBlurFilter(), new GPUImageSharpenFilter(),
            new GPUImageSketchFilter(), new GPUImageDissolveBlendFilter(), new GPUImageSoftLightBlendFilter(),
            new GPUImage3x3ConvolutionFilter(), new GPUImageSphereRefractionFilter(), new GPUImageToonFilter(),
            new GPUImageAlphaBlendFilter(), new GPUImageKuwaharaFilter()};
    private GPUImage gpuImage;

    private String[] texts = {"模糊", "锐化", "素描", "溶解", "柔光", "卷积", "折射", "卡通", "透明", "绿波"};

    @OnClick({R.id.save_picture, R.id.pick_images})
    public void editFilter(View view) {
        switch (view.getId()) {
            case R.id.save_picture:
                // 保存图片
                saveBitmap(bitmap);
                break;
            case R.id.pick_images:
                //打开图库
                pickImage();
                break;
            default:
                break;
        }
    }

    private void saveBitmap(Bitmap bitmap) {

        String picName = getCurrentPicName();
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中
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

    /**
     * 选择进入图库
     */
    public void pickImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType(IMAGE_PATH_ALL),
                    REQUEST_PICK_IMAGE);
        } else {
            final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(IMAGE_PATH_ALL);
            startActivityForResult(intent, REQUEST_SAF_PICK_IMAGE);
        }
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
        List<AppCompatTextView> mList = new ArrayList();
        for (int i = 0; i < 10; i++) {

            AppCompatTextView mTextView = new AppCompatTextView(this);
            mTextView.setId(i);
            mTextView.setBackgroundResource(R.drawable.cicrcle);
            mTextView.setOnClickListener(new MyListener(i));
            mTextView.setText(texts[i]);
            mTextView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //为小圆点左右添加间距
            params.leftMargin = 20;
            params.rightMargin = 20;
            llayoutContioner.addView(mTextView, params);
            mList.add(mTextView);
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

    private void openNativeImage(String filepath) {

        bitmap = BitmapFactory.decodeFile(filepath);
        imageView.setImageBitmap(bitmap);
        gpuImage = new GPUImage(FilterActivity.this);
        gpuImage.setImage(bitmap);
        Log.e("FilterActivity", "3" + (bitmap == null));

    }
}
