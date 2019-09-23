package com.goertek.aitutu.app.cropimage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ImageView;

import com.goertek.aitutu.R;
import com.isseiaoki.simplecropview.util.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultActivity extends AppCompatActivity {
    /**
     * LogTAG
     */

    private static final int NUMBER = 2048;
    /**
     * LogTAG
     */

    private static final String TAG = ResultActivity.class.getSimpleName();
    /**
     * LogTAG
     */

    private ImageView mImageView;
    /**
     * LogTAG
     */

    private ExecutorService mExecutor;

    /**
     * LogTAG
     */

    public static Intent createIntent(Activity activity, Uri uri) {
        final Intent intent = new Intent(activity, ResultActivity.class);
        intent.setData(uri);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initToolbar();

        mImageView = findViewById(R.id.result_image);
        mExecutor = Executors.newSingleThreadExecutor();

        final Uri uri = getIntent().getData();
        mExecutor.submit(new LoadScaledImageTask(this, uri, mImageView, calcImageSize()));
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
     * LogTAG
     */

    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    /**
     * LogTAG
     */
    private int calcImageSize() {
        final DisplayMetrics metrics = new DisplayMetrics();
        final Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        return Math.min(Math.max(metrics.widthPixels, metrics.heightPixels), NUMBER);
    }

    /**
     * LogTAG
     */
    public static class LoadScaledImageTask implements Runnable {
        /**
         * LogTAG
         */
        private Uri uri;
        /**
         * LogTAG
         */

        private int width;
        /**
         * LogTAG
         */

        private Context context;
        /**
         * LogTAG
         */
        private ImageView imageView;
        /**
         * LogTAG
         */
        private Handler mHandler = new Handler(Looper.getMainLooper());
        /**
         * LogTAG
         */
        public LoadScaledImageTask(Context context, Uri uri, ImageView imageView, int width) {
            this.context = context;
            this.uri = uri;
            this.imageView = imageView;
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
                    imageView.setImageMatrix(Utils.getMatrixFromExifOrientation(exifRotation));
                    imageView.setImageBitmap(sampledBitmap);
                });
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
