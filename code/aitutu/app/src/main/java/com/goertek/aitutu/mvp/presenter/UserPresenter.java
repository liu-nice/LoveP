package com.goertek.aitutu.mvp.presenter;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.goertek.aitutu.mvp.contract.UserContract;
import com.goertek.aitutu.mvp.model.entity.PhotoBean;
import com.goertek.arm.di.scope.ActivityScope;
import com.goertek.arm.integration.AppManager;
import com.goertek.arm.mvp.BasePresenter;
import com.goertek.arm.utils.PermissionUtil;
import com.goertek.arm.utils.RxLifecycleUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import timber.log.Timber;

@ActivityScope
public class UserPresenter extends BasePresenter<UserContract.Model, UserContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;

    @Inject
    AppManager appManager;

    @Inject
    Application application;

    @Inject
    List<PhotoBean> mPhotoList;

    @Inject
    RecyclerView.Adapter mAdapter;

    @Inject
    public UserPresenter(UserContract.Model model, UserContract.View rootView) {
        super(model, rootView);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate() {
        Log.d(TAG,"onCreate UserPresenter");
        requestPhotos();//打开 App 时自动加载列表
    }

    private void requestPhotos() {
        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
                //request permission success, do something.
                requestFromModel();
            }

            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                mRootView.showMessage("Request permissions failure");
                mRootView.hideLoading();//隐藏下拉刷新的进度条
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                mRootView.showMessage("Need to go to the settings");
                mRootView.hideLoading();//隐藏下拉刷新的进度条
            }
        }, mRootView.getRxPermissions(), mErrorHandler);
    }

    private void requestFromModel() {
        Timber.e("requestFromModel");
        mModel.getAllPhoto(application)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<List<PhotoBean>>(mErrorHandler) {
                    @Override
                    public void onNext(List<PhotoBean> photos) {
                        Timber.e("onNext:" + photos.size());
                        mPhotoList.clear();

                        mPhotoList.addAll(photos);

                        mAdapter.notifyDataSetChanged();

                        //mAdapter.notifyItemRangeInserted(preEndIndex, users.size());
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mAdapter = null;
        this.mPhotoList = null;
        this.mErrorHandler = null;
        this.appManager = null;
        this.application = null;
    }
}
