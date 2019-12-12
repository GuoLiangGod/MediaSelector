package com.guoliang.glalbum.loder;

import android.net.Uri;

import java.util.List;

/**
 * Author:     ZhangGuoLiang
 * CreateDate: 2019/12/1114:16
 */
public abstract class BaseLoader {
    /**
     * 查询URI
     *
     * @return
     */
    protected abstract Uri getScanUri();

    /**
     * 查询列名
     *
     * @return
     */
    protected abstract String[] getProjection();

    /**
     * 查询条件
     *
     * @return
     */
    protected abstract String getSelection();

    /**
     * 查询条件值
     *
     * @return
     */
    protected abstract Object[] getSelectionArgs();

    /**
     * 查询排序
     *
     * @return
     */
    protected abstract String getOrder();

    public interface LoaderCallBack<T>{
        void dataCallBack(List<T> list);
    }
}
