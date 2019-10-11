package com.goertek.aitutu.mvp.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.goertek.aitutu.R;
import com.goertek.aitutu.mvp.ui.fragment.StickerFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerHolder> {

    public DisplayImageOptions imageOption = new DisplayImageOptions.Builder()
            .cacheInMemory(true).showImageOnLoading(R.drawable.yd_image_tx)
            .build();// 下载图片显示

    private OnItemClickListener onItemClickListener = null;

    private StickerFragment mStickerFragment;

    private List<String> pathList = new ArrayList<String>();// 图片路径列表

    public StickerAdapter(StickerFragment fragment) {
        super();
        mStickerFragment = fragment;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public StickerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        StickerHolder stickerHolder = new StickerHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sticker_layout,viewGroup,false));
        return stickerHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StickerHolder stickerHolder, int position) {
        Log.e("weip","assets resource "+pathList.get(position));
//        Glide.with(mStickerFragment).load("assets://"+pathList.get(position)).into(stickerHolder.imageView);
        ImageLoader.getInstance().displayImage("assets://" + pathList.get(position), stickerHolder.imageView, imageOption);

        stickerHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(view,pathList.get(position),position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pathList.size();
    }

    public void addStickerImages(String folderPath) {
        pathList.clear();
        try {
            String[] files = mStickerFragment.getActivity().getAssets().list(folderPath);
            for (String name : files) {
                pathList.add(folderPath + File.separator + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    static class StickerHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public StickerHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_sticker_image);
        }
    }

    //回调接口
    public interface OnItemClickListener{
        void onItemClick(View view,String photoPath,int position);
    }


}
