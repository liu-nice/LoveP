/*
 * Copyright  2016 - Goertek- All rights reserved.
 */

package com.goertek.aitutu.camera.util;

import android.annotation.SuppressLint;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import timber.log.Timber;

/**
 * describition :
 *
 * @author ;falzy.ning
 * @version :1.0.0
 * @since : 2019/10/8 11:13
 */
public class EglSurfaceBase {
    private static final String TAG = "EglSurfaceBase";
    // 更新帧的锁
    private final Object mSyncFence = new Object();

    /**
     * 拍照
     */
    void takePicture() {
        synchronized (mSyncFence) {
            ByteBuffer buffer = getCurrentFrame();
//            onCapture(buffer, getWidth(), getHeight());
            CameraParam.getInstance().isTakePicture = false;
        }
    }


    /**
     * 获取当前帧的缓冲
     * @return
     */
    public ByteBuffer getCurrentFrame() {
        int width = getWidth();
        int height = getHeight();
        ByteBuffer buf = ByteBuffer.allocateDirect(width * height * 4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        GLES30.glReadPixels(0, 0, width, height,
                GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buf);
        checkGlError("glReadPixels");
        buf.rewind();
        return buf;
    }

    private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;
    private int mWidth = -1;
    private int mHeight = -1;
    private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    public int getWidth() {
        if (mWidth < 0) {
            return querySurface(mEGLSurface, EGL14.EGL_WIDTH);
        } else {
            return mWidth;
        }
    }

    /**
     * Performs a simple surface query.
     */
    public int querySurface(EGLSurface eglSurface, int what) {
        int[] value = new int[1];
        EGL14.eglQuerySurface(mEGLDisplay, eglSurface, what, value, 0);
        return value[0];
    }

    public int getHeight() {
        if (mHeight < 0) {
            return querySurface(mEGLSurface, EGL14.EGL_HEIGHT);
        } else {
            return mHeight;
        }
    }

    /**
     * 检查是否出错
     * @param op
     */
    @SuppressLint("TimberArgCount")
    public static void checkGlError(String op) {
        int error = GLES30.glGetError();
        if (error != GLES30.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Timber.e(TAG, msg);
//            throw new RuntimeException(msg);
        }
    }
}
