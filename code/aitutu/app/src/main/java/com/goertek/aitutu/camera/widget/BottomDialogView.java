/*
 * Copyright  2016 - Goertek- All rights reserved.
 */
package com.goertek.aitutu.camera.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.goertek.aitutu.R;
import com.goertek.aitutu.camera.util.CameraParam;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * describition :
 *
 * @author ;falzy.ning
 * @version :1.0.0
 * @since : 2019/10/12 14:47
 */
public class BottomDialogView extends Dialog {
    //16:9
    @BindView(R.id.rb_resolution_16_9)
    RadioButton rbResolution_16_9;

    //4:3
    @BindView(R.id.rb_resolution_4_3)
    RadioButton rbResolution_4_3;

    //1:1
    @BindView(R.id.rb_resolution_1_1)
    RadioButton rbResolution_1_1;

    //当前显示布局
    private View view;

    //上下文
    private Context context;

    //比例点击回调
    private IBottomView mListener;

    //butterknife实例
    private Unbinder unbinder;

    //这里的view其实可以替换直接传layout过来的 因为各种原因没传(lan)
    public BottomDialogView(Context context, int resId, int[] ratio) {
        super(context, R.style.BottomDialog);
        this.context = context;
        this.view = LayoutInflater.from(context).inflate(resId, null);
        unbinder = ButterKnife.bind(this, view);
        ECHOView(ratio);
    }

    /**
     * 回显当前选中的界面
     * @param ratio 当前比例
     */
    private void ECHOView(int[] ratio) {
        float mRatio = (1.0F * ratio[1]) / ratio[0];
        if(mRatio == 0.75) {
            rbResolution_4_3.setChecked(true);
        }else if(mRatio == 0.5625){
            rbResolution_16_9.setChecked(true);
        }else {
            rbResolution_1_1.setChecked(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setDimAmount(0f);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }

    @OnClick(value = {R.id.resolution_ok, R.id.resolution_cancel,
            R.id.rb_resolution_16_9, R.id.rb_resolution_4_3, R.id.rb_resolution_1_1})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.resolution_ok:
            case R.id.resolution_cancel:
                dismiss();
                unbinder();
                break;
            case R.id.rb_resolution_4_3:
                if (((RadioButton) view).isChecked()) {
                    int[] currentRatio = new int[]{4, 3};
                    mListener.OnRadioCheckedChanged(currentRatio, CameraParam.DEFAULT_4_3_WIDTH, CameraParam.DEFAULT_4_3_HEIGHT);
                }
                break;
            case R.id.rb_resolution_1_1:
                if (((RadioButton) view).isChecked()) {
                    int[] currentRatio = new int[]{1, 1};
                    mListener.OnRadioCheckedChanged(currentRatio, CameraParam.DEFAULT_1_1_WIDTH, CameraParam.DEFAULT_1_1_HEIGHT);
                }
                break;
            case R.id.rb_resolution_16_9:
                if (((RadioButton) view).isChecked()) {
                    int[] currentRatio = new int[]{16, 9};
                    mListener.OnRadioCheckedChanged(currentRatio, CameraParam.DEFAULT_16_9_WIDTH, CameraParam.DEFAULT_16_9_HEIGHT);
                }
                break;
            default:
                break;
        }
    }

    public void setOnRadioCheckedChangedListener(IBottomView listener) {
        this.mListener = listener;
    }

    public interface IBottomView {
        void OnRadioCheckedChanged(int[] ratio, int width, int height);
    }

    /**
     * 解绑
     */
    private void unbinder() {
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
