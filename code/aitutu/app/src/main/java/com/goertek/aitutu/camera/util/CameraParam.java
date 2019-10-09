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
    /**
     * 获取相机配置参数
     * @return
     */
    public static CameraParam getInstance() {
        return mInstance;
    }
}
