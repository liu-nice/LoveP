package com.goertek.aitutu.constant;

/**
 * key的常量
 * Created by huan on 2017/10/19.
 */

public class Key {
    //预览图片的当前角标
    public static final String PREVIEW_PHOTO_INDEX = "keyOfPreviewPhotoIndex";
    //当前预览界面的专辑index
    public static final String PREVIEW_ALBUM_ITEM_INDEX = "keyOfPreviewAlbumItemIndex";
    //当前预览界面的外部Photos集合
    public static final String PREVIEW_EXTERNAL_PHOTOS = "keyOfPreviewExternalPhotos";
    //当前预览界面的外部Photos集合底部PreviewFragment
    public static final String PREVIEW_EXTERNAL_PHOTOS_BOTTOM_PREVIEW = "keyOfPreviewExternalPhotosBottomPreview";
    //预览界面是否点击完成
    public static final String PREVIEW_CLICK_DONE = "keyOfPreviewClickDone";
    //拼图界面图片类型,true-Photo,false-String
    public static final String PUZZLE_FILE_IS_PHOTO = "keyOfPuzzleFilesTypeIsPhoto";
    //拼图界面图片结合
    public static final String PUZZLE_FILES = "keyOfPuzzleFiles";
    //拼图界面图片保存文件夹地址
    public static final String PUZZLE_SAVE_DIR = "keyOfPuzzleSaveDir";
    //拼图界面图片保存文件名前缀
    public static final String PUZZLE_SAVE_NAME_PREFIX = "keyOfPuzzleSaveNamePrefix";
    //拍照或者录制的第一帧
    public static final String EXTRA_RESULT_CAPTURE_IMAGE_PATH = "extraResultCaptureImagePath";
    //视频路径
    public static final String EXTRA_RESULT_CAPTURE_VIDEO_PATH = "extraResultCaptureVideoPath";
}
