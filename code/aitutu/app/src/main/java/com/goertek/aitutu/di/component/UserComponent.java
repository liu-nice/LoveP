package com.goertek.aitutu.di.component;

import com.goertek.aitutu.di.module.UserModule;
import com.goertek.aitutu.mvp.contract.UserContract;
import com.goertek.aitutu.mvp.ui.activity.UserActivity;
import com.goertek.arm.di.component.AppComponent;
import com.goertek.arm.di.scope.ActivityScope;

import dagger.BindsInstance;
import dagger.Component;

@ActivityScope
@Component(modules = {UserModule.class}, dependencies = AppComponent.class)
public interface UserComponent {

    void inject(UserActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        UserComponent.Builder view(UserContract.View view);

        UserComponent.Builder appComponent(AppComponent appComponent);

        UserComponent build();
    }
}
