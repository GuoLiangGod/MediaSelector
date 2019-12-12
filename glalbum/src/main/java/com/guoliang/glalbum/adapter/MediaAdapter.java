package com.guoliang.glalbum.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.guoliang.glalbum.R;
import com.guoliang.glalbum.entity.MediaFile;

import java.util.List;

/**
 * Author:     ZhangGuoLiang
 * CreateDate: 2019/12/1117:50
 */
public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    public static final int ITEM_TYPE_CAMERA = 1;
    public static final int ITEM_TYPE_IMAGE = 2;
    public static final int ITEM_TYPE_VIDEO = 3;
    private boolean isCamera;
    private List<MediaFile> mediaFileList;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public MediaAdapter(boolean isCamera, List<MediaFile> mediaFileList, Context mContext) {
        this.isCamera = isCamera;
        this.mediaFileList = mediaFileList;
        this.mContext = mContext;
    }

    @Override
    public int getItemViewType(int position) {
        if (isCamera){
            if (position==0){
                return ITEM_TYPE_CAMERA;
            }
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==ITEM_TYPE_CAMERA){
            View view = LayoutInflater.from(mContext).inflate(R.layout.adpter_camera_item, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClickCamera();
                }
            });
            return new ViewHolder(view);
        }else {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adpter_media_item,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position)==ITEM_TYPE_CAMERA){

        }else {
            final int mPosition ;
            if (isCamera) {
                mPosition=holder.getAdapterPosition()-1;
            }else {
                mPosition=holder.getAdapterPosition();
            }
            MediaFile mediaFile = mediaFileList.get(mPosition);
            Glide.with(mContext)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(mediaFile.getUri())
                    .apply(new RequestOptions()
                            .centerCrop())
                    .into(holder.iv_media_cover);
            if (mediaFile.getDuration()>0){
                holder.tv_media_duration.setText(DateUtils.formatElapsedTime(mediaFile.getDuration() / 1000));
                holder.tv_media_duration.setVisibility(View.VISIBLE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(mPosition);
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return isCamera?mediaFileList.size()+1:mediaFileList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_media_cover;
        TextView tv_media_duration;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_media_cover=itemView.findViewById(R.id.iv_media_cover);
            tv_media_duration=itemView.findViewById(R.id.tv_media_duration);
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
        void onClickCamera();
    }
}
