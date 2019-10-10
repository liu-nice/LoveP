/*
 * Copyright (c) 2019 -Goertek -All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.custom;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.goertek.aitutu.R;
import com.goertek.aitutu.util.RectUtil;


/**
 * @author weip
 */
public class StickerItem {
    private static final float MIN_SCALE = 0.15f;
    private static final int HELP_BOX_PAD = 25;

    public static final int BUTTON_WIDTH = ModuleConfig.STICKER_BTN_HALF_SIZE;

    public Bitmap bitmap;
    public Rect srcRect;// 原始图片坐标
    public RectF dstRect;// 绘制目标坐标
    protected float mCenterX;
    protected float mCenterY;
    protected Rect helpToolsRect;
    public RectF deleteRect;// 删除按钮位置
    public RectF rotateRect;// 旋转按钮位置

    public RectF helpBox;
    public Matrix matrix;// 变化矩阵
    public float roatetAngle = 0;
    boolean isDrawHelpTool = false;
    private Paint dstPaint = new Paint();
    protected Paint paint = new Paint();
    protected Paint helpBoxPaint = new Paint();

    protected float initWidth;// 加入屏幕时原始宽度

    protected Bitmap deleteBit;
    protected Bitmap rotateBit;

    private Paint debugPaint = new Paint();
    public RectF detectRotateRect;

    public RectF detectDeleteRect;

    public StickerItem(Context context) {

        helpBoxPaint.setColor(Color.BLACK);
        helpBoxPaint.setStyle(Style.STROKE);
        helpBoxPaint.setAntiAlias(true);
        helpBoxPaint.setStrokeWidth(4);

        // 导入工具按钮位图
        if (deleteBit == null) {
            deleteBit = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.sticker_delete);
        }// end if
        if (rotateBit == null) {
            rotateBit = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.sticker_rotate);
        }// end if
    }

    public void init(Bitmap addBit,View parentView) {
        bitmap = addBit;
        srcRect = new Rect(0,0,addBit.getWidth(),addBit.getHeight());
        int bitWidth = Math.min(addBit.getWidth(),parentView.getWidth() >> 1);
        int bitHeight = (int) bitWidth * addBit.getHeight() / addBit.getWidth();
        int left = (parentView.getWidth() >> 1) - (bitWidth >> 1);
        int top = (parentView.getHeight() >> 1) - (bitHeight >> 1);
        dstRect = new RectF(left,top,left + bitWidth,top + bitHeight);
        matrix = new Matrix();
        matrix.postTranslate(dstRect.left,dstRect.top);
        matrix.postScale((float) bitWidth / addBit.getWidth(),
                (float) bitHeight / addBit.getHeight(),dstRect.left,
                dstRect.top);
        initWidth = dstRect.width();// 记录原始宽度
        // item.matrix.setScale((float)bitWidth/addBit.getWidth(),
        // (float)bitHeight/addBit.getHeight());
        isDrawHelpTool = true;
        helpBox = new RectF(dstRect);
        updateHelpBoxRect();

        helpToolsRect = new Rect(0,0,deleteBit.getWidth(),
                deleteBit.getHeight());

        deleteRect = new RectF(helpBox.left - BUTTON_WIDTH,helpBox.top
                - BUTTON_WIDTH,helpBox.left + BUTTON_WIDTH,helpBox.top
                + BUTTON_WIDTH);
        rotateRect = new RectF(helpBox.right - BUTTON_WIDTH,helpBox.bottom
                - BUTTON_WIDTH,helpBox.right + BUTTON_WIDTH,helpBox.bottom
                + BUTTON_WIDTH);

        detectRotateRect = new RectF(rotateRect);
        detectDeleteRect = new RectF(deleteRect);
    }

    protected void updateHelpBoxRect() {
        helpBox.left -= HELP_BOX_PAD;
        helpBox.right += HELP_BOX_PAD;
        helpBox.top -= HELP_BOX_PAD;
        helpBox.bottom += HELP_BOX_PAD;
    }

    /**
     * 位置更新
     *
     * @param dx
     * @param dy
     */
    public void updatePos(final float dx,final float dy) {
        Log.e("weip","dx:" + dx + ",,,,,,,dy" + dy);
        matrix.postTranslate(dx,dy);// 记录到矩阵中
        mCenterX += dx;
        mCenterY += dy;
        dstRect.offset(dx,dy);

        // 工具按钮随之移动
        helpBox.offset(dx,dy);
        deleteRect.offset(dx,dy);
        rotateRect.offset(dx,dy);

        detectRotateRect.offset(dx,dy);
        detectDeleteRect.offset(dx,dy);
    }

    protected float scale = 1;

    /**
     * 旋转 缩放 更新
     *
     * @param dx
     * @param dy
     */
    public void updateRotateAndScale(final float oldx,final float oldy,
                                     final float dx,final float dy) {
        float c_x = dstRect.centerX();
        float c_y = dstRect.centerY();

        float x = detectRotateRect.centerX();
        float y = detectRotateRect.centerY();

        // float x = oldx;
        // float y = oldy;

        float n_x = x + dx;
        float n_y = y + dy;

        float xa = x - c_x;
        float ya = y - c_y;

        float xb = n_x - c_x;
        float yb = n_y - c_y;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);

        // System.out.println("srcLen--->" + srcLen + "   curLen---->" +
        // curLen);

        scale = curLen / srcLen;// 计算缩放比

        float newWidth = dstRect.width() * scale;
        if (newWidth / initWidth < MIN_SCALE) {// 最小缩放值检测
            return;
        }

        matrix.postScale(scale,scale,dstRect.centerX(),
                dstRect.centerY());// 存入scale矩阵
        // this.matrix.postRotate(5, this.dstRect.centerX(),
        // this.dstRect.centerY());
        RectUtil.scaleRect(dstRect,scale);// 缩放目标矩形

        // 重新计算工具箱坐标
        helpBox.set(dstRect);
        updateHelpBoxRect();// 重新计算
        rotateRect.offsetTo(helpBox.right - BUTTON_WIDTH,helpBox.bottom
                - BUTTON_WIDTH);
        deleteRect.offsetTo(helpBox.left - BUTTON_WIDTH,helpBox.top
                - BUTTON_WIDTH);

        detectRotateRect.offsetTo(helpBox.right - BUTTON_WIDTH,helpBox.bottom
                - BUTTON_WIDTH);
        detectDeleteRect.offsetTo(helpBox.left - BUTTON_WIDTH,helpBox.top
                - BUTTON_WIDTH);

        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1) {
            return;
        }
        float angle = (float) Math.toDegrees(Math.acos(cos));
        // System.out.println("angle--->" + angle);

        // 定理
        float calMatrix = xa * yb - xb * ya;// 行列式计算 确定转动方向

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        // System.out.println("angle--->" + angle);
        roatetAngle += angle;
        matrix.postRotate(angle,dstRect.centerX(),
                dstRect.centerY());

        RectUtil.rotateRect(detectRotateRect,dstRect.centerX(),
                dstRect.centerY(),roatetAngle);
        RectUtil.rotateRect(detectDeleteRect,dstRect.centerX(),
                dstRect.centerY(),roatetAngle);
        // System.out.println("angle----->" + angle + "   " + flag);

        // System.out
        // .println(srcLen + "     " + curLen + "    scale--->" + scale);

    }

    //0-255
    public int mAlpha = 255;

    public void draw(Canvas canvas) {
        paint.setAlpha(mAlpha);
        canvas.drawBitmap(bitmap,matrix,paint);// 贴图元素绘制
        // canvas.drawRect(this.dstRect, dstPaint);
        if (isDrawHelpTool) {// 绘制辅助工具线
            canvas.save();
            canvas.rotate(roatetAngle,helpBox.centerX(),helpBox.centerY());
            canvas.drawRoundRect(helpBox,10,10,helpBoxPaint);
            // 绘制工具按钮
            canvas.drawBitmap(deleteBit,helpToolsRect,deleteRect,null);
            canvas.drawBitmap(rotateBit,helpToolsRect,rotateRect,null);
            canvas.restore();
            // canvas.drawRect(deleteRect, dstPaint);
            // canvas.drawRect(rotateRect, dstPaint);

            //debug
//             canvas.drawRect(detectRotateRect, debugPaint);
//             canvas.drawRect(detectDeleteRect, debugPaint);
//             canvas.drawRect(helpBox , debugPaint);
        }// end if

        // detectRotateRect
    }
}// end class
