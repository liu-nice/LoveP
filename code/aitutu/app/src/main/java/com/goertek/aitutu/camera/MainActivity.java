/*
 * Copyright  2016 - Goertek- All rights reserved.
 */
package com.goertek.aitutu.camera;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.goertek.aitutu.R;
import com.goertek.aitutu.camera.util.BitmapUtils;
import com.goertek.aitutu.camera.util.CameraParam;
import com.goertek.aitutu.camera.util.FileUtil;
import com.goertek.aitutu.camera.widget.CameraGLSurfaceView;
import com.goertek.aitutu.camera.widget.CameraRenderer;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

/**
 * describition :相机主类
 *
 * @author ;falzy.ning
 * @version :1.0.0
 * @since : 2019/10/9 14:51
 */
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    //相机权限回调码
    private static final int PERMISSION_CAMERA = 0;

    //glview实例
    @BindView(value = R.id.camera_glsurfaceview)
    public CameraGLSurfaceView mCameraGLSurfaceView;

    //消息发送类
    private Handler mHandler;

    //拍照保存的bitmap
    private Bitmap mBitMap;

    //保存照片的包路径
    private String path;

    //照片名字
    private String picName;

    /**
     * 截屏回调
     */
    private CameraRenderer.CaptureCallback mCaptureCallback = new CameraRenderer.CaptureCallback() {
        @Override
        public void onCapture(final ByteBuffer buffer, final int width, final int height) {
            mHandler.post(() -> {
                Toast.makeText(MainActivity.this, "save picture", Toast.LENGTH_SHORT).show();
                path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM
                        + File.separator + "IMG" + File.separator;
                File dir = new File(path);
                if (!dir.exists()) {
                    boolean mk = dir.mkdirs();
                    Timber.i("创建文件%s", String.valueOf(mk));
                }
                picName = FileUtil.getCurrentPicName();
                String filePath = path + picName;
                BitmapUtils.saveBitmap(MainActivity.this, filePath, buffer, width, height);
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mHandler = new Handler(Looper.getMainLooper());
        mCameraGLSurfaceView.getCameraRenderer().setCaptureCallback(mCaptureCallback);
    }

    @OnCheckedChanged(value = {R.id.beauty, R.id.bigEye, R.id.stick})
    void onBindCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.beauty:
                mCameraGLSurfaceView.enableBeauty(isChecked);
                break;
            case R.id.bigEye:
                mCameraGLSurfaceView.enableBigEye(isChecked);
                break;
            case R.id.stick:
                mCameraGLSurfaceView.enableStick(isChecked);
                break;
            default:
                break;
        }
    }

    @OnClick(value = R.id.btn_takepic)
    void onBindClick(View view) {
        switch (view.getId()) {
            case R.id.btn_takepic:
                CameraParam.getInstance().isTakePicture = true;
                Toast.makeText(MainActivity.this, "takepicture", Toast.LENGTH_SHORT).show();
                Timber.i("takepicture");
                mCameraGLSurfaceView.requestRender();
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mCameraGLSurfaceView.switchCamera();
        }
        return super.onTouchEvent(event);
    }

    /**
     * 检查并获取权限
     */
    private void checkPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "申请相机权限和读写SD卡权限", PERMISSION_CAMERA, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Timber.i("onPermissionsGranted: ");
        if (requestCode != PERMISSION_CAMERA) {
            return;
        }
        for (int i = 0; i < perms.size(); i++) {
            if (perms.get(i).equals(Manifest.permission.CAMERA)) {
                Timber.i("onPermissionsGranted: " + "相机权限成功");
            } else if (perms.get(i).equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Timber.i("onPermissionsDenied: " + "读SD权限成功");
            } else if (perms.get(i).equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Timber.i("onPermissionsDenied: " + "写SD权限成功");
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Timber.i("onPermissionsDenied: ");
        if (requestCode != PERMISSION_CAMERA) {
            return;
        }
        for (int i = 0; i < perms.size(); i++) {
            if (perms.get(i).equals(Manifest.permission.CAMERA)) {
                Timber.i("onPermissionsDenied: " + "相机权限拒绝");
            } else if (perms.get(i).equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Timber.i("onPermissionsDenied: " + "读SD权限拒绝");
            } else if (perms.get(i).equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Timber.i("onPermissionsDenied: " + "写SD权限拒绝");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraGLSurfaceView != null) {
            mCameraGLSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraGLSurfaceView != null) {
            mCameraGLSurfaceView.onResume();
        }
    }
}
