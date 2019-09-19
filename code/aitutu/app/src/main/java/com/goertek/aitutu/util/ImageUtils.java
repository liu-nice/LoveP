package com.goertek.aitutu.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import com.goertek.aitutu.mvp.model.entity.FolderBean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 图片工具类
 */
public class ImageUtils {

    /**
     * 图片的八个位置
     **/
    public static final int TOP = 0;      //上
    public static final int BOTTOM = 1;      //下
    public static final int LEFT = 2;      //左
    public static final int RIGHT = 3;      //右
    public static final int LEFT_TOP = 4;    //左上
    public static final int LEFT_BOTTOM = 5;  //左下
    public static final int RIGHT_TOP = 6;    //右上
    public static final int RIGHT_BOTTOM = 7;  //右下
    /**
     * 最大图片数量的文件夹图片数
     */
    public static int mMaxCount = 0;
    /**
     * 最大图片数量的文件夹
     */
    public static File mMaxCountDir = null;    //////////////////////////获取手机的图片///////////////////////////////////

    public static List<FolderBean> getAllImagePath(Context ctx) {
        List<FolderBean> mFolderBeans = new ArrayList<>();        //[1]查询获得游标:content://media/external/images/media
        Uri mIngUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = ctx.getContentResolver();
        Cursor cursor = resolver.query(mIngUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?", new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);        //[2]通过游标获取path，创建folderBean对象并赋值
        //[2-1]为避免重复扫描，将dirPath放入HashSet集合
        Set<String> mDirPaths = new HashSet<>();
        while (cursor.moveToNext()) {            //获取数据库中图片路径：/storage/emulated/0/DCIM/Camera/IMG20160501152640.jpg
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));            //获取父目录：/storage/emulated/0/DCIM/Camera
            File parentFile = new File(path).getParentFile();            //没有父目录，跳出本次循环
            if (parentFile == null)
                continue;            //声明实体对象
            FolderBean folderBean;            //父目录的绝对路径：/storage/emulated/0/DCIM/Camera
            String dirPath = parentFile.getAbsolutePath();
            if (mDirPaths.contains(dirPath)) {
                continue;//集合中有这个目录 跳出本次循环
            } else {//集合中没有这个目录
                //加入集合
                mDirPaths.add(dirPath);                //创建实体对象
                folderBean = new FolderBean();                //父文件夹设置到folderBean
                folderBean.setDir(dirPath);                //第一张图片路径设置到folderBean
                folderBean.setFirstImgPath(path);
            }
            if (parentFile.list() != null) {                //根据父文件夹，过滤出所有以jpg,png,jpeg结尾的文件的数量
                int imgCount = parentFile.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg");
                    }
                }).length;
                if (mMaxCount <= imgCount) {
                    mMaxCount = imgCount;
                    mMaxCountDir = parentFile;
                }                //设置文件夹下图片的数量
                folderBean.setCount(imgCount);                //加入集合
                mFolderBeans.add(folderBean);
            }
        }
        cursor.close();
        return mFolderBeans;
    }

    public static int getMiniSize(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        return Math.min(options.outHeight, options.outWidth);
    }

    public static boolean isSquare(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        return options.outHeight == options.outWidth;
    }


    /**
     * 图像的放大缩小方法
     *
     * @param src    源位图对象
     * @param scaleX 宽度比例系数
     * @param scaleY 高度比例系数
     * @return 返回位图对象
     */
    public static Bitmap zoomBitmap(Bitmap src, float scaleX, float scaleY) {
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);
        Bitmap t_bitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return t_bitmap;
    }

    /**
     * 图像放大缩小--根据宽度和高度
     *
     * @param src
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBimtap(Bitmap src, int width, int height) {
        return Bitmap.createScaledBitmap(src, width, height, true);
    }

    /**
     * 将Drawable转为Bitmap对象
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }


    /**
     * 将Bitmap转换为Drawable对象
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    /**
     * Bitmap转byte[]
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        return out.toByteArray();
    }

    /**
     * byte[]转Bitmap
     *
     * @param data
     * @return
     */
    public static Bitmap byteToBitmap(byte[] data) {
        if (data.length != 0) {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        return null;
    }

    /**
     * 绘制带圆角的图像
     *
     * @param src
     * @param radius
     * @return
     */
    public static Bitmap createRoundedCornerBitmap(Bitmap src, int radius) {
        final int w = src.getWidth();
        final int h = src.getHeight();
        // 高清量32位图
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xff424242);
        // 防止边缘的锯齿
        paint.setFilterBitmap(true);
        Rect rect = new Rect(0, 0, w, h);
        RectF rectf = new RectF(rect);
        // 绘制带圆角的矩形
        canvas.drawRoundRect(rectf, radius, radius, paint);

        // 取两层绘制交集，显示上层
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // 绘制图像
        canvas.drawBitmap(src, rect, rect, paint);
        return bitmap;
    }

    /**
     * 创建选中带提示图片
     *
     * @param context
     * @param srcId
     * @param tipId
     * @return
     */
    public static Drawable createSelectedTip(Context context, int srcId, int tipId) {
        Bitmap src = BitmapFactory.decodeResource(context.getResources(), srcId);
        Bitmap tip = BitmapFactory.decodeResource(context.getResources(), tipId);
        final int w = src.getWidth();
        final int h = src.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);
        //绘制原图
        canvas.drawBitmap(src, 0, 0, paint);
        //绘制提示图片
        canvas.drawBitmap(tip, (w - tip.getWidth()), 0, paint);
        return bitmapToDrawable(bitmap);
    }

    /**
     * 带倒影的图像
     *
     * @param src
     * @return
     */
    public static Bitmap createReflectionBitmap(Bitmap src) {
        // 两个图像间的空隙
        final int spacing = 4;
        final int w = src.getWidth();
        final int h = src.getHeight();
        // 绘制高质量32位图
        Bitmap bitmap = Bitmap.createBitmap(w, h + h / 2 + spacing, Bitmap.Config.ARGB_8888);
        // 创建燕X轴的倒影图像
        Matrix m = new Matrix();
        m.setScale(1, -1);
        Bitmap t_bitmap = Bitmap.createBitmap(src, 0, h / 2, w, h / 2, m, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        //  绘制原图像
        canvas.drawBitmap(src, 0, 0, paint);
        // 绘制倒影图像
        canvas.drawBitmap(t_bitmap, 0, h + spacing, paint);
        // 线性渲染-沿Y轴高到低渲染
        Shader shader = new LinearGradient(0, h + spacing, 0, h + spacing + h / 2, 0x70ffffff, 0x00ffffff, Shader.TileMode.MIRROR);
        paint.setShader(shader);
        // 取两层绘制交集，显示下层。
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // 绘制渲染倒影的矩形
        canvas.drawRect(0, h + spacing, w, h + h / 2 + spacing, paint);
        return bitmap;
    }


    /**
     * 独立的倒影图像
     *
     * @param src
     * @return
     */
    public static Bitmap createReflectionBitmapForSingle(Bitmap src) {
        final int w = src.getWidth();
        final int h = src.getHeight();
        // 绘制高质量32位图
        Bitmap bitmap = Bitmap.createBitmap(w, h / 2, Bitmap.Config.ARGB_8888);
        // 创建沿X轴的倒影图像
        Matrix m = new Matrix();
        m.setScale(1, -1);
        Bitmap t_bitmap = Bitmap.createBitmap(src, 0, h / 2, w, h / 2, m, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        // 绘制倒影图像
        canvas.drawBitmap(t_bitmap, 0, 0, paint);
        // 线性渲染-沿Y轴高到低渲染
        Shader shader = new LinearGradient(0, 0, 0, h / 2, 0x70ffffff,
                0x00ffffff, Shader.TileMode.MIRROR);
        paint.setShader(shader);
        // 取两层绘制交集。显示下层。
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // 绘制渲染倒影的矩形
        canvas.drawRect(0, 0, w, h / 2, paint);
        return bitmap;
    }


    public static Bitmap createGreyBitmap(Bitmap src) {
        final int w = src.getWidth();
        final int h = src.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        // 颜色变换的矩阵
        ColorMatrix matrix = new ColorMatrix();
        // saturation 饱和度值，最小可设为0，此时对应的是灰度图；为1表示饱和度不变，设置大于1，就显示过饱和
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(src, 0, 0, paint);
        return bitmap;
    }

    /**
     * 保存图片
     *
     * @param src
     * @param filepath
     * @param format:[Bitmap.CompressFormat.PNG,Bitmap.CompressFormat.JPEG]
     * @return
     */
    public static boolean saveImage(Bitmap src, String filepath, Bitmap.CompressFormat format) {
        boolean rs = false;
        File file = new File(filepath);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (src.compress(format, 100, out)) {
                out.flush();  //写入流
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * 添加水印效果
     *
     * @param src       源位图
     * @param watermark 水印
     * @param direction 方向
     * @param spacing   间距
     * @return
     */
    public static Bitmap createWatermark(Bitmap src, Bitmap watermark, int direction, int spacing) {
        final int w = src.getWidth();
        final int h = src.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(src, 0, 0, null);
        if (direction == LEFT_TOP) {
            canvas.drawBitmap(watermark, spacing, spacing, null);
        } else if (direction == LEFT_BOTTOM) {
            canvas.drawBitmap(watermark, spacing, h - watermark.getHeight() - spacing, null);
        } else if (direction == RIGHT_TOP) {
            canvas.drawBitmap(watermark, w - watermark.getWidth() - spacing, spacing, null);
        } else if (direction == RIGHT_BOTTOM) {
            canvas.drawBitmap(watermark, w - watermark.getWidth() - spacing, h - watermark.getHeight() - spacing, null);
        }
        return bitmap;
    }


    /**
     * 合成图像
     *
     * @param direction
     * @param bitmaps
     * @return
     */
    public static Bitmap composeBitmap(int direction, Bitmap... bitmaps) {
        if (bitmaps.length < 2) {
            return null;
        }
        Bitmap firstBitmap = bitmaps[0];
        for (int i = 0; i < bitmaps.length; i++) {
            firstBitmap = composeBitmap(firstBitmap, bitmaps[i], direction);
        }
        return firstBitmap;
    }

    /**
     * 合成两张图像
     *
     * @param firstBitmap
     * @param secondBitmap
     * @param direction
     * @return
     */
    private static Bitmap composeBitmap(Bitmap firstBitmap, Bitmap secondBitmap,
                                        int direction) {
        if (firstBitmap == null) {
            return null;
        }
        if (secondBitmap == null) {
            return firstBitmap;
        }
        final int fw = firstBitmap.getWidth();
        final int fh = firstBitmap.getHeight();
        final int sw = secondBitmap.getWidth();
        final int sh = secondBitmap.getHeight();
        Bitmap bitmap = null;
        Canvas canvas = null;
        if (direction == TOP) {
            bitmap = Bitmap.createBitmap(sw > fw ? sw : fw, fh + sh, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            canvas.drawBitmap(secondBitmap, 0, 0, null);
            canvas.drawBitmap(firstBitmap, 0, sh, null);
        } else if (direction == BOTTOM) {
            bitmap = Bitmap.createBitmap(fw > sw ? fw : sw, fh + sh, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            canvas.drawBitmap(firstBitmap, 0, 0, null);
            canvas.drawBitmap(secondBitmap, 0, fh, null);
        } else if (direction == LEFT) {
            bitmap = Bitmap.createBitmap(fw + sw, sh > fh ? sh : fh, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            canvas.drawBitmap(secondBitmap, 0, 0, null);
            canvas.drawBitmap(firstBitmap, sw, 0, null);
        } else if (direction == RIGHT) {
            bitmap = Bitmap.createBitmap(fw + sw, fh > sh ? fh : sh,
                    Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            canvas.drawBitmap(firstBitmap, 0, 0, null);
            canvas.drawBitmap(secondBitmap, fw, 0, null);
        }
        return bitmap;
    }


    //从文件中读取Bitmap
    public static Bitmap decodeBitmapWithOrientation(String pathName, int width, int height) {
        return decodeBitmapWithSize(pathName, width, height, false);
    }

    public static Bitmap decodeBitmapWithOrientationMax(String pathName, int width, int height) {
        return decodeBitmapWithSize(pathName, width, height, true);
    }

    private static Bitmap decodeBitmapWithSize(String pathName, int width, int height,
                                               boolean useBigger) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inInputShareable = true;
        options.inPurgeable = true;
        BitmapFactory.decodeFile(pathName, options);

        int decodeWidth = width, decodeHeight = height;
        final int degrees = getImageDegrees(pathName);
        if (degrees == 90 || degrees == 270) {
            decodeWidth = height;
            decodeHeight = width;
        }

        if (useBigger) {
            options.inSampleSize = (int) Math.min(((float) options.outWidth / decodeWidth),
                    ((float) options.outHeight / decodeHeight));
        } else {
            options.inSampleSize = (int) Math.max(((float) options.outWidth / decodeWidth),
                    ((float) options.outHeight / decodeHeight));
        }

        options.inJustDecodeBounds = false;
        Bitmap sourceBm = BitmapFactory.decodeFile(pathName, options);
        return imageWithFixedRotation(sourceBm, degrees);
    }

    public static int getImageDegrees(String pathName) {
        int degrees = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(pathName);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degrees = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degrees;
    }

    public static Bitmap imageWithFixedRotation(Bitmap bm, int degrees) {
        if (bm == null || bm.isRecycled())
            return null;

        if (degrees == 0)
            return bm;

        final Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap result = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        if (result != bm)
            bm.recycle();
        return result;

    }

    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }}

}
