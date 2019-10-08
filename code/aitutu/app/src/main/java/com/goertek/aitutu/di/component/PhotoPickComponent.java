package com.goertek.aitutu.di.component;

import com.goertek.aitutu.di.module.PhotoPickModule;
import com.goertek.aitutu.mvp.contract.PhotoPickContract;
import com.goertek.aitutu.mvp.ui.activity.MainPhotoPickActivity;
import com.goertek.arm.di.component.AppComponent;
import com.goertek.arm.di.scope.ActivityScope;

import dagger.BindsInstance;
import dagger.Component;

@ActivityScope
@Component(modules = {PhotoPickModule.class}, dependencies = AppComponent.class)
public interface PhotoPickComponent {

    void inject(MainPhotoPickActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        PhotoPickComponent.Builder view(PhotoPickContract.View view);

        PhotoPickComponent.Builder appComponent(AppComponent appComponent);

        PhotoPickComponent build();
    }
}
