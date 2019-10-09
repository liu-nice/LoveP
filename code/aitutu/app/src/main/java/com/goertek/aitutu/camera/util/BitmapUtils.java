/*
 * Copyright  2016 - Goertek- All rights reserved.
 */
package com.goertek.aitutu.camera.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import timber.log.Timber;

/**
 * describition : bitmap工具类
 *
 * @author ;falzy.ning
 * @version :1.0.0
 * @since : 2019/9/29 11:45
 */
public class BitmapUtils {
    /**
     * 回收bitmap
     *
     * @param bitmap 回收的bitmap
     */
    public static void recycle(Bitmap bitmap) {
        if (null != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

    /**
     * 回收bitmap
     *
     * @param bitmaps 回收的bitmap集合
     */
    public static void recycle(Bitmap... bitmaps) {
        for (Bitmap b : bitmaps) {
            recycle(b);
        }
    }

    /**
     * 回收bitmap
     *
     * @param bitmaps 回收的bitmap
     */
    public static void recycle(List<Bitmap> bitmaps) {
        for (Bitmap b : bitmaps) {
            recycle(b);
        }
    }

    /**
     * 保存图片
     *
     * @param filePath 图片全路径
     * @param buffer   图片字节数组
     * @param width    宽
     * @param height   高
     * @param context 上下文
     */
    public static void saveBitmap(Context context, String filePath, ByteBuffer buffer, int width, int height) {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(filePath));
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = BitmapUtils.rotateBitmap(bitmap, 180, true);
            bitmap = BitmapUtils.flipBitmap(bitmap, true);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            notifyUpdateAlbum(context, filePath, bitmap);
            recycle(bitmap);
        } catch (FileNotFoundException e) {
            Timber.e(e.toString());
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    Timber.e(e.toString());
                }
            }
        }
    }

    /**
     * 通知相册更新
     *
     * @param context  上下文
     * @param fileName 图片路径
     * @param mBitMap  图片
     */
    private static void notifyUpdateAlbum(Context context, String fileName, Bitmap mBitMap) {
        MediaStore.Images.Media.insertImage(context.getContentResolver(),
                mBitMap, fileName, null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(fileName));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * 将Bitmap图片旋转一定角度
     *
     * @param bitmap 图片
     * @param rotate 旋转度
     * @param isRecycled 是否回收
     * @return 旋转后的bitmap
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int rotate, boolean isRecycled) {
        if (bitmap == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.postRotate(rotate);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        recycle(bitmap);
        return rotatedBitmap;
    }

    /**
     * 镜像翻转图片
     *
     * @param bitmap 图片
     * @param isRecycled 是否回收
     * @return 翻转后的bitmap
     */
    public static Bitmap flipBitmap(Bitmap bitmap, boolean isRecycled) {
        return flipBitmap(bitmap, true, false, isRecycled);
    }

    /**
     * 翻转图片
     *
     * @param bitmap     处理的图片
     * @param flipX      翻转X
     * @param flipY      翻转Y
     * @param isRecycled 是否回收
     * @return 翻转后的bitmap
     */
    public static Bitmap flipBitmap(Bitmap bitmap, boolean flipX, boolean flipY, boolean isRecycled) {
        if (bitmap == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.setScale(flipX ? -1 : 1, flipY ? -1 : 1);
        matrix.postTranslate(bitmap.getWidth(), 0);
        Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);
        recycle(bitmap);
        return result;
    }
}
