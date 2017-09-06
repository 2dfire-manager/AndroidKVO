package com.ark.androidkvo.models;

/**
 * Created by dove on 2017/8/25.
 * 线程安全上,切回主线程
 * 频繁通知刷新
 * 截留
 */
public interface IKVO<T extends IKVO> {
    void setListener(KVOListener kvoListener);
    T cloneSelf();
    boolean same(T t);
    boolean updateValue(T t);
}
