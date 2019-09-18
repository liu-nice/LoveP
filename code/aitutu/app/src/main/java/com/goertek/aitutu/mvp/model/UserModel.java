package com.goertek.aitutu.mvp.model;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;

import com.goertek.aitutu.R;
import com.goertek.aitutu.mvp.contract.UserContract;
import com.goertek.aitutu.mvp.model.entity.PhotoBean;
import com.goertek.arm.di.scope.ActivityScope;
import com.goertek.arm.integration.IRepositoryManager;
import com.goertek.arm.mvp.BaseModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import timber.log.Timber;

@ActivityScope
public class UserModel extends BaseModel implements UserContract.Model {

    @Inject
    public UserModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<List<PhotoBean>> getAllPhoto() {
        return Observable.create(new ObservableOnSubscribe<List<PhotoBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<PhotoBean>> emitter) throws Exception {
                List list = new ArrayList<PhotoBean>();
                Timber.e("subscribe");
                for (int i = 0;i< 10;i++) {
                    PhotoBean bean = new PhotoBean();
                    bean.setId(R.mipmap.ic_launcher_round);
                    list.add(bean);
                }
                Timber.e("subscribe"  + list.size());
                emitter.onNext(list);
                emitter.onComplete();
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {
        Timber.d("Release Resource");
    }
}
