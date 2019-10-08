package com.goertek.aitutu.util;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.goertek.aitutu.mvp.model.entity.FolderInfo;
import com.goertek.aitutu.mvp.model.entity.ImageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * 图片工具类
 *
 * @author: ww
 * @version: 1.0.0
 * @since: 2019/09/26
 */
public class PhotoPickUtil {
    // 不同loader定义
    private static final int LOADER_ALL = 0;

    private static final int LOADER_CATEGORY = 1;

    private static boolean hasFolderGened;

    public static final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.SIZE};

    @org.jetbrains.annotations.Nullable
    public static Loader<Cursor> getCursorLoader(Context context, int id, Bundle bundle) {
        if (id == LOADER_ALL) {
            CursorLoader cursorLoader = new CursorLoader(context,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    null, null, IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        } else if (id == LOADER_CATEGORY) {
            CursorLoader cursorLoader = new CursorLoader(context,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    IMAGE_PROJECTION[0] + " like '%" + bundle.getString("path") + "%'", null, PhotoPickUtil.IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        }
        return null;
    }

    /**
     * 获取所有图片
     * @param data 数据
     * @param imageInfos 所有图片集合
     * @param mResultFolder 文件夹集合
     */
    public static void getAllImages(Cursor data, List<ImageInfo> imageInfos, List<FolderInfo> mResultFolder) {
        do {
            String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
            String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
            long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
            int size = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
            boolean show_flag = size > 1024 * 10; //是否大于10K
            ImageInfo imageInfo = new ImageInfo(path, name, dateTime);
            if (show_flag) {
                imageInfos.add(imageInfo);
            }

            if (!hasFolderGened && show_flag) {
                // 获取文件夹名称
                File imageFile = new File(path);
                File folderFile = imageFile.getParentFile();
                FolderInfo folderInfo = new FolderInfo();
                folderInfo.name = folderFile.getName();
                folderInfo.path = folderFile.getAbsolutePath();
                folderInfo.cover = imageInfo;
                if (!mResultFolder.contains(folderInfo)) {
                    List<ImageInfo> imageList = new ArrayList<ImageInfo>();
                    imageList.add(imageInfo);
                    folderInfo.imageInfos = imageList;
                    mResultFolder.add(folderInfo);
                } else {
                    // 更新
                    FolderInfo folder = mResultFolder.get(mResultFolder.indexOf(folderInfo));
                    if (!folder.imageInfos.contains(imageInfo)) {
                        folder.imageInfos.add(imageInfo);
                    }
                }

                Timber.e("imageInfos =========" + imageInfos.size());
            }
        } while (data.moveToNext());
        hasFolderGened = true;
    }
}
