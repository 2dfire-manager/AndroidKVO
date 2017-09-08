package com.ark.androidkvo.models;

/**
 * Created by dove on 2017/8/28.
 */

public interface KVORef<T extends IKVO> extends IKVO<T>{
    public KVOObserverObject getObserverObject(String field);
}
