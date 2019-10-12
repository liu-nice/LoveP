/*
 * Copyright (c) 2019 -Goertek -All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.custom.sticker;

import android.view.MotionEvent;

/**
 * @author wupanjie
 */

public interface StickerIconEvent {
  void onActionDown(StickerView stickerView,MotionEvent event);

  void onActionMove(StickerView stickerView,MotionEvent event);

  void onActionUp(StickerView stickerView,MotionEvent event);
}
