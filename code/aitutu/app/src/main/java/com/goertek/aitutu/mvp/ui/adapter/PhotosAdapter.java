package com.goertek.aitutu.mvp.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.goertek.aitutu.R;
import com.goertek.aitutu.constant.Type;
import com.goertek.aitutu.mvp.model.entity.Photo;
import com.goertek.aitutu.mvp.ui.custom.PressedImageView;
import com.goertek.aitutu.mvp.ui.holder.AdViewHolder;
import com.goertek.aitutu.util.DurationUtils;
import com.goertek.aitutu.util.Result;
import com.goertek.aitutu.util.Setting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 专辑相册适配器
 * Created by huan on 2017/10/23.
 */
public class PhotosAdapter extends RecyclerView.Adapter {
    private static final int TYPE_AD = 0;
    private static final int TYPE_CAMERA = 1;
    private static final int TYPE_ALBUM_ITEMS = 2;

    private ArrayList<Object> dataList;
    private LayoutInflater mInflater;
    private OnClickListener listener;
    private boolean unable, isSingle;
    private int singlePosition;


    public PhotosAdapter(Context cxt, ArrayList<Object> dataList, OnClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
        this.mInflater = LayoutInflater.from(cxt);
        this.unable = Result.count() == Setting.count;
        this.isSingle = Setting.count == 1;
    }

    public void change() {
        unable = Result.count() == Setting.count;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_AD:
                return new AdViewHolder(mInflater.inflate(R.layout.item_ad_easy_photos, parent, false));
            case TYPE_CAMERA:
                return new CameraViewHolder(mInflater.inflate(R.layout.item_camera_easy_photos, parent, false));
            default:
                return new PhotoViewHolder(mInflater.inflate(R.layout.item_rv_photos_easy_photos, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final int p = position;
        if (holder instanceof PhotoViewHolder) {
            final Photo item = (Photo) dataList.get(p);
            if (item == null) return;
            boolean isSelected = Result.isSelected(item);
            updateSelector(((PhotoViewHolder) holder).tvSelector, isSelected, item, p);
            String path = item.path;
            String type = item.type;
            long duration = item.duration;
            final boolean isGif = path.endsWith(Type.GIF) || type.endsWith(Type.GIF);
            if (Setting.showGif && isGif) {
                Setting.imageEngine.loadGifAsBitmap(((PhotoViewHolder) holder).ivPhoto.getContext(), path, ((PhotoViewHolder) holder).ivPhoto);
                ((PhotoViewHolder) holder).tvType.setText(R.string.gif_easy_photos);
                ((PhotoViewHolder) holder).tvType.setVisibility(View.VISIBLE);
            } else if (Setting.showVideo() && type.contains(Type.VIDEO)) {
                Setting.imageEngine.loadPhoto(((PhotoViewHolder) holder).ivPhoto.getContext(), path, ((PhotoViewHolder) holder).ivPhoto);
                ((PhotoViewHolder) holder).tvType.setText(DurationUtils.format(duration));
                ((PhotoViewHolder) holder).tvType.setVisibility(View.VISIBLE);
            } else {
                Setting.imageEngine.loadPhoto(((PhotoViewHolder) holder).ivPhoto.getContext(), path, ((PhotoViewHolder) holder).ivPhoto);
                ((PhotoViewHolder) holder).tvType.setVisibility(View.GONE);
            }

            if (Setting.singleCheckedBack) {
                ((PhotoViewHolder) holder).vSelector.setVisibility(View.GONE);
                ((PhotoViewHolder) holder).tvSelector.setVisibility(View.GONE);
            } else {
                ((PhotoViewHolder) holder).vSelector.setVisibility(View.VISIBLE);
                ((PhotoViewHolder) holder).tvSelector.setVisibility(View.VISIBLE);
            }
            ((PhotoViewHolder) holder).ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Setting.singleCheckedBack) {
                        ((PhotoViewHolder) holder).vSelector.performClick();
                    } else {
                        int realPosition = p;
                        if (Setting.hasPhotosAd()) {
                            realPosition--;
                        }
                        if (Setting.isShowCamera && !Setting.isBottomRightCamera()) {
                            realPosition--;
                        }
                        listener.onPhotoClick(p, realPosition);
                    }
                }
            });


            ((PhotoViewHolder) holder).vSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSingle) {
                        singleSelector(item, p);
                        return;
                    }
                    boolean isSelected = Result.isSelected(item);
                    if (unable) {
                        if (isSelected) {
                            Result.removePhoto(item);
                            if (unable) {
                                unable = false;
                            }
                            listener.onSelectorChanged();
                            notifyDataSetChanged();
                            return;
                        }
                        listener.onSelectError(null);
                        return;
                    }
                    if (!isSelected) {
                        int res = Result.addPhoto(item);
                        if (res != 0) {
                            listener.onSelectError(res);
                            return;
                        }
                        ((PhotoViewHolder) holder).tvSelector.setBackgroundResource(R.drawable.bg_select_true_easy_photos);
                        ((PhotoViewHolder) holder).tvSelector.setText(String.valueOf(Result.count()));
                        if (Result.count() == Setting.count) {
                            unable = true;
                            notifyDataSetChanged();
                        }
                    } else {
                        Result.removePhoto(item);
                        if (unable) {
                            unable = false;
                        }
                        notifyDataSetChanged();
                    }
                    listener.onSelectorChanged();
                }
            });
            return;
        }

        if (holder instanceof AdViewHolder) {
            if (!Setting.photoAdIsOk) {
                ((AdViewHolder) holder).adFrame.setVisibility(View.GONE);
                return;
            }

            WeakReference weakReference = (WeakReference) dataList.get(p);

            if (null != weakReference) {
                View adView = (View) weakReference.get();
                if (null != adView) {
                    if (null != adView.getParent()) {
                        if (adView.getParent() instanceof FrameLayout) {
                            ((FrameLayout) adView.getParent()).removeAllViews();
                        }
                    }
                    ((AdViewHolder) holder).adFrame.setVisibility(View.VISIBLE);
                    ((AdViewHolder) holder).adFrame.removeAllViews();
                    ((AdViewHolder) holder).adFrame.addView(adView);
                }
            }
        }

        if (holder instanceof CameraViewHolder) {
            ((CameraViewHolder) holder).flCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCameraClick();
                }
            });
        }
    }

    private void singleSelector(Photo photo, int position) {
        if (!Result.isEmpty()) {
            if (Result.getPhotoPath(0).equals(photo.path) && !Setting.singleCheckedBack) {
                Result.removePhoto(photo);
                notifyItemChanged(position);
            } else {
                Result.removePhoto(0);
                Result.addPhoto(photo);
                notifyItemChanged(singlePosition);
                notifyItemChanged(position);
            }
        } else {
            Result.addPhoto(photo);
            notifyItemChanged(position);
        }
        listener.onSelectorChanged();
    }

    private void updateSelector(TextView tvSelector, boolean selected, Photo photo, int position) {
        if (selected) {
            String number = Result.getSelectorNumber(photo);
            if (number.equals("0")) {
                tvSelector.setBackgroundResource(R.drawable.bg_select_false_easy_photos);
                tvSelector.setText(null);
                return;
            }
            tvSelector.setText(number);
            tvSelector.setBackgroundResource(R.drawable.bg_select_true_easy_photos);
            if (isSingle) {
                singlePosition = position;
                tvSelector.setText("1");
            }
        } else {
            if (unable) {
                tvSelector.setBackgroundResource(R.drawable.bg_select_false_unable_easy_photos);
            } else {
                tvSelector.setBackgroundResource(R.drawable.bg_select_false_easy_photos);
            }
            tvSelector.setText(null);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (0 == position) {
            if (Setting.hasPhotosAd()) {
                return TYPE_AD;
            }
            if (Setting.isShowCamera && !Setting.isBottomRightCamera()) {
                return TYPE_CAMERA;
            }
        }
        if (1 == position && !Setting.isBottomRightCamera()) {
            if (Setting.hasPhotosAd() && Setting.isShowCamera) {
                return TYPE_CAMERA;
            }
        }
        return TYPE_ALBUM_ITEMS;
    }

    public interface OnClickListener {
        void onCameraClick();

        void onPhotoClick(int position, int realPosition);

        void onSelectError(@Nullable Integer result);

        void onSelectorChanged();
    }

    private class CameraViewHolder extends RecyclerView.ViewHolder {
        final FrameLayout flCamera;

        CameraViewHolder(View itemView) {
            super(itemView);
            this.flCamera = itemView.findViewById(R.id.fl_camera);
        }
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        final PressedImageView ivPhoto;
        final TextView tvSelector;
        final View vSelector;
        final TextView tvType;

        PhotoViewHolder(View itemView) {
            super(itemView);
            this.ivPhoto = itemView.findViewById(R.id.iv_photo);
            this.tvSelector = itemView.findViewById(R.id.tv_selector);
            this.vSelector = itemView.findViewById(R.id.v_selector);
            this.tvType = itemView.findViewById(R.id.tv_type);
        }
    }
}
