package com.goertek.aitutu.mvp.contract;

import android.app.Activity;

import com.goertek.aitutu.mvp.model.entity.PhotoBean;
import com.goertek.arm.mvp.IModel;
import com.goertek.arm.mvp.IView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;

public interface UserContract {

    interface View extends IView {
        void startLoadMore();

        void endLoadMore();

        Activity getActivity();

        RxPermissions getRxPermissions();
    }

    interface Model extends IModel {
        Observable<List<PhotoBean>> getAllPhoto();
    }
}
