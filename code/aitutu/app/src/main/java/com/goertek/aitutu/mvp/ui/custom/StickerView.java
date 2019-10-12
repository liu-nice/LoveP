/*
 * Copyright (c) 2019 -Goertek -All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.custom;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.goertek.aitutu.util.RectUtil;

import java.util.LinkedHashMap;

/**
 * 贴图操作控件
 *
 * @author weip
 */
public class StickerView extends View {
    //闲置状态
    private static int STATUS_IDLE = 0;
    //移动状态
    private static int STATUS_MOVE = 1;
    //删除状态
    private static int STATUS_DELETE = 2;
    //旋转状态
    private static int STATUS_ROTATE = 3;
    //已加入照片的数量
    private int imageCount;
    //上下文
    private Context mContext;
    //当前状态
    private int currentStatus;
    //当前操作的贴图数据
    private StickerItem currentItem;
    private float oldx, oldy;
    private Paint rectPaint = new Paint();
    private Paint boxPaint = new Paint();
    //存储每层贴图数据
    private LinkedHashMap<Integer, StickerItem> bank = new LinkedHashMap<Integer, StickerItem>();

    private Point mPoint = new Point(0,0);

    public StickerView(Context context) {
        super(context);
        init(context);
    }

    public StickerView(Context context,AttributeSet attrs) {
        super(context,attrs);
        init(context);
    }

    public StickerView(Context context,AttributeSet attrs,int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        currentStatus = STATUS_IDLE;
        rectPaint.setColor(Color.RED);
        rectPaint.setAlpha(100);
    }

    public void addBitImage(Bitmap addBit) {
        StickerItem item = new StickerItem(getContext());
        item.init(addBit,this);
        if (currentItem != null) {
            currentItem.isDrawHelpTool = false;
        }
        bank.put(++imageCount,item);
        invalidate();// 重绘视图
    }

//    public void addText(TextPro textPro) {
//        TextStickerItem item = new TextStickerItem(getContext());
//        item.init(textPro,this);
//        if (currentItem != null) {
//            currentItem.isDrawHelpTool = false;
//        }
//        bank.put(++imageCount,item);
//        invalidate();// 重绘视图
//    }

    /**
     * 绘制客户页面
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // System.out.println("on draw!!~");
        for (Integer id : bank.keySet()) {
            StickerItem item = bank.get(id);
            item.draw(canvas);
        }// end for each
    }

    /**
     * 更新贴纸透明度
     *
     * @param alpha
     */
    public void updateStickerAlpha(int alpha) {
        Log.e("weip","=============size:" + imageCount);
        if (imageCount == 0) {
            return;
        }
        if (currentItem != null) {
            currentItem.mAlpha = alpha;
        } else {
            StickerItem stickerItem = bank.get(imageCount);
            if (stickerItem != null) {
                stickerItem.mAlpha = alpha;
            }
        }
        invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);// 是否向下传递事件标志 true为消耗
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //第一种，点击了item，显示边框、删除按钮、旋转按钮；
                // 第一种，显示出来了删除按钮、旋转按钮，点击删除按钮，删除；
                // 第二种，显示出来了边框，点击旋转按钮，进行旋转；
                // 第三种，显示出来了边框，长按拖动，进行移动；
                //第二种，点击item之外的区域，边框显示
                int deleteId = -1;
                for (Integer id : bank.keySet()) {
                    StickerItem item = bank.get(id);
                    float left = item.dstRect.left;
                    float top = item.dstRect.top;
                    float right = item.dstRect.right;
                    float bottom = item.dstRect.bottom;
                    boolean contains = item.dstRect.contains(x,y);
                    Log.e("weip","isArea:" + contains + ",,,,x:" + x + ",,,,y:" + y + ",,,,left:" + left + ",,,top::" + top + ",,,,right:" + right + ",,,,bottom:" + bottom);
                    if (item.detectDeleteRect.contains(x,y)) {// 删除模式
                        Log.e("weip","删除模式....");
                        // ret = true;
                        deleteId = id;
                        currentStatus = STATUS_DELETE;
                    } else if (item.detectRotateRect.contains(x,y)) {// 点击了旋转按钮
                        Log.e("weip","旋转模式....");
                        ret = true;
                        if (currentItem != null) {
                            currentItem.isDrawHelpTool = false;
                        }
                        currentItem = item;
                        currentItem.isDrawHelpTool = true;
                        currentStatus = STATUS_ROTATE;
                        oldx = x;
                        oldy = y;
                    } else if (detectInItemContent(item,x,y)) {// 移动模式
                        Log.e("weip","移动模式....");
                        // 被选中一张贴图
                        ret = true;
                        if (currentItem != null) {
                            currentItem.isDrawHelpTool = false;
                        }
                        currentItem = item;
                        currentItem.isDrawHelpTool = true;
                        currentStatus = STATUS_MOVE;
                        oldx = x;
                        oldy = y;
                    }// end if
                }// end for each
                if (!ret && currentItem != null && currentStatus == STATUS_IDLE) {// 没有贴图被选择
                    currentItem.isDrawHelpTool = false;
                    currentItem = null;
                    invalidate();
                }
                if (deleteId > 0 && currentStatus == STATUS_DELETE) {// 删除选定贴图
                    bank.remove(deleteId,bank.get(deleteId));
                    currentStatus = STATUS_IDLE;// 返回空闲状态
                    invalidate();
                }// end if
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("lll"," ACTION_MOVE currentStatus==" + currentStatus);
                ret = true;
                if (currentStatus == STATUS_MOVE) {// 移动贴图
                    float dx = x - oldx;
                    float dy = y - oldy;
                    if (currentItem != null) {
                        //贴纸不能绘制到屏幕外
                        Log.e("weip","dx的值:" + dx + ",dy的值:" + dy + ",xxx的值:" + x);
                        if (currentItem.dstRect.left > 20) {
                            currentItem.updatePos(dx,dy);
                            invalidate();
                        } else {

                        }
                    }// end if
                    oldx = x;
                    oldy = y;
                } else if (currentStatus == STATUS_ROTATE) {// 旋转 缩放图片操作
                    // System.out.println("旋转");
                    float dx = x - oldx;
                    float dy = y - oldy;
                    if (currentItem != null) {
                        currentItem.updateRotateAndScale(oldx,oldy,dx,dy);// 旋转
                        invalidate();
                    }// end if
                    oldx = x;
                    oldy = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.e("lll"," ACTION_UP currentStatus==" + currentStatus);
                ret = false;
                currentStatus = STATUS_IDLE;
                break;
        }// end switch
        return ret;
    }

    /**
     * 判定点击点是否在内容范围之内  需考虑旋转
     *
     * @param item
     * @param x
     * @param y
     * @return
     */
    private boolean detectInItemContent(StickerItem item,float x,float y) {
        //reset
//        mPoint.set((int) x,(int) y);
//        //旋转点击点
//        RectUtil.rotatePoint(mPoint,item.helpBox.centerX(),item.helpBox.centerY(),-item.roatetAngle);
//        return item.helpBox.contains(mPoint.x,mPoint.y);
        return detectInItemContent(item.helpBox,item.helpBox.centerX(),item.helpBox.centerY(),x,y,item.roatetAngle);
    }

    private boolean detectInItemContent(RectF rectF,float px,float py,float x,float y,float rotateAngle) {
        //reset
        mPoint.set((int) x,(int) y);
        //旋转点击点
        RectUtil.rotatePoint(mPoint,px,py,-rotateAngle);
        return rectF.contains(mPoint.x,mPoint.y);
    }

    public LinkedHashMap<Integer, StickerItem> getBank() {
        return bank;
    }

    public void clear() {
        bank.clear();
        invalidate();
    }
}// end class
