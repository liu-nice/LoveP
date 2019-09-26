package com.goertek.aitutu.mvp.model;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;

import com.goertek.aitutu.mvp.contract.UserContract;
import com.goertek.aitutu.mvp.model.entity.FolderBean;
import com.goertek.aitutu.mvp.model.entity.PhotoBean;
import com.goertek.aitutu.util.ImageUtils;
import com.goertek.arm.di.scope.ActivityScope;
import com.goertek.arm.integration.IRepositoryManager;
import com.goertek.arm.mvp.BaseModel;

import java.io.File;
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
    public Observable<List<PhotoBean>> getAllPhoto(Context context) {
        return Observable.create(new ObservableOnSubscribe<List<PhotoBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<PhotoBean>> emitter) throws Exception {
                List<FolderBean> list = ImageUtils.getAllImagePath(context);
                List l = new ArrayList<PhotoBean>();
                Timber.e("subscribe");
                for (int i = 0;i< list.size();i++) {
                    FolderBean bean = list.get(i);
                    File[] subFile = new File(bean.getDir()).listFiles();
                    for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                        //判断是否为文件夹
                        String path = subFile[iFileLength].getAbsolutePath();
                        if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith("jpeg")) {
                            PhotoBean p = new PhotoBean();
                            p.setUrl(path);
                          l.add(p);
                          Timber.e(path);
                        }
                    }
                }
                Timber.e("subscribe"  + l.size());
                emitter.onNext(l);
                emitter.onComplete();
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {
        Timber.d("Release Resource");
    }
}
