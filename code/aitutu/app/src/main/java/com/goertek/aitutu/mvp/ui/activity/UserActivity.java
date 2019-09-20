/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.goertek.aitutu.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.goertek.aitutu.R;
import com.goertek.aitutu.di.component.DaggerUserComponent;
import com.goertek.aitutu.mvp.contract.UserContract;
import com.goertek.aitutu.mvp.model.entity.FolderBean;
import com.goertek.aitutu.mvp.model.entity.Student;
import com.goertek.aitutu.mvp.presenter.UserPresenter;
import com.goertek.aitutu.util.ImageUtils;
import com.goertek.arm.base.BaseActivity;
import com.goertek.arm.base.BaseHolder;
import com.goertek.arm.base.DefaultAdapter;
import com.goertek.arm.di.component.AppComponent;
import com.goertek.arm.utils.ArmsUtils;
import com.goertek.arm.utils.Preconditions;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class UserActivity extends BaseActivity<UserPresenter> implements UserContract.View, SwipeRefreshLayout.OnRefreshListener, BaseHolder.OnViewClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Inject
    RxPermissions mRxPermissions;

    @Inject
    RecyclerView.LayoutManager mLayoutManager;

    @Inject
    RecyclerView.Adapter mAdapter;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        Timber.d("setupActivityComponent");
        DaggerUserComponent.builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        Log.d(TAG,"initView");
        return R.layout.activity_user;

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        Log.d(TAG,"initData");
        initRecyclerView();
        mRecyclerView.setAdapter(mAdapter);
        initPaginate();
    }

    private void initPaginate() {
        List<Student> list = new ArrayList<>();
        for (int i = 0;i < 10;i++) {
            Student s = new Student();
            s.setAge(i);
            s.setName("wanggangdan" + i);
            list.add(s);
        }
        LitePal.saveAll(list);

        List<Student> li = LitePal.findAll(Student.class);
        Timber.e("student" + li.size());
        Observable.create(new ObservableOnSubscribe<List<FolderBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<FolderBean>> emitter) throws Exception {
                emitter.onNext(ImageUtils.getAllImagePath(getActivity()));
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(new Observer<List<FolderBean>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<FolderBean> folderBeans) {
                for (int i = 0;i < folderBeans.size();i++) {
                    Timber.e(folderBeans.get(i).toString());
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void initRecyclerView() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        ArmsUtils.configRecyclerView(mRecyclerView, mLayoutManager);
    }

    @Override
    public void startLoadMore() {

    }

    @Override
    public void endLoadMore() {

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        Preconditions.checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onViewClick(View view, int position) {
        Timber.e(position+"");
    }

    @Override
    protected void onDestroy() {
        DefaultAdapter.releaseAllHolder(mRecyclerView);//super.onDestroy()之后会unbind,所有view被置为null,所以必须在之前调用
        super.onDestroy();
        this.mRxPermissions = null;
    }
}
