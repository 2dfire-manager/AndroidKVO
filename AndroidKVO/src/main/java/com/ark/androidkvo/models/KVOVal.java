package com.ark.androidkvo.models;

/**
 * Created by dove on 2017/8/28.
 */

public interface KVOVal<T,V extends IKVO> extends IKVO<V>{
    T getValue();
}
