package com.goertek.aitutu.camera.widget;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public class CameraGLSurfaceView extends GLSurfaceView {

    //默认正常速度
    private Speed mSpeed = Speed.MODE_NORMAL;

    public void switchCamera() {
        cameraRenderer.switchCamera();
    }

    public Camera getCamera() {
        return cameraRenderer.getCamera();
    }


    public enum Speed {
        MODE_EXTRA_SLOW, MODE_SLOW, MODE_NORMAL, MODE_FAST, MODE_EXTRA_FAST
    }


    public CameraRenderer getCameraRenderer() {
        return cameraRenderer;
    }

    private CameraRenderer cameraRenderer;

    public CameraGLSurfaceView(Context context) {
        this(context, null);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        /**
         * 配置GLSurfaceView
         */
        //设置EGL版本
        setEGLContextClientVersion(2);
        cameraRenderer = new CameraRenderer(this);
        setRenderer(cameraRenderer);
        //设置按需渲染 当我们调用 requestRender 请求GLThread 回调一次 onDrawFrame
        // 连续渲染 就是自动的回调onDrawFrame
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        cameraRenderer.onSurfaceDestroyed();
    }

    public void setSpeed(Speed speed) {
        mSpeed = speed;
    }

    public void enableBeauty(boolean isChecked) {
        cameraRenderer.enableBeauty(isChecked);
    }

    public void enableBigEye(boolean isChecked) {
        cameraRenderer.enableBigEye(isChecked);
    }

    public void enableStick(boolean isChecked) {
        cameraRenderer.enableStick(isChecked);
    }

}
