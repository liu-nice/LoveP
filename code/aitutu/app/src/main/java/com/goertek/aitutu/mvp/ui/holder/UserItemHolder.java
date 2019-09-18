package com.goertek.aitutu.mvp.ui.holder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goertek.aitutu.R;
import com.goertek.aitutu.app.imageloader.ImageConfigImpl;
import com.goertek.aitutu.mvp.model.entity.PhotoBean;
import com.goertek.arm.base.BaseHolder;
import com.goertek.arm.di.component.AppComponent;
import com.goertek.arm.http.imageloader.ImageLoader;
import com.goertek.arm.utils.ArmsUtils;

import butterknife.BindView;

public class UserItemHolder extends BaseHolder<PhotoBean> {

    @BindView(R.id.iv_avatar)
    ImageView mAvatar;
    @BindView(R.id.tv_name)
    TextView mName;
    private ImageLoader mImageLoader;

    private AppComponent mAppComponent;

    public UserItemHolder(View itemView) {
        super(itemView);
        mAppComponent = ArmsUtils.obtainAppComponentFromContext(itemView.getContext());
        mImageLoader = mAppComponent.imageLoader();
    }

    @Override
    public void setData(@NonNull PhotoBean data, int position) {
        mAvatar.setImageResource(data.getId());
    }

    @Override
    protected void onRelease() {
        //只要传入的 Context 为 Activity, Glide 就会自己做好生命周期的管理, 其实在上面的代码中传入的 Context 就是 Activity
        //所以在 onRelease 方法中不做 clear 也是可以的, 但是在这里想展示一下 clear 的用法
        mImageLoader.clear(mAppComponent.application(), ImageConfigImpl.builder()
                .imageViews(mAvatar)
                .build());
        this.mAvatar = null;
        this.mName = null;
        this.mAppComponent = null;
        this.mImageLoader = null;
    }
}
