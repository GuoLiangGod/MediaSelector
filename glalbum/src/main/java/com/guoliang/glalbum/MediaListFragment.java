package com.guoliang.glalbum;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.guoliang.glalbum.adapter.ListPopupWindowAdapter;
import com.guoliang.glalbum.adapter.MediaAdapter;
import com.guoliang.glalbum.entity.Album;
import com.guoliang.glalbum.entity.MediaFile;
import com.guoliang.glalbum.loder.AlbumLoader;
import com.guoliang.glalbum.loder.BaseLoader;
import com.guoliang.glalbum.loder.MediaLoader;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class MediaListFragment extends Fragment {

    private List<MediaFile> mediaFileList = new ArrayList<>();
    private List<Album> albumList = new ArrayList<>();
    private MediaAdapter mediaAdapter;
    private TextView tv_album;
    private AlbumLoader albumLoader;
    private MediaLoader mediaLoader;
    private Album selectAlbum = new Album();
    private MediaSelectConfig.SelectType selectType;
    private boolean isCamera;

    public MediaListFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle!=null) {
            isCamera=bundle.getBoolean(MediaSelectConfig.MEDIA_CAMERA, false);
            selectType = bundle.getParcelable(MediaSelectConfig.MEDIA_MIME_TYPE);
        }
        return inflater.inflate(R.layout.fragment_media_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recycler_media = view.findViewById(R.id.recycler_media);
        final SwipeRefreshLayout swipe_media = (SwipeRefreshLayout) view.findViewById(R.id.swipe_media);
        tv_album = view.findViewById(R.id.tv_album);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recycler_media.setLayoutManager(gridLayoutManager);
        mediaAdapter = new MediaAdapter(isCamera, mediaFileList, getContext());
        recycler_media.setAdapter(mediaAdapter);
        mediaAdapter.setOnItemClickListener(new MediaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent();
                intent.putExtra(MediaSelectConfig.EXTRA_RESULT_MEDIA_FILE,mediaFileList.get(position));
                if (getActivity()!=null) {
                    getActivity().setResult(RESULT_OK, intent);
                    getActivity().finish();
                }
            }

            @Override
            public void onClickCamera() {

            }
        });
        albumLoader = new AlbumLoader(getContext(), selectType);
        albumLoader.setAlbumLoaderCallBack(new BaseLoader.LoaderCallBack<Album>() {
            @Override
            public void dataCallBack(List<Album> list) {
                albumList.addAll(list);
            }
        });
        albumLoader.query();
        mediaLoader = new MediaLoader(getContext(), selectType);
        mediaLoader.setAlbumLoaderCallBack(new BaseLoader.LoaderCallBack<MediaFile>() {
            @Override
            public void dataCallBack(List<MediaFile> list) {
                mediaFileList.clear();
                mediaAdapter.notifyDataSetChanged();
                mediaFileList.addAll(list);
                mediaAdapter.notifyDataSetChanged();
                swipe_media.setRefreshing(false);
            }
        });
        mediaLoader.query(selectAlbum.getBucketId());
        tv_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListPopup();
            }
        });
        swipe_media.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mediaLoader.query(selectAlbum.getBucketId());
            }
        });
    }

    private void showListPopup() {
        if (getContext() != null) {
            final ListPopupWindow listPopupWindow = new ListPopupWindow(getContext());
            ListPopupWindowAdapter listPopupWindowAdapter = new ListPopupWindowAdapter(getContext(), albumList);
            listPopupWindow.setWidth(600);
            listPopupWindow.setHeight(1000);
            listPopupWindow.setAdapter(listPopupWindowAdapter);
            listPopupWindow.setAnchorView(tv_album);
            listPopupWindow.setModal(true);
            listPopupWindow.show();
            listPopupWindow.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectAlbum = albumList.get(position);
                    mediaLoader.query(selectAlbum.getBucketId());
                    tv_album.setText(selectAlbum.getBucketName());
                    listPopupWindow.dismiss();
                }
            });
        }
    }
}
