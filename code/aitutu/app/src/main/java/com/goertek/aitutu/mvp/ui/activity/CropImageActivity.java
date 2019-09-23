package com.goertek.aitutu.mvp.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.goertek.aitutu.R;
import com.goertek.arm.base.BaseActivity;
import com.goertek.arm.di.component.AppComponent;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.isseiaoki.simplecropview.util.Logger;
import com.isseiaoki.simplecropview.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CropImageActivity extends BaseActivity implements View.OnClickListener {
    /**
     * LogTAG : CropImageActivity
     */
    private static final String TAG = CropImageActivity.class.getSimpleName();
    /**
     * 裁剪图框
     */
    private static final String KEY_FRAME_RECT = "FrameRect";
    /**
     * 裁剪图片uri
     */
    private static final String KEY_SOURCE_URI = "SourceUri";
    /**
     * LogTAG
     */
    private static final int REQUEST_PICK_IMAGE = 10011;
    /**
     * LogTAG
     */
    private static final int REQUEST_SAF_PICK_IMAGE = 10012;
    /**
     * LogTAG
     */
    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "load image success");
        }

        @Override
        public void onError(Throwable error) {
            Log.d(TAG, "load image error :" + error.getMessage());
        }
    };

    /**
     * LogTAG
     */
    private final CropCallback mCropCallback = new CropCallback() {
        @Override
        public void onSuccess(Bitmap cropped) {
            cropImageView.save(cropped)
                    .compressFormat(mCompressFormat)
                    .execute(createSaveUri(), mSaveCallback);
        }

        @Override
        public void onError(Throwable error) {
        }
    };

    /**
     * LogTAG
     */
    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override
        public void onSuccess(Uri outputUri) {
            (CropImageActivity.this).startResultActivity(outputUri);
        }

        @Override
        public void onError(Throwable error) {
        }
    };

    /**
     * LogTAG
     */
    private CropImageView cropImageView;

    /**
     * LogTAG
     */
    private RectF mFrameRect;
    /**
     * LogTAG
     */

    private Uri mSourceUri;
    /**
     * LogTAG
     */

    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;

    /**
     * LogTAG
     */
    private void initFindView() {
        cropImageView = findViewById(R.id.crop_imageView);
        findViewById(R.id.buttonFitImage).setOnClickListener(this);
        findViewById(R.id.button1_1).setOnClickListener(this);
        findViewById(R.id.button3_4).setOnClickListener(this);
        findViewById(R.id.button4_3).setOnClickListener(this);
        findViewById(R.id.button9_16).setOnClickListener(this);
        findViewById(R.id.button16_9).setOnClickListener(this);
        findViewById(R.id.buttonFree).setOnClickListener(this);
        findViewById(R.id.buttonDone).setOnClickListener(this);
        findViewById(R.id.buttonFitImage).setOnClickListener(this);

        findViewById(R.id.buttonPickImage).setOnClickListener(this);
        findViewById(R.id.buttonRotateLeft).setOnClickListener(this);
        findViewById(R.id.buttonRotateRight).setOnClickListener(this);
        findViewById(R.id.buttonCustom).setOnClickListener(this);
        findViewById(R.id.buttonCircle).setOnClickListener(this);
        findViewById(R.id.buttonShowCircleButCropAsSquare).setOnClickListener(this);
        findViewById(R.id.buttonCustom).setOnClickListener(this);
        findViewById(R.id.buttonCircle).setOnClickListener(this);
        findViewById(R.id.buttonShowCircleButCropAsSquare).setOnClickListener(this);

    }

    /**
     * LogTAG
     */
    public static Uri getUriFromDrawableResId(Context context, int drawableResId) {
        final StringBuilder builder = new StringBuilder().append(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .append("://")
                .append(context.getResources().getResourcePackageName(drawableResId))
                .append("/")
                .append(context.getResources().getResourceTypeName(drawableResId))
                .append("/")
                .append(context.getResources().getResourceEntryName(drawableResId));
        return Uri.parse(builder.toString());
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.buttonFitImage:
                cropImageView.setCropMode(CropImageView.CropMode.FIT_IMAGE);
                break;
            case R.id.button1_1:
                cropImageView.setCropMode(CropImageView.CropMode.SQUARE);
                break;
            case R.id.button3_4:
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_3_4);
                break;
            case R.id.button4_3:
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_4_3);
                break;
            case R.id.button9_16:
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_9_16);
                break;
            case R.id.button16_9:
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_16_9);
                break;
            case R.id.buttonCustom:
                cropImageView.setCustomRatio(1, 1);
                break;
            case R.id.buttonFree:
                cropImageView.setCropMode(CropImageView.CropMode.FREE);
                break;
            case R.id.buttonCircle:
                cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
                break;
            case R.id.buttonShowCircleButCropAsSquare:
                cropImageView.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE);
                break;
            case R.id.buttonRotateLeft:
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                break;
            case R.id.buttonRotateRight:
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                break;
            case R.id.buttonPickImage:
                if (ContextCompat.checkSelfPermission(CropImageActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //权限还没有授予，需要在这里写申请权限的代码
                    // 第二个参数是一个字符串数组，里面是你需要申请的权限 可以设置申请多个权限
                    // 最后一个参数是标志你这次申请的权限，该常量在onRequestPermissionsResult中使用到
                    ActivityCompat.requestPermissions(CropImageActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);

                } else {
                    //权限已经被授予，在这里直接写要执行的相应方法即可
                    pickImage();
                }

                break;
            case R.id.buttonDone:
                cropImage();
                break;

            default:
                break;

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save data
        outState.putParcelable(KEY_FRAME_RECT, cropImageView.getActualCropRect());
        outState.putParcelable(KEY_SOURCE_URI, cropImageView.getSourceUri());
    }

    /**
     * LogTAG
     */
    public void cropImage() {
        cropImageView.crop(mSourceUri).execute(mCropCallback);
    }

    /**
     * LogTAG
     */

    public Uri createSaveUri() {
        return createNewUri(this, mCompressFormat);
    }

    /**
     * LogTAG
     */
    public static String getDirPath() {
        String dirPath = "";
        File imageDir = null;
        final File extStorageDir = Environment.getExternalStorageDirectory();
        if (extStorageDir.canWrite()) {
            imageDir = new File(extStorageDir.getPath() + "/simplecropview");
        }
        if (imageDir != null) {
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            if (imageDir.canWrite()) {
                dirPath = imageDir.getPath();
            }
        }
        return dirPath;
    }

    /**
     * LogTAG
     */
    public static Uri createNewUri(Context context, Bitmap.CompressFormat format) {
        final long currentTimeMillis = System.currentTimeMillis();
        final Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);
        final String dirPath = getDirPath();
        final String fileName = "scv" + title + "." + getMimeType(format);
        final String path = dirPath + "/" + fileName;

        final ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(format));
        values.put(MediaStore.Images.Media.DATA, path);
        final long time = currentTimeMillis / 1000;
        values.put(MediaStore.MediaColumns.DATE_ADDED, time);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);
        final File file = new File(path);
        if (file.exists()) {
            values.put(MediaStore.Images.Media.SIZE, file.length());
        }

        final ContentResolver resolver = context.getContentResolver();
        final Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Logger.i("SaveUri = " + uri);
        return uri;
    }

    /**
     * LogTAG
     */
    public static String getMimeType(Bitmap.CompressFormat format) {
        Logger.i("getMimeType CompressFormat = " + format);
        switch (format) {
            case JPEG:
                return "jpeg";
            case PNG:
                return "png";
            default:
                break;
        }
        return "png";
    }

    /**
     * LogTAG
     */
    public void startResultActivity(Uri uri) {
        if (isFinishing()) return;
        // Start ResultActivity
        startActivity(ResultActivity.createIntent(this, uri));
    }

    /**
     * LogTAG
     */
    public void pickImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                    REQUEST_PICK_IMAGE);
        } else {
            final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_SAF_PICK_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == Activity.RESULT_OK) {
            // reset frame rect
            mFrameRect = null;
            switch (requestCode) {
                case REQUEST_PICK_IMAGE:
                    mSourceUri = result.getData();
                    cropImageView.load(mSourceUri)
                            .initialFrameRect(mFrameRect)
                            .useThumbnail(true)
                            .execute(mLoadCallback);
                    break;
                case REQUEST_SAF_PICK_IMAGE:
                    mSourceUri = Utils.ensureUriPermission(this, result);
                    cropImageView.load(mSourceUri)
                            .initialFrameRect(mFrameRect)
                            .useThumbnail(true)
                            .execute(mLoadCallback);
                    break;
                default:

            }
        }
    }

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_crop_image;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // restore data
            mFrameRect = savedInstanceState.getParcelable(KEY_FRAME_RECT);
            mSourceUri = savedInstanceState.getParcelable(KEY_SOURCE_URI);
        }
        initFindView();
        // load image
        mSourceUri = getUriFromDrawableResId(this, R.drawable.sample5);
        cropImageView.load(mSourceUri)
                .initialFrameRect(mFrameRect)
                .useThumbnail(true)
                .execute(mLoadCallback);
    }
}
