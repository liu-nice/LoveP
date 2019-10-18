/*
 * Copyright  2016 - Goertek- All rights reserved.
 */
package com.goertek.aitutu.camera.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;


/**
 * describition :openGL视图
 *
 * @author ;falzy.ning
 * @version :1.0.0
 * @since : 2019/10/9 14:51
 */
public class CameraGLSurfaceView extends GLSurfaceView {
    //相机渲染
    private CameraRenderer cameraRenderer;

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        cameraRenderer.switchCamera();
    }

    /**
     * 返回渲染器
     * @return render
     */
    public CameraRenderer getCameraRenderer() {
        return cameraRenderer;
    }

    /**
     * 构造器
     * @param context 上下文
     */
    public CameraGLSurfaceView(Context context) {
        this(context, null);
    }

    /**
     * 构造器
     * @param context 上下文
     * @param attrs 属性
     */
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

    /**
     * 美颜
     * @param isChecked 选中状态
     */
    public void enableBeauty(boolean isChecked) {
        cameraRenderer.enableBeauty(isChecked);
    }

    /**
     * 大眼
     * @param isChecked 选中状态
     */
    public void enableBigEye(boolean isChecked) {
        cameraRenderer.enableBigEye(isChecked);
    }

    /**
     * 贴图
     * @param isChecked 选中状态
     */
    public void enableStick(boolean isChecked) {
        cameraRenderer.enableStick(isChecked);
    }

    /**
     * 灰度
     * @param isChecked 选中状态
     */
    public void enableGrayScale(boolean isChecked) {
        cameraRenderer.enableGrayScale(isChecked);
    }
}
