package com.goertek.aitutu.mvp.model;

import android.database.Cursor;

import com.goertek.aitutu.mvp.contract.PhotoPickContract;
import com.goertek.aitutu.mvp.model.entity.FolderInfo;
import com.goertek.aitutu.mvp.model.entity.ImageEntity;
import com.goertek.aitutu.mvp.model.entity.ImageInfo;
import com.goertek.aitutu.util.PhotoPickUtil;
import com.goertek.arm.di.scope.ActivityScope;
import com.goertek.arm.integration.IRepositoryManager;
import com.goertek.arm.mvp.BaseModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import timber.log.Timber;

@ActivityScope
public class PhotoPickModel extends BaseModel implements PhotoPickContract.Model {
    private static final String TAG = "PhotoPickModel";

    @Inject
    public PhotoPickModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Inject
    List<ImageInfo> imageInfos;

    @Inject
    List<FolderInfo> folderInfos;

    // 不同loader定义
    private static final int LOADER_ALL = 0;

    private static final int LOADER_CATEGORY = 1;

    public static final int REQUEST_CODE_CAMERA = 1;

    @Override
    public Observable<ImageEntity> getAllPhoto(Cursor data) {
        return Observable.create(emitter -> {
            if (data != null) {
                if (data.getCount() > 0) {
                    data.moveToFirst();
                    PhotoPickUtil.getAllImages(data, imageInfos, folderInfos);
                }
            }
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImageInfoList(imageInfos);
            imageEntity.setFolderInfoList(folderInfos);
            emitter.onNext(imageEntity);
            emitter.onComplete();
        });

    }


}
