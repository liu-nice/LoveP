/*
 * Copyright (c) 2019 -Goertek -All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.goertek.aitutu.R;

import java.util.ArrayList;
import java.util.List;

public class StickersAdapter extends RecyclerView.Adapter<StickersAdapter.StickerHolder>{

    private Context mContext;

    static List<Integer> stickers = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static {
        stickers.add(R.drawable.sticker_best);
        stickers.add(R.drawable.sticker_chuizi);
        stickers.add(R.drawable.sticker_eye);
        stickers.add(R.drawable.sticker_goodnight);
        stickers.add(R.drawable.sticker_heart);
        stickers.add(R.drawable.sticker_kissmybaby);
        stickers.add(R.drawable.sticker_qiezi);
        stickers.add(R.drawable.sticker_shengrikuaile);
        stickers.add(R.drawable.sticker_success);
        stickers.add(R.drawable.sticker_sun);
        stickers.add(R.drawable.sticker_sweet);
        stickers.add(R.drawable.sticker_xiong);
        stickers.add(R.drawable.sticker_yummy);
    }

    public StickersAdapter(Context context){
        mContext =context;
    }

    @NonNull
    @Override
    public StickerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,int position) {
        StickerHolder stickerHolder = new StickerHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sticker_layout,viewGroup,false));
        return stickerHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StickerHolder stickerHolder,int position) {
        stickerHolder.imageView.setImageResource(stickers.get(position));
        stickerHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(stickers.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stickers.size();
    }

    class StickerHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public StickerHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_sticker_image);
        }
    }

    //回调接口
    public interface OnItemClickListener{
        void onItemClick(int imageSource);
    }
}
