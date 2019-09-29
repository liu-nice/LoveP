package com.goertek.aitutu.mvp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goertek.aitutu.R;
import com.goertek.aitutu.mvp.model.entity.FolderInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件夹Adapter
 *
 * @author: ww
 * @version: 1.0.0
 * @since: 2019/09/27
 */
public class FolderAdapter extends BaseAdapter {
    private static final String TAG = "FolderAdapter";

    private Context mContext;

    private LayoutInflater mInflater;

    private static final int MIMAGE_SIZE = 72;

    private List<FolderInfo> mFolders = new ArrayList<FolderInfo>();

    private int mImageSize;

    private int lastSelected = 0;

    /**
     * 文件夹适配器
     *
     * @param context 上下文对象
     */
    public FolderAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageSize = MIMAGE_SIZE;
    }

    /**
     * 设置数据集
     *
     * @param folderInfos 文件夹数据
     */
    public void setData(List<FolderInfo> folderInfos) {
        if (folderInfos != null && folderInfos.size() > 0) {
            mFolders = folderInfos;
        } else {
            mFolders.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFolders.size() + 1;
    }

    @Override
    public FolderInfo getItem(int position) {
        if (position == 0) {
            return null;
        }
        return mFolders.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.list_item_folder, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (holder != null) {
            if (position == 0) {
                holder.name.setText(mContext.getResources().getString(R.string.photo_album_all));
                holder.size.setText(getTotalImageSize() + "张");
                if (mFolders.size() > 0) {
                    FolderInfo f = mFolders.get(0);
                    Glide.with(mContext)
                            .load(new File(f.cover.path))
                            .error(R.drawable.photo_pic_loading)
                            .override(mImageSize, mImageSize)
                            .centerCrop()
                            .into(holder.cover);

                }

            } else {
                holder.bindData(getItem(position));
            }
            if (lastSelected == position) {
                holder.indicator.setVisibility(View.VISIBLE);
            } else {
                holder.indicator.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }

    private int getTotalImageSize() {
        int result = 0;
        if (mFolders != null && mFolders.size() > 0) {
            for (FolderInfo f : mFolders) {
                result += f.imageInfos.size();
            }
        }
        return result;
    }

    /**
     * 设置文件夹选中数据
     *
     * @param position 选中的文件夹
     */
    public void setSelectIndex(int position) {
        if (lastSelected == position) return;

        lastSelected = position;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return lastSelected;
    }

    /***
     * 文件夹ViewHolder
     */
    class ViewHolder {
        ImageView cover;
        TextView name;
        TextView size;
        ImageView indicator;

        ViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.cover);
            name = (TextView) view.findViewById(R.id.name);
            size = (TextView) view.findViewById(R.id.size);
            indicator = (ImageView) view.findViewById(R.id.indicator);
            view.setTag(this);
        }

        void bindData(FolderInfo data) {
            name.setText(data.name);
            size.setText(data.imageInfos.size() + "张");
            // 显示图片
            Glide.with(mContext)
                    .load(new File(data.cover.path))
                    .placeholder(R.drawable.photo_pic_loading)
                    .override(mImageSize, mImageSize)
                    .centerCrop()
                    .into(cover);
        }
    }

}
