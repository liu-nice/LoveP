package com.goertek.aitutu.mvp.ui.custom.doodle;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

/**
 * 涂鸦库虚线
 * @author wanghong
 * @version 1.0
 * @since 2019-09-20
 */

public class DoodleDottedLine extends BaseAction {
    private float startX;
    private float startY;
    private float stopX;
    private float stopY;
    private int size;

    /**
     * 虚线距离点
     */
    private int dottedLine = 4;

    DoodleDottedLine() {
        startX = 0;
        startY = 0;
        stopX = 0;
        stopY = 0;
    }

    /**
     * 构造方法及参数
     * @param xPoint 初始x坐标
     * @param yPoint 初始y坐标
     * @param size   初始笔迹大小
     * @param color  初始笔迹颜色
     */
    DoodleDottedLine(float xPoint, float yPoint, int size, int color) {
        super(color);
        this.startX = xPoint;
        this.startY = yPoint;
        stopX = xPoint;
        stopY = yPoint;
        this.size = size;
    }
    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(size);
        paint.setPathEffect(new DashPathEffect(new float[]{dottedLine, dottedLine}, 0));
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    @Override
    public void move(float mx, float my) {
        this.stopX = mx;
        this.stopY = my;
    }
}
