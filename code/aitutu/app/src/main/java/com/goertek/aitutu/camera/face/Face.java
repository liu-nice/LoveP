/*
 * Copyright  2016 - Goertek- All rights reserved.
 */
package com.goertek.aitutu.camera.face;

import java.util.Arrays;

/**
 * describition :
 *
 * @author ;falzy.ning
 * @version :1.0.0
 * @since : 2019/9/29 11:45
 */
public class Face {
    //每两个 保存 一个点 x+y
    //0、1 : 保存人脸的 x与y
    // 后面的 保存人脸关键点坐标 有序的
    public float[] landmarks;
    // 保存人脸的宽、高
    public int width;
    public int height;

    //送去检测图片的宽、高
    public int imgWidth;
    public int imgHeight;

    Face(int width, int height, int imgWidth, int imgHeight, float[] landmarks) {
        this.width = width;
        this.height = height;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.landmarks = landmarks;
    }

    @Override
    public String toString() {
        return "Face{" +
                "landmarks=" + Arrays.toString(landmarks) +
                ", width=" + width +
                ", height=" + height +
                ", imgWidth=" + imgWidth +
                ", imgHeight=" + imgHeight +
                '}';
    }
}
