/*
 * Copyright  2016 - Goertek- All rights reserved.
 */

package com.goertek.aitutu.camera.widget;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.goertek.aitutu.R;
import com.goertek.aitutu.camera.util.CameraParam;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * describition : 灰度调整
 *
 * @author ;falzy.ning
 * @version :1.0.0
 * @since : 2019/10/17 10:52
 */
public class GrayScaleBottomDialog extends BottomDialogView implements SeekBar.OnSeekBarChangeListener {
    //取消
    @BindView(R.id.grayScale_cancel)
    Button grayScaleCancel;

    //确定
    @BindView(R.id.grayScale_ok)
    Button grayScaleOk;

    //进度条
    @BindView(R.id.grayscale_progress)
    SeekBar grayscaleProgress;

    //比例点击回调
    private IBottomView mListener;

    //这里的view其实可以替换直接传layout过来的 因为各种原因没传(lan)
    public GrayScaleBottomDialog(Context context, int resId, int[] scale) {
        super(context, resId);
        grayscaleProgress.setOnSeekBarChangeListener(this);
        ECHOView(scale);
    }

    /**
     * 回显当前选中的界面
     * @param scale 当前比例
     */
    private void ECHOView(int[] scale) {
    }

    @OnClick(value = {R.id.grayScale_cancel, R.id.grayScale_ok})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.grayScale_ok:
            case R.id.grayScale_cancel:
                dismiss();
                unbinder();
                break;
            default:
                break;
        }
    }

    public void setOnRadioCheckedChangedListener(IBottomView listener) {
        this.mListener = listener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface IBottomView {
        void OnRadioCheckedChanged(int[] ratio, int width, int height);
    }
}
