/*
 * Copyright (c) 2019 -Goertek -All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.custom.sticker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;

import com.goertek.aitutu.mvp.ui.custom.doodle.DoodleEraser;

/**
 * @author wupanjie
 */
public class DrawableSticker extends Sticker {

    private Drawable drawable;
    private Rect realBounds;
    private DoodleEraser doodleEraser;

    public Rect getStickerRect() {
        return realBounds;
    }

    public DoodleEraser getDoodleEraser() {
        return doodleEraser;
    }

    public DrawableSticker(Drawable drawable) {
        this.drawable = drawable;
        realBounds = new Rect(0,0,getWidth(),getHeight());
    }

    public void initDoodleEraser(float downX,float downY) {
        doodleEraser = new DoodleEraser(downX,downY,2,Color.BLUE);
    }

    public void move(float mx,float my) {
        Log.e("weip","mx=" + mx + ",,,,,,,,,,,,,,,,,,,,my=" + my);
        if ((mx >= 0 && mx <= getWidth()) && (my >= 0 && my <= getHeight())) {
            doodleEraser.move(mx,my);
        }
    }

    public void up() {
        doodleEraser = null;
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return drawable;
    }

    @Override
    public DrawableSticker setDrawable(@NonNull Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.concat(getMatrix());
        drawable.setBounds(realBounds);
        drawable.draw(canvas);
        canvas.restore();
    }

    @NonNull
    @Override
    public DrawableSticker setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        drawable.setAlpha(alpha);
        return this;
    }

    @Override
    public int getWidth() {
        return drawable.getIntrinsicWidth();
    }

    @Override
    public int getHeight() {
        return drawable.getIntrinsicHeight();
    }

    @Override
    public void release() {
        super.release();
        if (drawable != null) {
            drawable = null;
        }
    }
}
