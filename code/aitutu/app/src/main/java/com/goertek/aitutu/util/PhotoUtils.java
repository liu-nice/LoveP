package com.goertek.aitutu.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * 图片基本操作
 */
public class PhotoUtils {

    /**
     * 图片旋转
     *
     * @param bitmap  旋转原图片
     * @param degrees 旋转角度
     * @return 返回旋转后的图片
     */
    public static Bitmap rotateImage(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap tempBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return tempBitmap;
    }

    /**
     * 图片翻转
     *
     * @param bitmap 翻转原图片
     * @param xZhou  翻转X轴
     * @param yZhou  翻转Y轴
     * @return 翻转之后的图片, 说明: (1,-1)上下翻转  (-1,1)左右翻转
     */
    public static Bitmap reverseImage(Bitmap bitmap, int xZhou, int yZhou) {
        Matrix matrix = new Matrix();
        matrix.postScale(xZhou, yZhou);
        Bitmap tempBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return tempBitmap;
    }
}
