package com.goertek.aitutu.camera.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.goertek.aitutu.camera.face.Face;
import com.goertek.aitutu.camera.face.FaceTrack;
import com.goertek.aitutu.camera.filter.BeautyFilter;
import com.goertek.aitutu.camera.filter.BigEyeFilter;
import com.goertek.aitutu.camera.filter.CameraFilter;
import com.goertek.aitutu.camera.filter.ScreenFilter;
import com.goertek.aitutu.camera.filter.StickFilter;
import com.goertek.aitutu.camera.record.MediaRecorder;
import com.goertek.aitutu.camera.util.CameraHelper;
import com.goertek.aitutu.camera.util.CameraParam;
import com.goertek.aitutu.camera.util.EglSurfaceBase;
import com.goertek.aitutu.camera.util.OpenGLUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener, Camera.PreviewCallback {
    private ScreenFilter mScreenFilter;
    private CameraGLSurfaceView mView;
    private CameraHelper mCameraHelper;
    private SurfaceTexture mSurfaceTexture;
    private float[] mtx = new float[16];
    private int[] mTextures;
    private CameraFilter mCameraFilter;
    private MediaRecorder mMediaRecorder;
    private FaceTrack mFaceTrack;
    private BigEyeFilter mBigEyeFilter;
    private StickFilter mStickFilter;
    private int mHeigh;
    private int mWidth;
    private BeautyFilter mBeautyFilter;
    private MediaRecorder.OnRecordFinishListener mListener;

    public CameraRenderer(CameraGLSurfaceView cameraGLSurfaceView) {
        mView = cameraGLSurfaceView;
        Context context = mView.getContext();
        //拷贝 模型
        OpenGLUtils.copyAssets2SdCard(context, "lbpcascade_frontalface.xml",
                "/sdcard/lbpcascade_frontalface.xml");
        OpenGLUtils.copyAssets2SdCard(context, "seeta_fa_v1.1.bin",
                "/sdcard/seeta_fa_v1.1.bin");
    }

    /**
     * 画布创建好啦
     *
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        //初始化的操作
        mCameraHelper = new CameraHelper(Camera.CameraInfo.CAMERA_FACING_BACK);
        mCameraHelper.setPreviewCallback(this);
        //准备好摄像头绘制的画布
        //通过opengl创建一个纹理id
        mTextures = new int[1];
        //偷懒 这里可以不配置 （当然 配置了也可以）
        GLES20.glGenTextures(mTextures.length, mTextures, 0);
        mSurfaceTexture = new SurfaceTexture(mTextures[0]);
        //
        mSurfaceTexture.setOnFrameAvailableListener(this);
        //注意：必须在gl线程操作opengl
        mCameraFilter = new CameraFilter(mView.getContext());
        mScreenFilter = new ScreenFilter(mView.getContext());


        //渲染线程的EGL上下文
        EGLContext eglContext = EGL14.eglGetCurrentContext();
        mMediaRecorder = new MediaRecorder(mView.getContext(), "/sdcard/a.mp4", CameraHelper.HEIGHT, CameraHelper.WIDTH, eglContext);
        mMediaRecorder.setOnRecordFinishListener(mListener);
    }

    /**
     * 画布发生了改变
     *
     * @param gl
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeigh = height;
        // 创建跟踪器
        mFaceTrack = new FaceTrack("/sdcard/lbpcascade_frontalface.xml",
                "/sdcard/seeta_fa_v1.1.bin", mCameraHelper);
        //启动跟踪器
        mFaceTrack.startTrack();
        //开启预览
        mCameraHelper.startPreview(mSurfaceTexture);
        mCameraFilter.onReady(width, height);
        mScreenFilter.onReady(width, height);
    }

    /**
     * 开始画画吧
     *
     * @param gl
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        // 配置屏幕
        //清理屏幕 :告诉opengl 需要把屏幕清理成什么颜色
        GLES20.glClearColor(0, 0, 0, 0);
        //执行上一个：glClearColor配置的屏幕颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // 把摄像头的数据先输出来
        // 更新纹理，然后我们才能够使用opengl从SurfaceTexure当中获得数据 进行渲染
        mSurfaceTexture.updateTexImage();
        //surfaceTexture 比较特殊，在opengl当中 使用的是特殊的采样器 samplerExternalOES （不是sampler2D）
        //获得变换矩阵
        mSurfaceTexture.getTransformMatrix(mtx);
        //
        mCameraFilter.setMatrix(mtx);
        //责任链
        int id = mCameraFilter.onDrawFrame(mTextures[0]);
        //加效果滤镜
        // id  = 效果1.onDrawFrame(id);
        // id = 效果2.onDrawFrame(id);
        //....
        Face face = mFaceTrack.getFace();
        if (null != mBigEyeFilter) {
            mBigEyeFilter.setFace(face);
            id = mBigEyeFilter.onDrawFrame(id);
        }
        // 贴纸
        if (null != mStickFilter) {
            mStickFilter.setFace(face);
            id = mStickFilter.onDrawFrame(id);
        }
        if (null != mBeautyFilter){
            id = mBeautyFilter.onDrawFrame(id);
        }
        //加完之后再显示到屏幕中去
        mScreenFilter.onDrawFrame(id);
        //进行录制
        mMediaRecorder.encodeFrame(id, mSurfaceTexture.getTimestamp());
        EglSurfaceBase eglSurfaceBase = new EglSurfaceBase();
        if (CameraParam.getInstance().isTakePicture) {
            int width = mView.getWidth();
            int height = mView.getHeight();
            ByteBuffer buf = ByteBuffer.allocateDirect(width * height * 4);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            GLES30.glReadPixels(0, 0, width, height,
                    GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buf);
            eglSurfaceBase.checkGlError("glReadPixels");
            buf.rewind();
            CameraParam.getInstance().isTakePicture = false;
            if (mCaptureCallback != null) {
                mCaptureCallback.onCapture(buf, width, height);
            }
        }
    }

    CaptureCallback mCaptureCallback;
    /**
     * 截屏回调
     * @param captureCallback
     */
    public void setCaptureCallback(CaptureCallback captureCallback) {
        this.mCaptureCallback = captureCallback;
    }
    /**
     * 截帧回调
     */
    public interface CaptureCallback {
        void onCapture(ByteBuffer buffer, int width, int height);
    }

    /**
     * surfaceTexture 有一个有效的新数据的时候回调
     *
     * @param surfaceTexture
     */
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mView.requestRender();
    }

    public void onSurfaceDestroyed() {
        mCameraHelper.stopPreview();
        mFaceTrack.stopTrack();
    }

    public void startRecord(float speed) {
        try {
            mMediaRecorder.start(speed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        mMediaRecorder.stop();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // data 送去进行人脸定位 与 关键点定位
        if (null != mBigEyeFilter || null != mStickFilter){
            mFaceTrack.detector(data);
        }
    }

    public void enableBeauty(final boolean isChecked) {
        //向GL线程发布一个任务
        //任务会放入一个任务队列， 并在gl线程中去执行
        mView.queueEvent(new Runnable() {
            @Override
            public void run() {
                //Opengl线程
                if (isChecked) {
                    mBeautyFilter = new BeautyFilter(mView.getContext());
                    mBeautyFilter.onReady(mWidth,mHeigh);
                } else {
                    mBeautyFilter.release();
                    mBeautyFilter = null;
                }
            }
        });
    }

    public void enableBigEye(final boolean isChecked) {
        //向GL线程发布一个任务
        //任务会放入一个任务队列， 并在gl线程中去执行
        mView.queueEvent(new Runnable() {
            @Override
            public void run() {
                //Opengl线程
                if (isChecked) {
                    mBigEyeFilter = new BigEyeFilter(mView.getContext());
                    mBigEyeFilter.onReady(mWidth,mHeigh);
                } else {
                    mBigEyeFilter.release();
                    mBigEyeFilter = null;
                }
            }
        });
    }

    public void enableStick(final boolean isChecked) {
        //向GL线程发布一个任务
        //任务会放入一个任务队列， 并在gl线程中去执行
        mView.queueEvent(new Runnable() {
            @Override
            public void run() {
                //Opengl线程
                if (isChecked) {
                    mStickFilter = new StickFilter(mView.getContext());
                    mStickFilter.onReady(mWidth,mHeigh);
                } else {
                    mStickFilter.release();
                    mStickFilter = null;
                }
            }
        });
    }

    public void switchCamera() {
        mCameraHelper.switchCamera();
    }

    public void setOnRecordFinishListener(MediaRecorder.OnRecordFinishListener listener){
        if (null != mMediaRecorder){
            mMediaRecorder.setOnRecordFinishListener(listener);
        }
        mListener = listener;
    }


    public Camera getCamera() {
        return mCameraHelper.getCamera();
    }
}
