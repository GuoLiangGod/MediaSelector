package com.guoliang.glalbum;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.lang.ref.WeakReference;

/**
 * Author:     ZhangGuoLiang
 * CreateDate: 2019/12/914:06
 */
public class MediaSelectConfig  {
    public enum SelectType implements Parcelable {
        ALL,VIDEO,IMAGE;

        public static final Creator<SelectType> CREATOR = new Creator<SelectType>() {
            @Override
            public SelectType createFromParcel(Parcel in) {
                return SelectType.values()[in.readInt()];
            }

            @Override
            public SelectType[] newArray(int size) {
                return new SelectType[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(ordinal());
        }
    }
    public final static String MEDIA_MIME_TYPE="media_mime_type";
    public final static String MEDIA_COUNTABLE="media_countable";
    public final static String MEDIA_CAMERA="media_camera";
    public final static String MEDIA_COUNT="media_count";
    public final static String EXTRA_RESULT_MEDIA_FILE="extra_result_media_file";

    private SelectType selectType = SelectType.ALL;
    //多选
    private boolean countable=false;
    //拍照
    private boolean camera=false;
    //可选数量
    private int count=9;

    private final WeakReference<Activity> mActivity;

    public MediaSelectConfig(Activity activity) {
        this.mActivity = new WeakReference<>(activity);
    }

    public static MediaSelectConfig from(Activity activity){
        return new MediaSelectConfig(activity);
    }

    /**
     * 选择器类型
     * @param type
     * @return
     */
    public MediaSelectConfig selectType(SelectType type){
        this.selectType =type;
        return this;
    }

    /**
     * 是否可以多选
     * @param countable
     * @return
     */
    public MediaSelectConfig countable(boolean countable) {
        this.countable=countable;
        return this;
    }

    /**
     * 是否有拍照
     * @param camera
     * @return
     */
    public MediaSelectConfig camera(boolean camera){
        this.camera=camera;
        return this;
    }

    /**
     * 可选数量
     * @param count
     * @return
     */
    public MediaSelectConfig count(int count){
        this.count=count;
        return this;
    }

    public void forResult(int requestCode){
        Activity activity = mActivity.get();
        Intent intent = new Intent(activity, SelectMediaActivity.class);
        intent.putExtra(MEDIA_MIME_TYPE, (Parcelable) selectType);
        intent.putExtra(MEDIA_COUNTABLE,countable);
        intent.putExtra(MEDIA_CAMERA,camera);
        activity.startActivityForResult(intent,requestCode);
    }
}
