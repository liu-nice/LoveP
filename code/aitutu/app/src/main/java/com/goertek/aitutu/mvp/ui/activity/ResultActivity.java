package com.goertek.aitutu.mvp.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ImageView;

import com.goertek.aitutu.R;
import com.goertek.arm.base.BaseActivity;
import com.goertek.arm.di.component.AppComponent;
import com.isseiaoki.simplecropview.util.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * description:ResultActivity is save cropPicture activity
 *
 * @author libin
 * @version 1.0
 */

public class ResultActivity extends BaseActivity {
    /**
     * 常量
     */
    private static final int NUMBER = 2048;
    /**
     * TAG
     */
    private static final String TAG = ResultActivity.class.getSimpleName();
    /**
     * imageview
     */
    private ImageView mImageView;
    /**
     * mExecuor
     */
    private ExecutorService mExecutor;

    /**
     * 创建intent
     *
     * @param activity activity
     * @param uri      uri
     * @return intent
     */
    public static Intent createIntent(Activity activity, Uri uri) {
        final Intent intent = new Intent(activity, ResultActivity.class);
        intent.setData(uri);
        return intent;
    }

    @Override
    protected void onDestroy() {
        mExecutor.shutdown();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    /**
     * 计算图片的尺寸
     *
     * @return int
     */
    private int calcImageSize() {
        final DisplayMetrics metrics = new DisplayMetrics();
        final Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        return Math.min(Math.max(metrics.widthPixels, metrics.heightPixels), NUMBER);
    }

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_result;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        Timber.d(TAG, "initData()");
        initToolbar();

        mImageView = findViewById(R.id.result_image);
        mExecutor = Executors.newSingleThreadExecutor();

        final Uri uri = getIntent().getData();
        mExecutor.submit(new LoadScaledImageTask(this, uri, mImageView, calcImageSize()));

    }

    /**
     * 架子图片处理任务
     */
    public static class LoadScaledImageTask implements Runnable {
        /**
         * uri
         */
        private Uri uri;
        /**
         * width
         */

        private int width;
        /**
         * context
         */

        private Context context;
        /**
         * mImageView
         */
        private ImageView mImageView;
        /**
         * mHandler
         */
        private Handler mHandler = new Handler(Looper.getMainLooper());

        /**
         * 构造方法
         */
        public LoadScaledImageTask(Context context, Uri uri, ImageView imageView, int width) {

            this.context = context;
            this.uri = uri;
            this.mImageView = imageView;
            this.width = width;
        }

        @Override
        public void run() {
            final int exifRotation = Utils.getExifOrientation(context, uri);
            final int maxSize = Utils.getMaxSize();
            final int requestSize = Math.min(width, maxSize);
            try {
                final Bitmap sampledBitmap = Utils.decodeSampledBitmapFromUri(context, uri, requestSize);
                mHandler.post(() -> {
                    mImageView.setImageMatrix(Utils.getMatrixFromExifOrientation(exifRotation));
                    mImageView.setImageBitmap(sampledBitmap);
                });
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
