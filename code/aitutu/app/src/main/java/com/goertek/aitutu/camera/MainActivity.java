package com.goertek.aitutu.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.goertek.aitutu.R;
import com.goertek.aitutu.camera.record.MediaRecorder;
import com.goertek.aitutu.camera.video.VideoActivity;
import com.goertek.aitutu.camera.widget.CameraGLSurfaceView;
import com.goertek.aitutu.camera.widget.RecordButton;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    CameraGLSurfaceView cameraGLSurfaceView;

    private static final int PERMISSION_CAMERA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        setContentView(R.layout.activity_main);
        cameraGLSurfaceView = findViewById(R.id.douyinView);
        RecordButton recordButton = findViewById(R.id.btn_record);
        recordButton.setOnRecordListener(new RecordButton.OnRecordListener() {
            /**
             * 开始录制
             */
            @Override
            public void onRecordStart() {
                cameraGLSurfaceView.startRecord();
            }

            /**
             * 停止录制
             */
            @Override
            public void onRecordStop() {
                cameraGLSurfaceView.stopRecord();
            }
        });
        RadioGroup radioGroup = findViewById(R.id.rg_speed);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            /**
             * 选择录制模式
             * @param group
             * @param checkedId
             */
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_extra_slow: //极慢
                        cameraGLSurfaceView.setSpeed(CameraGLSurfaceView.Speed.MODE_EXTRA_SLOW);
                        break;
                    case R.id.rb_slow:
                        cameraGLSurfaceView.setSpeed(CameraGLSurfaceView.Speed.MODE_SLOW);
                        break;
                    case R.id.rb_normal:
                        cameraGLSurfaceView.setSpeed(CameraGLSurfaceView.Speed.MODE_NORMAL);
                        break;
                    case R.id.rb_fast:
                        cameraGLSurfaceView.setSpeed(CameraGLSurfaceView.Speed.MODE_FAST);
                        break;
                    case R.id.rb_extra_fast: //极快
                        cameraGLSurfaceView.setSpeed(CameraGLSurfaceView.Speed.MODE_EXTRA_FAST);
                        break;
                }
            }
        });

        ((CheckBox) findViewById(R.id.beauty)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cameraGLSurfaceView.enableBeauty(isChecked);
            }
        });

        ((CheckBox) findViewById(R.id.bigEye)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cameraGLSurfaceView.enableBigEye(isChecked);
            }
        });
        ((CheckBox) findViewById(R.id.stick)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cameraGLSurfaceView.enableStick(isChecked);
            }
        });

        /**
         * 录制完成
         */
        cameraGLSurfaceView.setOnRecordFinishListener(new MediaRecorder.OnRecordFinishListener() {
            @Override
            public void onRecordFinish(final String path) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                        intent.putExtra("path", path);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            cameraGLSurfaceView.switchCamera();
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
}
