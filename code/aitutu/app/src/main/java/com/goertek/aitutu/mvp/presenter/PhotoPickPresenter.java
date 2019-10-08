package com.goertek.aitutu.mvp.presenter;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.database.Cursor;
import android.util.Log;

import com.goertek.aitutu.mvp.contract.PhotoPickContract;
import com.goertek.aitutu.mvp.model.PhotoPickModel;
import com.goertek.aitutu.mvp.model.entity.ImageEntity;
import com.goertek.aitutu.mvp.ui.activity.MainPhotoPickActivity;
import com.goertek.arm.di.scope.ActivityScope;
import com.goertek.arm.mvp.BasePresenter;
import com.goertek.arm.utils.RxLifecycleUtils;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import timber.log.Timber;

@ActivityScope
public class PhotoPickPresenter extends BasePresenter<PhotoPickContract.Model, PhotoPickContract.View> {
    private static final String TAG = "PhotoPickPresenter";

    MainPhotoPickActivity activity;

    @Inject
    RxErrorHandler mErrorHandler;

    @Inject
    public PhotoPickPresenter(PhotoPickContract.Model model, PhotoPickContract.View rootView) {
        super(model, rootView);
        activity = (MainPhotoPickActivity) rootView.getActivity();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate() {
        Log.d(TAG, "onCreate UserPresenter");

    }

    public void onLoadFinished(Cursor data) {
        mModel.getAllPhoto(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new Observer<ImageEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ImageEntity imageEntity) {
                        Timber.e("PhotoPickModel.Info" + imageEntity.getFolderInfoList().size());
                        activity.setImageInfos(imageEntity.getImageInfoList());
                        activity.setFolderInfos(imageEntity.getFolderInfoList());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

}
