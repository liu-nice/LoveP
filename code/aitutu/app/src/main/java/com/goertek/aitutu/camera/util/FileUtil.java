/*
 * Copyright  2016 - Goertek- All rights reserved.
 */
package com.goertek.aitutu.camera.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * describition : 文件工具类
 *
 * @author ;falzy.ning
 * @version :1.0.0
 * @since : 2019/9/29 15:10
 */
public class FileUtil {
    /**
     * 图片名字时间戳格式
     */
    private static String SDF = "yyyyMMddHHmmssSSS";

    /**
     * 获取当前要保存的图片名字
     * @return picture name
     */
    public static String getCurrentPicName() {
        DateFormat dateTimeFormat = new SimpleDateFormat(SDF);
        String currDateTime = dateTimeFormat.format(new Date());
        return "IMG_" + currDateTime + ".jpg";
    }
}
