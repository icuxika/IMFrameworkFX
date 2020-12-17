package com.icuxika.api;

/**
 * 请求进度监听器
 */
public interface RequestProgressListener {

    /**
     * 上传进度通知
     *
     * @param workDone 已完成上传大小
     * @param max      总共需要上传大小
     */
    void onRequestProgress(long workDone, long max);

}
