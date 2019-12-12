package com.guoliang.glalbum.loder;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.guoliang.glalbum.MediaSelectConfig;
import com.guoliang.glalbum.entity.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author:     ZhangGuoLiang
 * CreateDate: 2019/12/1113:46
 */
public class AlbumLoader extends BaseLoader {

    private final MediaSelectConfig.SelectType mSelectType;
    private String COLUMN_COUNT = "count";

    @Override
    protected Uri getScanUri() {
        return MediaStore.Files.getContentUri("external");
    }

    @Override
    protected String[] getProjection() {
        if (isAndroidQ()) {
            return new String[]{
                    MediaStore.MediaColumns._ID,
                    MediaStore.MediaColumns.BUCKET_ID,
                    MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.MediaColumns.DATE_ADDED,
                    MediaStore.MediaColumns.MIME_TYPE};
        } else {
            return new String[]{
                    MediaStore.MediaColumns._ID,
                    MediaStore.MediaColumns.BUCKET_ID,
                    MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.MediaColumns.DATE_ADDED,
                    MediaStore.MediaColumns.MIME_TYPE,
                    "COUNT(*) AS " + COLUMN_COUNT};
        }
    }

    @Override
    protected String getSelection() {
        String selection;
        if (mSelectType == MediaSelectConfig.SelectType.ALL) {
            if (isAndroidQ()) {
                selection = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? or " +
                        MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                        + " AND " + MediaStore.MediaColumns.SIZE + ">0";
            } else {
                selection = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? or " +
                        MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                        + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                        + ") GROUP BY (bucket_id";
            }
        } else {
            if (isAndroidQ()) {
                selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + MediaStore.MediaColumns.SIZE + ">0";
            } else {
                selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + MediaStore.MediaColumns.SIZE + ">0"
                        + ") GROUP BY (bucket_id";
            }
        }
        return selection;
    }

    @Override
    protected String[] getSelectionArgs() {
        String[] selectionArgs;
        switch (mSelectType) {
            case IMAGE:
                selectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)};
                break;
            case VIDEO:
                selectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
                break;
            default:
                selectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
                break;
        }
        return selectionArgs;
    }

    @Override
    protected String getOrder() {
        return MediaStore.Images.Media.DATE_TAKEN + " desc";
    }


    private Context mContext;
    private LoaderCallBack<Album> albumLoaderCallBack;

    public AlbumLoader(Context mContext,MediaSelectConfig.SelectType selectType) {
        mSelectType = selectType;
        this.mContext = mContext;
    }

    public void query() {
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(getScanUri(), getProjection(), getSelection(), getSelectionArgs(), getOrder());
        int allCount=0;
        List<Album> albumList = new ArrayList<>();
        if (cursor != null) {
            if (isAndroidQ()) {
                // Pseudo GROUP BY
                Map<Long, Integer> countMap = new HashMap<>();
                while (cursor.moveToNext()) {
                    long bucketId = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_ID));
                    Integer count = countMap.get(bucketId);
                    if (count == null) {
                        count = 1;
                    } else {
                        count++;
                    }
                    countMap.put(bucketId, count);
                }
                if (cursor.moveToFirst()) {
                    Set<Long> done = new HashSet<>();
                    do {
                        long bucket_id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_ID));
                        if (done.contains(bucket_id)) {
                            continue;
                        }
                        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                        String bucket_display_name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME));
                        long date_added = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED));
                        Integer count = countMap.get(bucket_id);
                        Album album = new Album();
                        album.setUri(ContentUris.withAppendedId(getScanUri(), id));
                        album.setBucketId(Long.toString(bucket_id));
                        album.setBucketName(bucket_display_name);
                        album.setDateAdded(date_added);
                        album.setCount(count == null ? 0 : count);
                        albumList.add(album);
                        done.add(bucket_id);
                        allCount+=album.getCount();
                    } while (cursor.moveToNext());
                }
            } else {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                    long bucket_id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_ID));
                    String bucket_display_name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME));
                    long date_added = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED));
                    int count = cursor.getInt(cursor.getColumnIndex(COLUMN_COUNT));
                    Album album = new Album();
                    album.setUri(ContentUris.withAppendedId(getScanUri(), id));
                    album.setBucketId(Long.toString(bucket_id));
                    album.setBucketName(bucket_display_name);
                    album.setDateAdded(date_added);
                    album.setCount(count);
                    albumList.add(album);
                    allCount+=album.getCount();
                }
            }
            cursor.close();
            Album album = new Album();
            album.setBucketName("全部");
            album.setCount(allCount);
            albumList.add(0,album);
            albumLoaderCallBack.dataCallBack(albumList);
        }
    }

    public void setAlbumLoaderCallBack(LoaderCallBack<Album> albumLoaderCallBack) {
        this.albumLoaderCallBack = albumLoaderCallBack;
    }

    /**
     * @return 是否是 Android 10 （Q） 的版本
     */
    private static boolean isAndroidQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }
}
