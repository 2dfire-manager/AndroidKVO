package com.ark.androidkvo.models.ref;

import com.ark.androidkvo.models.IKVO;
import com.ark.androidkvo.models.KVOListener;
import com.ark.androidkvo.models.KVORef;

import java.util.HashMap;

/**
 * Created by dove on 2017/8/28.
 * 包裹 HashMap 对象
 */
public class KVOMap<K,V extends IKVO> extends HashMap<K,V> implements KVORef{

    private KVOListener mKVOListener;

    @Override
    public V put(K key, V value) {
        V origin = super.get(key);
        V changedObj = super.put(key,value);
        if (mKVOListener != null && !origin.equals(changedObj)){
            mKVOListener.onValueChange(origin,changedObj,key.toString());
        }
        return changedObj;
    }

    @Override
    public void setListener(KVOListener kvoListener) {
        mKVOListener = kvoListener;
    }

    @Override
    public KVOMap cloneSelf() {
        return null;
    }
}
