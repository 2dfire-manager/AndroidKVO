package com.ark.androidkvo.models.ref;

import com.ark.androidkvo.models.IKVO;
import com.ark.androidkvo.models.KVOListener;

import java.util.HashMap;

/**
 * Created by dove on 2017/8/28.
 */

public class KVOMap<K,V extends IKVO> extends HashMap<K,V> implements IKVO{

    KVOListener mKVOListener;

    @Override
    public V put(K key, V value) {
        V origin = super.get(key);
        V changedObj = super.put(key,value);
        if (!origin.equals(changedObj)){
            mKVOListener.onValueChange(origin,changedObj,key.toString());
        }
        return changedObj;
    }

    @Override
    public void setListener(KVOListener kvoListener) {
        mKVOListener = kvoListener;
    }
}
