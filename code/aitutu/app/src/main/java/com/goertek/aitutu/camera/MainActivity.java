package com.goertek.aitutu.camera;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.goertek.aitutu.R;
import com.goertek.aitutu.camera.record.EGLBase;
import com.goertek.aitutu.camera.util.CameraParam;
import com.goertek.aitutu.camera.util.FileUtil;
import com.goertek.aitutu.camera.widget.CameraGLSurfaceView;
import com.goertek.aitutu.camera.widget.CameraRenderer;
import com.goertek.aitutu.util.BitmapUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.camera_glsurfaceview)
    public CameraGLSurfaceView mCameraGLSurfaceView;

    @BindView(R.id.btn_takepic)
    public AppCompatTextView mTakePicture;

    private static final int PERMISSION_CAMERA = 0;
    private Handler mHandler;
    private EGLBase mEglBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mHandler =new Handler(Looper.getMainLooper());
        ((CheckBox) findViewById(R.id.beauty)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCameraGLSurfaceView.enableBeauty(isChecked);
            }
        });

        ((CheckBox) findViewById(R.id.bigEye)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCameraGLSurfaceView.enableBigEye(isChecked);
            }
        });
        ((CheckBox) findViewById(R.id.stick)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCameraGLSurfaceView.enableStick(isChecked);
            }
        });
        mCameraGLSurfaceView.getCameraRenderer().setCaptureCallback(mCaptureCallback);
    }

    @OnClick(R.id.btn_takepic)
    void onBindClick(View view) {
        switch (view.getId()) {
            case R.id.btn_takepic:
                CameraParam.getInstance().isTakePicture = true;
                Toast.makeText(MainActivity.this, "takepicture", Toast.LENGTH_SHORT).show();
                mCameraGLSurfaceView.requestRender();
//                Camera mCamera = mCameraGLSurfaceView.getCamera();
//                mCamera.takePicture(null, null, (data, camera) -> takePicture(data));
                break;
            default:
                break;
        }
    }

    /**
     * 截屏回调
     */
    private CameraRenderer.CaptureCallback mCaptureCallback = new CameraRenderer.CaptureCallback() {
        @Override
        public void onCapture(final ByteBuffer buffer, final int width, final int height) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM
                            + File.separator + "IMG" + File.separator;
                    picName = FileUtil.getCurrentPicName();
                    String filePath = path + picName;
                    BitmapUtils.saveBitmap(filePath, buffer, width, height);
                }
            });
        }
    };


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

    private Bitmap mBitMap;
    private String path, picName;

    public void takePicture(byte[] data) {
        Toast.makeText(MainActivity.this, data.length + "", Toast.LENGTH_SHORT).show();
        mBitMap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap bMapRotate;
        Matrix matrix = new Matrix();
        matrix.reset();
//        matrix.postRotate(90);
        bMapRotate = Bitmap.createBitmap(mBitMap, 0, 0, mBitMap.getWidth(),
                mBitMap.getHeight(), matrix, true);
        mBitMap = bMapRotate;
        savePic();
    }

    private void savePic() {
        Thread thread = new Thread(() -> {
            try {
                path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM
                        + File.separator + "IMG" + File.separator;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();// 创建文件夹
                }
                picName = FileUtil.getCurrentPicName();
                String fileName = path + picName;
                File file = new File(fileName);
                BufferedOutputStream bos =
                        new BufferedOutputStream(new FileOutputStream(file));
                mBitMap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中
                bos.flush();//输出
                bos.close();//关闭
                //通知相册更新
                MediaStore.Images.Media.insertImage(getContentResolver(),
                        mBitMap, fileName, null);
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                sendBroadcast(intent);
                Toast.makeText(MainActivity.this, "save pic", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Timber.e(e.getMessage().toString());
            }
//                handler.sendEmptyMessage(SUCCESS);
        });
        // 启动存储照片的线程
        thread.start();
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
