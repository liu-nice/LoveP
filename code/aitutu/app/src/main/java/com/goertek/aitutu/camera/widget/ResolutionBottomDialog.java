/*
 * Copyright  2016 - Goertek- All rights reserved.
 */
package com.goertek.aitutu.camera.widget;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;

import com.goertek.aitutu.R;
import com.goertek.aitutu.camera.util.CameraParam;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * describition :
 *
 * @author ;falzy.ning
 * @version :1.0.0
 * @since : 2019/10/17 10:52
 */
public class ResolutionBottomDialog extends BottomDialogView {
    //16:9
    @BindView(R.id.rb_resolution_16_9)
    RadioButton rbResolution_16_9;

    //4:3
    @BindView(R.id.rb_resolution_4_3)
    RadioButton rbResolution_4_3;

    //1:1
    @BindView(R.id.rb_resolution_1_1)
    RadioButton rbResolution_1_1;

    //比例点击回调
    private IResolutionView mListener;

    //这里的view其实可以替换直接传layout过来的 因为各种原因没传(lan)
    public ResolutionBottomDialog(Context context, int resId, int[] ratio) {
        super(context, resId);
        ECHOView(ratio);
    }

    /**
     * 回显当前选中的界面
     *
     * @param ratio 当前比例
     */
    private void ECHOView(int[] ratio) {
        float mRatio = (1.0F * ratio[1]) / ratio[0];
        if (mRatio == 0.75) {
            rbResolution_4_3.setChecked(true);
        } else if (mRatio == 0.5625) {
            rbResolution_16_9.setChecked(true);
        } else {
            rbResolution_1_1.setChecked(true);
        }
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

    public void setOnRadioCheckedChangedListener(IResolutionView listener) {
        this.mListener = listener;
    }

    public interface IResolutionView {
        void OnRadioCheckedChanged(int[] ratio, int width, int height);
    }
}
