package com.goertek.aitutu.mvp.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import com.goertek.aitutu.callback.SaveBitmapCallBack;
import com.goertek.aitutu.util.BitmapUtils;
import com.goertek.aitutu.util.EasyPhotos;

/**
 * 贴图view的管理器，用于module与外部解耦
 * Created by huan on 2017/7/24.
 */

public class StickerModel {


    public StickerModel() {
        super();
    }


    public void save(Activity act, ViewGroup stickerGroup, View imageGroup, int imageWidth, int imageHeight, final String dirPath, final String namePrefix, final boolean notifyMedia, final SaveBitmapCallBack callBack) {

        Bitmap srcBitmap = Bitmap.createBitmap(stickerGroup.getWidth(), stickerGroup.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(srcBitmap);
        stickerGroup.draw(canvas);

        Bitmap cropBitmap = Bitmap.createBitmap(srcBitmap, imageGroup.getLeft(), imageGroup.getTop(), imageGroup.getWidth(), imageGroup.getHeight());
        BitmapUtils.recycle(srcBitmap);
        Bitmap saveBitmap = null;
        if (imageGroup.getWidth() > imageWidth || imageGroup.getHeight() > imageHeight) {
            saveBitmap = Bitmap.createScaledBitmap(cropBitmap, imageWidth, imageHeight, true);
            BitmapUtils.recycle(cropBitmap);
        } else {
            saveBitmap = cropBitmap;
        }

        EasyPhotos.saveBitmapToDir(act, dirPath, namePrefix, saveBitmap, notifyMedia, callBack);

    }

    public void setCanvasSize(final Bitmap b, final ViewGroup imageGroup) {
        if (imageGroup.getMeasuredWidth() == 0) {
            imageGroup.post(new Runnable() {
                @Override
                public void run() {
                    setSize(b, imageGroup);
                }
            });
        } else {
            setSize(b, imageGroup);
        }
    }

    private void setSize(Bitmap b, ViewGroup v) {
        int bW = b.getWidth();
        int bH = b.getHeight();

        int vW = v.getMeasuredWidth();
        int vH = v.getMeasuredHeight();

        float scalW = (float) vW / (float) bW;
        float scalH = (float) vH / (float) bH;

        ViewGroup.LayoutParams params = v.getLayoutParams();
        //如果图片小于viewGroup的宽高则把viewgroup设置为图片宽高
//        if (bW < vW && bH < vH) {
//            params.width = bW;
//            params.height = bH;
//            v.setLayoutParams(params);
//            return;
//        }
        if (bW >= bH) {
            params.width = vW;
            params.height = (int) (scalW * bH);
        } else {
            params.width = (int) (scalH * bW);
            params.height = vH;
        }
        if (params.width > vW) {
            float tempScaleW = (float) vW / (float) params.width;
            params.width = vW;
            params.height = (int) (params.height * tempScaleW);
        }
        if (params.height > vH) {
            float tempScaleH = (float) vH / (float) params.height;
            params.height = vH;
            params.width = (int) (params.width * tempScaleH);
        }
        v.setLayoutParams(params);
    }

}
