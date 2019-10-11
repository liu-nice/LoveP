package com.goertek.aitutu.mvp.ui.custom.doodle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义用于涂鸦的view
 * @author wanghong
 * @version 1.0
 * @since 2019-09-20
 */

public class DoodleView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "DoodleView";

    // surfaceHolder
    private SurfaceHolder mSurfaceHolder = null;

    // 当前所选画笔的形状
    private BaseAction curAction = null;

    // 默认画笔为黑色
    private int currentColor = Color.BLACK;

    // 画笔的粗细
    private int currentSize = 5;

    // 画笔
    private Paint mPaint;

    // 橡皮擦
    private Paint mEraserPaint;

    private List<BaseAction> mBaseActions;

    private Bitmap mBitmap;

    private int mMode;

    private ActionType mActionType = ActionType.Path;

    public DoodleView(Context context) {
        super(context);
        init();
    }

    public DoodleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);
//        setBackgroundResource(R.drawable.ic_photo);

        // 使surfaceview放到最顶层
        setZOrderOnTop(true);
        // 使窗口支持透明度
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        mPaint = new Paint();
        mEraserPaint = new Paint();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        // 绘制透明色
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mSurfaceHolder.unlockCanvasAndPost(canvas);
        mBaseActions = new ArrayList<>();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL) {
            return false;
        }

        float touchX = event.getX();
        float touchY = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mMode == 0) {
                    setCurAction(touchX, touchY);
                } else {
                    curAction = new DoodleEraser(touchX, touchY, currentSize, currentColor);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Canvas canvas = mSurfaceHolder.lockCanvas();
                // canvas.drawColor(Color.TRANSPARENT) 这样有闪烁问题
                if (mMode == 0) {
                    // 绘制透明色
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    for (BaseAction baseAction : mBaseActions) {
                        baseAction.draw(canvas);
                    }
                    curAction.move(touchX, touchY);
                    curAction.draw(canvas);
                } else {
                    // 绘制透明色
                    canvas.drawColor(Color.TRANSPARENT);
                    for (BaseAction baseAction : mBaseActions) {
                        baseAction.draw(canvas);
                    }
                    curAction.move(touchX, touchY);
                    curAction.draw(canvas);
                }
                mSurfaceHolder.unlockCanvasAndPost(canvas);
                break;
            case MotionEvent.ACTION_UP:
                mBaseActions.add(curAction);
                curAction = null;
                break;

            default:
                break;
        }
        return true;
    }

    /**
     * 得到当前画笔的类型，并进行实例化
     *
     * @param xPoint x坐标值
     * @param yPoint y坐标值
     */
    private void setCurAction(float xPoint, float yPoint) {
        switch (mActionType) {
            case Path:
                curAction = new DoodlePath(xPoint, yPoint, currentSize, currentColor);
                break;
            case Line:
                curAction = new DoodleLine(xPoint, yPoint, currentSize, currentColor);
                break;
            case DottedLine:
                curAction = new DoodleDottedLine(xPoint, yPoint, currentSize, currentColor);
                break;
            case Circle:
                curAction = new DoodleCircle(xPoint, yPoint, currentSize, currentColor);
                break;
            case Triangle:
                curAction = new DoodleTriangle(xPoint, yPoint, currentSize, currentColor);
                break;
            default:
                break;
        }
    }

    /**
     * 设置画笔的颜色
     *
     * @param color 颜色
     */
    public void setColor(String color) {
        this.currentColor = Color.parseColor(color);
    }

    /**
     * 设置画笔的粗细
     *
     * @param size 画笔的粗细
     */
    public void setSize(int size) {
        this.currentSize = size;
    }

    /**
     * 设置画笔的形状
     *
     * @param type 画笔的形状
     */
    public void setType(ActionType type) {
        this.mActionType = type;
    }

    /**
     * 将当前的画布转换成一个 Bitmap
     *
     * @return Bitmap
     */
    public Bitmap getBitmap() {
        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        doDraw(canvas);
        return mBitmap;
    }

    /**
     * 保存涂鸦后的图片
     *
     * @param doodleView 自定义view对象
     * @return 图片的保存路径
     */
    public String saveBitmap(DoodleView doodleView) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Download/" + System.currentTimeMillis() + ".png";
        if (!new File(path).exists()) {
            new File(path).getParentFile().mkdir();
        }
        savePicByPNG(doodleView.getBitmap(), path);
        return path;
    }

    /**
     * 将一个 Bitmap 保存在一个指定的路径中
     *
     * @param bitmap 保存的btimap
     * @param filePath 保存图片的路径
     */
    public static void savePicByPNG(Bitmap bitmap, String filePath) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(filePath);
            if (null != fileOutputStream) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 开始进行绘画
     * @param canvas 画板
     */
    private void doDraw(Canvas canvas) {
        // 画背景图片
        draw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        for (BaseAction action : mBaseActions) {
            action.draw(canvas);
        }
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);

    }


    /**
     * 回退
     * @return 是否已经回退成功
     */
    public boolean back(){
        if(mBaseActions != null && mBaseActions.size() > 0){
            mBaseActions.remove(mBaseActions.size() - 1);
            Canvas canvas = mSurfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            for (BaseAction action : mBaseActions) {
                action.draw(canvas);
            }
            mSurfaceHolder.unlockCanvasAndPost(canvas);
            return true;
        }
        return false;
    }

    /**
     * 重置
     */
    public void reset(){
        if(mBaseActions != null && mBaseActions.size() > 0){
            mBaseActions.clear();
            Canvas canvas = mSurfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            for (BaseAction action : mBaseActions) {
                action.draw(canvas);
            }
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * 橡皮擦
     */
    public void eraser() {
        Log.e("DoodleView", "Eraser");
    }

    /**
     * @param mode 设置画笔类型
     */
    public void setMode(int mode) {
        mMode = mode;
    }

    /**
     * Path, Line, DottedLine, Circle, Triangle
     */
    public enum ActionType {
        Path, Line, DottedLine, Circle, Triangle
    }
}






















