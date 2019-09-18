package com.goertek.aitutu.di.module;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.goertek.aitutu.mvp.contract.UserContract;
import com.goertek.aitutu.mvp.model.UserModel;
import com.goertek.aitutu.mvp.model.entity.PhotoBean;
import com.goertek.aitutu.mvp.ui.adapter.UserAdapter;
import com.goertek.arm.di.scope.ActivityScope;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class UserModule {

    @Binds
    abstract UserContract.Model bindUserModel(UserModel userModule);

    @ActivityScope
    @Provides
    static RxPermissions provideRxPermissions(UserContract.View view) {
        return new RxPermissions((FragmentActivity) view.getActivity());
    }

    @ActivityScope
    @Provides
    static RecyclerView.LayoutManager provideLayoutManager(UserContract.View view) {
        return new GridLayoutManager(view.getActivity(), 2);
    }

    @ActivityScope
    @Provides
    static List<PhotoBean> providePhotoList() {
        return new ArrayList<>();
    }

    @ActivityScope
    @Provides
    static RecyclerView.Adapter provideUserAdapter(List<PhotoBean> list) {
        return new UserAdapter(list);
    }
}
