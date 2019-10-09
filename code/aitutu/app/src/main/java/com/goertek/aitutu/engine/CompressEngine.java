package com.goertek.aitutu.engine;

import android.content.Context;

import com.goertek.aitutu.callback.CompressCallback;
import com.goertek.aitutu.mvp.model.entity.Photo;
import java.util.ArrayList;

/**
 * 图片压缩方式
 * Created by joker on 2019/8/1.
 */
public interface CompressEngine {
    /**
     * 压缩处理
     *
     * @param photos 选择的图片
     */
    void compress(Context context, ArrayList<Photo> photos, CompressCallback callback);
}
