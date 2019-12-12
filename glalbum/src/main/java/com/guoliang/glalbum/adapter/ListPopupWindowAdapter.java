package com.guoliang.glalbum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.guoliang.glalbum.R;
import com.guoliang.glalbum.entity.Album;

import java.util.List;

/**
 * Author:     ZhangGuoLiang
 * CreateDate: 2019/12/12 10:32
 */
public class ListPopupWindowAdapter extends BaseAdapter {

    private List<Album> albumList;
    private Context mContext;

    public ListPopupWindowAdapter(Context context,List<Album> list) {
        super();
        this.albumList = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public Object getItem(int position) {
        return albumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adpter_album_item, null, false);
            holder.tv_album_name = convertView.findViewById(R.id.tv_album_name);
            holder.tv_album_count = convertView.findViewById(R.id.tv_album_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Album album = albumList.get(position);
        if (holder.tv_album_name != null) {
            holder.tv_album_name.setText(album.getBucketName());
            holder.tv_album_count.setText(String.valueOf(album.getCount()));
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tv_album_name;
        TextView tv_album_count;
    }

}
