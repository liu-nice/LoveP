/*
 * Copyright  2016 - Goertek- All rights reserved.
 */
package com.goertek.aitutu.camera.util;

/**
 * describition :相机配置参数
 *
 * @author ;falzy.ning
 * @version :1.0.0
 * @since : 2019/10/8 11:42
 */
public final class CameraParam {
    // 是否拍照
    public boolean isTakePicture;
    //当前实例
    private static final CameraParam mInstance = new CameraParam();
    // 实际预览宽度
    public int previewWidth;
    // 实际预览高度
    public int previewHeight;
    // 期望预览宽度
    public int expectWidth = DEFAULT_16_9_WIDTH;
    // 期望预览高度
    public int expectHeight = DEFAULT_16_9_HEIGHT;
    // 当前长宽比
    public float currentRatio;
    //默认比例
    public int[] ratio;
    //灰度比例
    public float mScale;
    // 16:9的默认宽高(理想值)
    public static final int DEFAULT_16_9_WIDTH = 1280;
    public static final int DEFAULT_16_9_HEIGHT = 720;
    // 4:3的默认宽高(理想值)
    public static final int DEFAULT_4_3_WIDTH = 640;
    public static final int DEFAULT_4_3_HEIGHT = 480;
    // 1:1的默认宽高(理想值)
    public static final int DEFAULT_1_1_WIDTH = 640;
    public static final int DEFAULT_1_1_HEIGHT = 640;

    public CameraParam() {
        ratio = new int[]{16, 9};
    }

    /**
     * 获取相机配置参数
     * @return
     */
    public static CameraParam getInstance() {
        return mInstance;
    }
}
