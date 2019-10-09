/*
 * Copyright  2019 - Goertek- All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goertek.aitutu.R;
import com.goertek.aitutu.camera.MainActivity;
import com.goertek.aitutu.di.component.DaggerPhotoPickComponent;
import com.goertek.aitutu.mvp.contract.PhotoPickContract;
import com.goertek.aitutu.mvp.model.entity.FolderInfo;
import com.goertek.aitutu.mvp.model.entity.ImageInfo;
import com.goertek.aitutu.mvp.presenter.PhotoPickPresenter;
import com.goertek.aitutu.mvp.ui.adapter.FolderAdapter;
import com.goertek.aitutu.mvp.ui.adapter.PhotoPickAdapter;
import com.goertek.aitutu.util.PhotoPickUtil;
import com.goertek.aitutu.util.ToastUtils;
import com.goertek.arm.base.BaseActivity;
import com.goertek.arm.di.component.AppComponent;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

/**
 * 首页Activity
 *
 * @author: ww
 * @version: 1.0.0
 * @since: 2019/09/27
 */
public class MainPhotoPickActivity extends BaseActivity<PhotoPickPresenter>
        implements PhotoPickContract.View, EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MainPhotoPickActivity";

    // RecyclerView 滑动状态
    private static final int SCROLL_STATE_IDLE = 0;

    // RecyclerView 滑动状态
    private static final int SCROLL_STATE_TOUCH_SCROLL = 1;

    //系统权限请求码
    private static final int REQUEST_CODE_PERMISSION = 0;

    //相机权限请求码
    private static final int REQUEST_CODE_CAMERA = 1;

    // 不同loader定义
    private static final int LOADER_ALL = 0;

    //延迟时间
    private static final int DALAY_MILLS = 100;

    // 是否有文件夹
    private boolean hasFolderGened;

    private TextView mTvFolderName;

    private ImageView mIvArrow;

    private PopupWindow mPopupWindow;

    // 系统相机返回码
    private static final int TAKE_PICTURE_FROM_CAMERA = 2;

    // RecyclerView 列数
    public static final int LAYOUTMANAGER_SPANCOUNT = 3;

    private ListView mFolderListView;

    @BindView(R.id.photo_pick_rv)
    RecyclerView mRecyclerView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.open_camara)
    CircleImageView openCamara;

    @Inject
    RxPermissions mRxPermissions;

    @Inject
    PhotoPickPresenter mPhotoPickPresenter;

    @Inject
    PhotoPickAdapter mPhotoPickAdapter;

    @Inject
    FolderAdapter mFolderAdapter;

    // 文件夹数据
    @Inject
    List<FolderInfo> mResultFolder;
    // 图片数据
    @Inject
    List<ImageInfo> imageInfos;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        Timber.d("setupActivityComponent");
        DaggerPhotoPickComponent.builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main_photo_pick;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);
        setToolBar(mToolbar);
        requestPhoto();
        initRecyclerView();
        initAdapter();
        setListener();
    }

    /**
     * 获取相册图片
     */
    @AfterPermissionGranted(REQUEST_CODE_PERMISSION)
    public void requestPhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问读写权限", REQUEST_CODE_PERMISSION, perms);
        }

    }

    private void initRecyclerView() {
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(LAYOUTMANAGER_SPANCOUNT, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            ToastUtils.toast(this, "您拒绝了读取图片的权限");
        }

        if (requestCode == REQUEST_CODE_CAMERA) {
            ToastUtils.toast(this, "您拒绝了相机权限");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
            Loader<Cursor> cursorLoader = PhotoPickUtil.getCursorLoader(MainPhotoPickActivity.this, id, bundle);
            if (cursorLoader != null) {
                return cursorLoader;
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mPresenter.onLoadFinished(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    private void initAdapter() {
        mRecyclerView.setAdapter(mPhotoPickAdapter);
        mPhotoPickAdapter.setOnitemClickLintener((images, position) -> ToastUtils.toast(MainPhotoPickActivity.this, "你点击了第" + position + "个条目 ！！！" + " &path ==" + images.get(position).path));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE || newState == SCROLL_STATE_TOUCH_SCROLL) {
                    //停止滚动时加载图片
                    Glide.with(MainPhotoPickActivity.this).resumeRequests();
                } else {
                    //滚动时暂停加载图片
                    Glide.with(MainPhotoPickActivity.this).pauseRequests();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_pick, menu);
        customToolBarStyle();
        return true;
    }

    private void setToolBar(Toolbar toolbar) {
        toolbar.setBackgroundColor(Color.parseColor("#d4237a"));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void customToolBarStyle() {
        MenuItem item = mToolbar.getMenu().findItem(R.id.photo_pick_menu);
        View actionView = item.getActionView();
        actionView.setBackgroundColor(Color.parseColor("#d4237a"));
        mTvFolderName = actionView.findViewById(R.id.photo_picker_menu_item_title);
        mIvArrow = actionView.findViewById(R.id.photo_picker_menu_item_arrow);
        if (mShowImageFolderListener != null) {
            mTvFolderName.setOnClickListener(mShowImageFolderListener);
            mIvArrow.setOnClickListener(mShowImageFolderListener);
        }
    }

    private View.OnClickListener mShowImageFolderListener = view -> showPopupFolder();

    /**
     * 选择图片文件夹对话框
     */
    private void showPopupFolder() {
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(MainPhotoPickActivity.this);
            mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
            View view = getLayoutInflater().inflate(R.layout.photopick_popup_folder, null);
            mPopupWindow.showAtLocation(mToolbar, Gravity.TOP, 0, 0);
            mPopupWindow.setAnimationStyle(R.style.anim_photo_select);
            mPopupWindow.setContentView(view);
            mFolderListView = view.findViewById(R.id.lsv_folder);
            mFolderListView.setAdapter(mFolderAdapter);
            view.setOnClickListener(v1 -> mPopupWindow.dismiss());
            mFolderListView.setOnItemClickListener((parent, view1, position, id) -> {
                mFolderAdapter.setSelectIndex(position);
                final int index = position;
                new Handler().postDelayed(() -> {
                    mPopupWindow.dismiss();
                    if (index == 0) {
                        requestPhoto();
                        mTvFolderName.setText(getString(R.string.photo_album_all));
                    } else {
                        FolderInfo folderInfo = mFolderAdapter.getItem(index);
                        if (null != folderInfo) {
                            mPhotoPickAdapter.setData(folderInfo.imageInfos);
                            mTvFolderName.setText(folderInfo.name);
                        }
                    }
                    // 滑动到最初始位置
                    mRecyclerView.smoothScrollToPosition(0);

                }, DALAY_MILLS);
            });
        }
        mPopupWindow.showAsDropDown(mToolbar);
    }

    @Override
    public BaseActivity getActivity() {
        return this;
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
    }

    @Override
    public void showMessage(@NonNull String message) {

    }

    /**
     * 设置选中的文件夹
     *
     * @param imageInfos 所有图片数据
     */
    public void setImageInfos(List<ImageInfo> imageInfos) {
        mPhotoPickAdapter.setData(imageInfos);
    }

    /**
     * 设置文件夹数据
     *
     * @param folderInfos 文件夹数据
     */
    public void setFolderInfos(List<FolderInfo> folderInfos) {
        mFolderAdapter.setData(folderInfos);
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION)
    @OnClick(R.id.open_camara)
    public void onViewClicked() {
        //打开相机
        showCameraAction();
    }

    @AfterPermissionGranted(REQUEST_CODE_CAMERA)
    private void showCameraAction() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 跳转到系统照相机
//            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(cameraIntent, TAKE_PICTURE_FROM_CAMERA);
            startActivity(new Intent(MainPhotoPickActivity.this, MainActivity.class));
        } else {
            EasyPermissions.requestPermissions(this, "照相机需要以下权限:\n\n1.照相", REQUEST_CODE_CAMERA, perms);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
