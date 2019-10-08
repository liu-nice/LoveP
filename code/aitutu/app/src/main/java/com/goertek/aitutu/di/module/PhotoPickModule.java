package com.goertek.aitutu.di.module;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.goertek.aitutu.mvp.contract.PhotoPickContract;
import com.goertek.aitutu.mvp.model.PhotoPickModel;
import com.goertek.aitutu.mvp.model.entity.FolderInfo;
import com.goertek.aitutu.mvp.model.entity.ImageInfo;
import com.goertek.aitutu.mvp.presenter.PhotoPickPresenter;
import com.goertek.aitutu.mvp.ui.adapter.FolderAdapter;
import com.goertek.aitutu.mvp.ui.adapter.PhotoPickAdapter;
import com.goertek.arm.di.scope.ActivityScope;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class PhotoPickModule {

    @Binds
    abstract PhotoPickContract.Model bindPhotoPickModel(PhotoPickModel photoPickModel);

    @ActivityScope
    @Provides
    static RxPermissions provideRxPermissions(PhotoPickContract.View view) {
        return new RxPermissions((FragmentActivity) view.getActivity());
    }

    @Provides
    static Context provideContext(PhotoPickContract.View view) {
        return view.getActivity();
    }

    @ActivityScope
    @Provides
    static PhotoPickAdapter providePhotoPickAdapter(Context context) {
        return new PhotoPickAdapter(context);
    }

    @ActivityScope
    @Provides
    static FolderAdapter provideFolderAdapter(Context context) {
        return new FolderAdapter(context);
    }

    @ActivityScope
    @Provides
    static PhotoPickPresenter providePhotoPickPresenter(PhotoPickContract.Model model, PhotoPickContract.View rootView) {
        return new PhotoPickPresenter(model, rootView);
    }

    @ActivityScope
    @Provides
    static List<FolderInfo> provideResultFolder() {
        return new ArrayList<>();
    }

    @ActivityScope
    @Provides
    static List<ImageInfo> provideImages() {
        return new ArrayList<>();
    }
}
