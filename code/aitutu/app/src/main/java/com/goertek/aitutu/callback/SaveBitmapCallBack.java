package com.goertek.aitutu.callback;

import java.io.File;
import java.io.IOException;

/**
 * 保存图片到本地的回调
 * Created by huan on 2017/12/6.
 */

public interface SaveBitmapCallBack {
    void onSuccess(File file);

    void onIOFailed(IOException exception);

    void onCreateDirFailed();
}
