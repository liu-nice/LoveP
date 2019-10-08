package com.goertek.aitutu.mvp.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.goertek.aitutu.R;
import com.goertek.aitutu.mvp.model.entity.ImageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能说明
 *
 * @author: ww
 * @version: 1.0.0
 * @since: 2019/09/25
 */
public class PhotoPickAdapter extends RecyclerView.Adapter<PhotoPickAdapter.ViewHolder> {
    private static final String TAG = "PhotoPickAdapter";

    private Context mContext;

    private LayoutInflater mInflater;

    private List<ImageInfo> mImages = new ArrayList<ImageInfo>();

    private int mItemSize;

    //定义点击事件接口
    private onItemClick onitemClick;

    //定义设置点击事件监听的方法
    public void setOnitemClickLintener(onItemClick onitemClick) {
        this.onitemClick = onitemClick;
    }

    public PhotoPickAdapter(Context context) {
        this.mContext = context;
    }


    /**
     * 获取所有图片的路径集合
     *
     * @return
     */
    public ArrayList<String> getImagePathList() {
        ArrayList<String> imagePaths = new ArrayList<>();
        for (ImageInfo info : mImages) {
            imagePaths.add(info.path);
        }
        return imagePaths;
    }

    /**
     * 获取图片
     *
     * @param path 文件路径
     * @return 图片
     */
    public ImageInfo getImageByPath(String path) {
        if (mImages != null && mImages.size() > 0) {
            for (ImageInfo imageInfo : mImages) {
                if (imageInfo.path.equalsIgnoreCase(path)) {
                    return imageInfo;
                }
            }
        }
        return null;
    }

    /**
     * 重置每个Column的Size
     *
     * @param columnWidth 列宽
     */
    public void setItemSize(int columnWidth) {

        if (mItemSize == columnWidth) {
            return;
        }

        mItemSize = columnWidth;

        notifyDataSetChanged();
    }

    /**
     * 设置数据集
     *
     * @param imageInfos 图片集合
     */
    public void setData(List<ImageInfo> imageInfos) {
        if (imageInfos != null && imageInfos.size() > 0) {
            mImages.clear();
            mImages.addAll(imageInfos);
        }
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int postion) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.photopick_list_item_image, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bindData(mImages.get(position));
        if (onitemClick != null) {
            viewHolder.itemView.setOnClickListener(v -> onitemClick.onItemClick(mImages, position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    /**
     * 图片适配器ViewHoleder
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private View itemView;

        private ImageView ivPicture;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ivPicture = itemView.findViewById(R.id.image);
            itemView.setTag(this);
        }

        public void bindData(final ImageInfo data) {
            if (data == null) return;
            File imageFile = new File(data.path);
            // 显示图片
            Glide.with(mContext)
                    .load(imageFile)
                    .placeholder(R.drawable.photo_pic_loading)
                    .error(R.drawable.photo_pic_loading)
                    .into(ivPicture);
        }
    }

    //定义一个点击事件的接口
    public interface onItemClick {
        void onItemClick(List<ImageInfo> images, int position);
    }
}
