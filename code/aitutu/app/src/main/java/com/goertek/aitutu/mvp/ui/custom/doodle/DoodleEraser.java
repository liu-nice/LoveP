package com.goertek.aitutu.mvp.ui.custom.doodle;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;

/**
 * 涂鸦橡皮擦
 *
 * @author wanghong
 * @version 1.0
 * @since 2019-09-20
 */
public class DoodleEraser extends BaseAction {
    private Path path;
    private int size;

    DoodleEraser() {
        path = new Path();
        size = 1;
    }

    /**
     * 构造方法及参数
     *
     * @param xPoint 初始x坐标
     * @param yPoint 初始y坐标
     * @param size   初始笔迹大小
     * @param color  初始笔迹颜色
     */
    public DoodleEraser(float xPoint,float yPoint,int size,int color) {
        super(color);
        path = new Path();
        this.size = size;
        path.moveTo(xPoint,yPoint);
        path.lineTo(xPoint,yPoint);
//        path.close();
    }


    @Override
    public void draw(Canvas canvas) {
        Log.e("weip","橡皮檫开始绘制啦");
        Paint mEraserPaint = new Paint();
        mEraserPaint.setAlpha(0);
        //这个属性是设置paint为橡皮擦重中之重
        //这是重点
        //下面这句代码是橡皮擦设置的重点
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        //上面这句代码是橡皮擦设置的重点（重要的事是不是一定要说三遍）
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setDither(true);
        mEraserPaint.setColor(Color.TRANSPARENT);
        mEraserPaint.setStyle(Paint.Style.STROKE);
//        mEraserPaint.setStyle(Paint.Style.FILL);
        mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEraserPaint.setStrokeWidth(1);
        //绘制区域
        canvas.drawPath(path,mEraserPaint);
    }

    @Override
    public void move(float mx,float my) {
        Log.e("weip","mx:" + mx + ",my:" + my);
        path.lineTo(mx,my);
//        path.close();
    }
}
