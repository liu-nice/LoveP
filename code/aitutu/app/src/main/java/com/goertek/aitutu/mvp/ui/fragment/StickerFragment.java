/*
 * Copyright (c) 2019 -Goertek -All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.fragment;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.ViewFlipper;

import com.goertek.aitutu.R;
import com.goertek.aitutu.mvp.task.StickerTask;
import com.goertek.aitutu.mvp.ui.activity.ImageEditActivity;
import com.goertek.aitutu.mvp.ui.adapter.StickerAdapter;
import com.goertek.aitutu.mvp.ui.adapter.StickerTypeAdapter;
import com.goertek.aitutu.mvp.ui.custom.ModuleConfig;
import com.goertek.aitutu.mvp.ui.custom.StickerItem;
import com.goertek.aitutu.mvp.ui.custom.StickerView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

public class StickerFragment extends BaseFragment {

    public static final int INDEX = ModuleConfig.INDEX_STICKER;

    private View mainView;
    private ViewFlipper flipper;
    private SeekBar seekBar;
    //返回主菜单
    private View backToMenu;
    //贴图分类列表
    private RecyclerView typeList;
    //贴图素材列表
    private RecyclerView stickerList;
    //返回类型选择
    private View backToType;
    //贴图显示控件
    private StickerView mStickerView;
    //贴图列表适配器
    private StickerAdapter mStickerAdapter;
    //保存贴纸task
    private SaveStickersTask mSaveTask;

    /**
     * 贴纸fragment
     *
     * @return 贴纸fragment实例
     */
    public static StickerFragment newInstance() {
        StickerFragment fragment = new StickerFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_sticker_layout,null);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        File cacheDir = StorageUtils.getCacheDirectory(getActivity());
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity()).memoryCacheExtraOptions(480,800).defaultDisplayImageOptions(defaultOptions)
                .diskCacheExtraOptions(480,800,null).threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(maxMemory / 5))
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(getActivity())) // default
                .imageDecoder(new BaseImageDecoder(false)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()).build();
        ImageLoader.getInstance().init(config);

        mStickerView = activity.mStickerView;
        seekBar = mainView.findViewById(R.id.fragment_sticker_layout_seekbar);
        flipper = (ViewFlipper) mainView.findViewById(R.id.flipper);
        //
        backToMenu = mainView.findViewById(R.id.back_to_main);
        typeList = (RecyclerView) mainView.findViewById(R.id.stickers_type_list);
        typeList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        typeList.setLayoutManager(mLayoutManager);
        typeList.setAdapter(new StickerTypeAdapter(this));

        backToType = mainView.findViewById(R.id.back_to_type);// back按钮

        stickerList = (RecyclerView) mainView.findViewById(R.id.stickers_list);
        stickerList.setHasFixedSize(true);
        LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(activity);
        stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        stickerList.setLayoutManager(stickerListLayoutManager);
        mStickerAdapter = new StickerAdapter(this);
        stickerList.setAdapter(mStickerAdapter);
        mStickerAdapter.setOnItemClickListener(new StickerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view,String photoPath,int position) {
                mStickerView.addBitImage(getImageFromAssetsFile(photoPath));
            }
        });

        backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单
        backToType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// 返回上一级列表
                flipper.showPrevious();
            }
        });
        //设置贴纸透明度(0~255)
        seekBar.setMax(255);
        seekBar.setProgress(255);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser) {
                mStickerView.updateStickerAlpha(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 跳转到贴图详情列表
     *
     * @param path
     */
    public void swipToStickerDetails(String path) {
        mStickerAdapter.addStickerImages(path);
        flipper.showNext();
    }

    /**
     * 从Assert文件夹中读取位图数据
     *
     * @param fileName
     * @return
     */
    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    @Override
    public void onShow() {
        activity.mode = ImageEditActivity.MODE_STICKERS;
        activity.mStickerFragment.getmStickerView().setVisibility(View.VISIBLE);
    }

    /**
     * 返回贴纸view
     *
     * @return
     */
    public StickerView getmStickerView() {
        return mStickerView;
    }

    private final class BackToMenuClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            backToMain();
        }
    }

    @Override
    public void backToMain() {
        activity.mode = ImageEditActivity.MODE_NONE;
        activity.mBottomViewPager.setCurrentItem(0);
        mStickerView.setVisibility(View.GONE);
    }

    /**
     * 贴纸
     */
    public void applyStickers() {
        if (mSaveTask != null) {
            mSaveTask.cancel(true);
        }
        mSaveTask = new SaveStickersTask((ImageEditActivity) getActivity());
        mSaveTask.execute(activity.getMainBit());
    }

    private final class SaveStickersTask extends StickerTask {

        public SaveStickersTask(ImageEditActivity activity) {
            super(activity);
        }

        @Override
        public void handleImage(Canvas canvas,Matrix matrix) {
            LinkedHashMap<Integer, StickerItem> addItems = mStickerView.getBank();
            for (Integer id : addItems.keySet()) {
                StickerItem item = addItems.get(id);
                //乘以底部图片变化矩阵
                item.matrix.postConcat(matrix);
                canvas.drawBitmap(item.bitmap,item.matrix,null);
            }// end for
        }

        @Override
        public void onPostResult(Bitmap result) {
            mStickerView.clear();
            activity.changeMainBitmap(result);
        }
    }
}
