package com.goertek.aitutu.mvp.model.entity;

/**
 * 作者：铁血男儿王钢蛋

 * 说明：文件夹实体类
 */
public class FolderBean {

    /**
     * 当前文件夹路径
     */
    private String dir;
    /**
     * 当前文件夹第一个照片的路径
     */
    private String firstImgPath;

    /**
     *
     */
    private String name;
    /**
     * 当前文件夹内图片数量
     */
    private int count;

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        String[] names = this.dir.split("/");
        this.name = names[names.length - 1];
    }

    public String getFirstImgPath() {
        return firstImgPath;
    }

    public void setFirstImgPath(String firstImgPath) {
        this.firstImgPath = firstImgPath;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    @Override
    public String toString() {
        return "FolderBean{" + "dir='" + dir + '\'' + ", firstImgPath='" + firstImgPath + '\'' + ", name='" + name + '\'' + ", count=" + count + '}';
    }}