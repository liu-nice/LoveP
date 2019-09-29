package com.goertek.aitutu.mvp.model.entity;

import java.util.List;

/**
 * 功能说明
 *
 * @author: ww
 * @version: 1.0.0
 * @since: 2019/09/29
 */
public class ImageEntity {

    private List<ImageInfo> imageInfoList;

    private List<FolderInfo> folderInfoList;

    public List<ImageInfo> getImageInfoList() {
        return imageInfoList;
    }

    public void setImageInfoList(List<ImageInfo> imageInfoList) {
        this.imageInfoList = imageInfoList;
    }

    public List<FolderInfo> getFolderInfoList() {
        return folderInfoList;
    }

    public void setFolderInfoList(List<FolderInfo> folderInfoList) {
        this.folderInfoList = folderInfoList;
    }
}
