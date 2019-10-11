package com.goertek.aitutu.mvp.ui.custom.doodle;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 涂鸦库圆形
 * @author wanghong
 * @version 1.0
 * @since 2019-09-20
 */
class DoodleCircle extends BaseAction {
    private float startX;
    private float startY;
    private float stopX;
    private float stopY;
    private float radius;
    private int size;

    DoodleCircle() {
        startX = 0;
        startY = 0;
        stopX = 0;
        stopY = 0;
        radius = 0;
    }

    /**
     * 构造方法及参数
     * @param xPoint 初始x坐标
     * @param yPoint 初始y坐标
     * @param size   初始笔迹大小
     * @param color  初始笔迹颜色
     */
    DoodleCircle(float xPoint, float yPoint, int size, int color) {
        super(color);
        this.startX = xPoint;
        this.startY = yPoint;
        this.stopX = xPoint;
        this.stopY = yPoint;
        this.radius = 0;
        this.size = size;
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(size);
        canvas.drawCircle((startX + stopX) / 2, (startY + stopY) / 2, radius, paint);
    }

    @Override
    public void move(float mx, float my) {
        stopX = mx;
        stopY = my;
        radius = (float) ((Math.sqrt((mx - startX) * (mx - startX)
                + (my - startY) * (my - startY))) / 2);
    }
}
