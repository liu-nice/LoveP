package com.goertek.aitutu.mvp.ui.custom.doodle;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * 涂鸦自由绘制曲线
 * @author wanghong
 * @version 1.0
 * @since 2019-09-20
 */

class DoodlePath extends BaseAction {
    private Path path;
    private int size;

    DoodlePath() {
        path = new Path();
        size = 1;
    }

    /**
     * 构造方法及参数
     * @param xPoint 初始x坐标
     * @param yPoint 初始y坐标
     * @param size   初始笔迹大小
     * @param color  初始笔迹颜色
     */
    DoodlePath(float xPoint, float yPoint, int size, int color) {
        super(color);
        this.path = new Path();
        this.size = size;
        path.moveTo(xPoint, yPoint);
        path.lineTo(xPoint, yPoint);
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStrokeWidth(size);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPath(path, paint);
    }

    @Override
    public void move(float mx, float my) {
        path.lineTo(mx, my);
    }
}
