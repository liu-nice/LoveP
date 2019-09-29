package com.goertek.aitutu.mvp.contract;

import android.app.Activity;
import android.content.Context;

import android.database.Cursor;
import android.support.v4.content.Loader;

import com.goertek.aitutu.mvp.model.PhotoPickModel;
import com.goertek.aitutu.mvp.model.entity.FolderInfo;
import com.goertek.aitutu.mvp.model.entity.ImageEntity;
import com.goertek.aitutu.mvp.model.entity.ImageInfo;
import com.goertek.aitutu.mvp.model.entity.PhotoBean;
import com.goertek.arm.base.BaseActivity;
import com.goertek.arm.mvp.IModel;
import com.goertek.arm.mvp.IView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;

public interface PhotoPickContract {

    interface View extends IView {
        BaseActivity getActivity();

        RxPermissions getRxPermissions();
    }

    interface Model extends IModel {
        Observable<ImageEntity> getAllPhoto(Cursor data);
    }
}
