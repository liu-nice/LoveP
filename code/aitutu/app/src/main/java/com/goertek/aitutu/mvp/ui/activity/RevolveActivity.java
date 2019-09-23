package com.goertek.aitutu.mvp.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goertek.aitutu.R;
import com.goertek.aitutu.util.PhotoUtils;
import com.goertek.arm.base.BaseActivity;
import com.goertek.arm.di.component.AppComponent;

import butterknife.BindView;
import butterknife.OnClick;

public class RevolveActivity extends BaseActivity {

    /**
     * 旋转90度
     */
    private static final int DEGREES_90 = 90;

    /**
     * 旋转-90度
     */
    private static final int DEGREES_FU_90 = -90;

    /**
     * 编辑图片
     */
    @BindView(R.id.activity_edit_image)
    private ImageView editImage;

    /**
     * 重置图片
     */
    @BindView(R.id.activity_edit_reset)
    private TextView editReset;

    /**
     * 左旋转
     */
    @BindView(R.id.activity_edit_left)
    private ImageView editLeft;

    /**
     * 右旋转
     */
    @BindView(R.id.activity_edit_right)
    private ImageView editRight;

    /**
     * 左右旋转
     */
    @BindView(R.id.activity_edit_left_right)
    private ImageView editLeftRight;

    /**
     * 上下旋转
     */
    @BindView(R.id.activity_edit_top_bottom)
    private ImageView editTopBottom;

    /**
     * 原图
     */
    private Bitmap srcBitmap;

    /**
     * 新图
     */
    private Bitmap newBitmap;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_edit_revolve;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        srcBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.lake);
        newBitmap = srcBitmap;
        editImage.setImageBitmap(newBitmap);
    }

    /**
     * 图片编辑的事件处理
     * @param view
     */
    @OnClick({R.id.activity_edit_reset, R.id.activity_edit_left, R.id.activity_edit_right,
            R.id.activity_edit_left_right, R.id.activity_edit_top_bottom})
    public void editEvent(View view) {
        switch (view.getId()){
            case R.id.activity_edit_reset:
                newBitmap = srcBitmap;
                editImage.setImageBitmap(newBitmap);
                break;
            case R.id.activity_edit_left:
                newBitmap = PhotoUtils.rotateImage(newBitmap, DEGREES_FU_90);
                editImage.setImageBitmap(newBitmap);
                break;
            case R.id.activity_edit_right:
                newBitmap = PhotoUtils.rotateImage(newBitmap, DEGREES_90);
                editImage.setImageBitmap(newBitmap);
                break;
            case R.id.activity_edit_left_right:
                newBitmap = PhotoUtils.reverseImage(newBitmap, -1, 1);
                editImage.setImageBitmap(newBitmap);
                break;
            case R.id.activity_edit_top_bottom:
                newBitmap = PhotoUtils.reverseImage(newBitmap, 1, -1);
                editImage.setImageBitmap(newBitmap);
                break;
            default:
                break;
        }
    }
}
