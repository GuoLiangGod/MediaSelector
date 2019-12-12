package com.guoliang.glalbum.loder;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.guoliang.glalbum.MediaSelectConfig;
import com.guoliang.glalbum.entity.MediaFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:     ZhangGuoLiang
 * CreateDate: 2019/12/1111:19
 */
public class MediaLoader extends BaseLoader {

    private LoaderCallBack<MediaFile> albumLoaderCallBack;
    private String mBucketId;

    @Override
    protected Uri getScanUri() {
        return MediaStore.Files.getContentUri("external");
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.DATE_TAKEN,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DURATION};
    }

    @Override
    protected String getSelection() {
        String selection;
        if (mSelectType == MediaSelectConfig.SelectType.ALL) {
            selection = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? or " +
                    MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";
        } else {
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + MediaStore.MediaColumns.SIZE + ">0";
        }
        if (!mBucketId.equals("-1")) {
            selection += " AND " + MediaStore.MediaColumns.BUCKET_ID + "=?";
        }
        return selection;
    }

    @Override
    protected String[] getSelectionArgs() {
        List<String> stringList = new ArrayList<>();
        switch (mSelectType) {
            case IMAGE:
                stringList.add(String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE));
                break;
            case VIDEO:
                stringList.add(String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO));
                break;
            default:
                stringList.add(String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE));
                stringList.add(String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO));
                break;
        }
        if (!mBucketId.equals("-1")) {
            stringList.add(mBucketId);
        }
        return stringList.toArray(new String[0]);
    }

    @Override
    protected String getOrder() {
        return MediaStore.Images.Media.DATE_TAKEN + " desc";
    }

    private MediaSelectConfig.SelectType mSelectType;
    private Context mContext;

    public MediaLoader(Context mContext, MediaSelectConfig.SelectType selectType) {
        this.mContext = mContext;
        this.mSelectType = selectType;
    }

    public void query(String bucketId) {
        mBucketId = bucketId;
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(getScanUri(), getProjection(), getSelection(), getSelectionArgs(), getOrder());
        List<MediaFile> mediaFileList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                String mime_type = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                long date_taken = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_TAKEN));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DURATION));
                MediaFile mediaFile = new MediaFile();
                mediaFile.setUri(ContentUris.withAppendedId(getScanUri(), id));
                mediaFile.setName(display_name);
                mediaFile.setMimeType(mime_type);
                mediaFile.setDateToken(date_taken);
                mediaFile.setSize(size);
                mediaFile.setDuration(duration);
                mediaFile.setMediaType(mSelectType);
                mediaFileList.add(mediaFile);
            }
            cursor.close();
            albumLoaderCallBack.dataCallBack(mediaFileList);
        }
    }

    public void setAlbumLoaderCallBack(LoaderCallBack<MediaFile> albumLoaderCallBack) {
        this.albumLoaderCallBack = albumLoaderCallBack;
    }
}
