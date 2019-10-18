package com.goertek.aitutu.mvp.ui.custom.doodle;

import android.graphics.Canvas;
import android.graphics.Color;

/**
 * 动作的基础类
 */

public abstract class BaseAction {

    public int color;

    BaseAction() {
        color = Color.WHITE;
    }

    BaseAction(int color) {
        this.color = color;
    }

    public abstract void draw(Canvas canvas);

    public abstract void move(float mx, float my);

}





























