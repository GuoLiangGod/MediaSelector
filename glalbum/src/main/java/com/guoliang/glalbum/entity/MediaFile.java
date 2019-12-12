package com.guoliang.glalbum.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.guoliang.glalbum.MediaSelectConfig;

/**
 * Author:     ZhangGuoLiang
 * CreateDate: 2019/12/1111:23
 */
public class MediaFile implements Parcelable {

    private Uri uri;
    private String name;
    private String mimeType;
    private long duration;
    private long size;
    private long dateToken;
    private MediaSelectConfig.SelectType mediaType;

    public MediaFile() {
    }

    public MediaFile(Uri uri, String name, String mimeType, long duration, long size, long dateToken) {
        this.uri = uri;
        this.name = name;
        this.mimeType = mimeType;
        this.duration = duration;
        this.size = size;
        this.dateToken = dateToken;
    }

    private MediaFile(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
        name = in.readString();
        mimeType = in.readString();
        duration = in.readLong();
        size = in.readLong();
        dateToken = in.readLong();
    }


    public static final Creator<MediaFile> CREATOR = new Creator<MediaFile>() {
        @Override
        public MediaFile createFromParcel(Parcel in) {
            return new MediaFile(in);
        }

        @Override
        public MediaFile[] newArray(int size) {
            return new MediaFile[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
        dest.writeString(name);
        dest.writeString(mimeType);
        dest.writeLong(duration);
        dest.writeLong(size);
        dest.writeLong(dateToken);
    }
    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDateToken() {
        return dateToken;
    }

    public void setDateToken(long dateToken) {
        this.dateToken = dateToken;
    }

    public MediaSelectConfig.SelectType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaSelectConfig.SelectType mediaType) {
        this.mediaType = mediaType;
    }

}
