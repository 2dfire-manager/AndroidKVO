package com.ark.androidkvo.models.ref;

import com.ark.androidkvo.manager.KVOManager;
import com.ark.androidkvo.manager.Utils;
import com.ark.androidkvo.models.FieldObject;
import com.ark.androidkvo.models.IKVO;
import com.ark.androidkvo.models.KVOListener;
import com.ark.androidkvo.models.KVOObserverObject;
import com.ark.androidkvo.models.KVORef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dove on 2017/8/28.
 * 包裹 HashMap 对象
 * 与一般对象不同
 * 在 put 方法调用之前
 * 是无法预知到所有 field 内容的
 * 所以 observerObject 的创建需要同时放到 put 中
 */
public class KVOMap<K extends String,V extends IKVO> extends HashMap<K,V> implements KVORef<KVOMap>{

    List<String> allKVOFields = new ArrayList<String>();
    KVOManager mKVOManager;

    private KVOListener mKVOListener;

    private KVORef parent;

    private String mSelfField;

    public void setSelfField(String selfField) {
        mSelfField = selfField;
    }

    public String getSelfField() {
        return mSelfField;
    }

    public void setParent(KVORef parent) {
        this.parent = parent;
    }

    public KVORef getParent() {
        return parent;
    }

    @Override
    public V put(K  key, V value) {
        V oldValue = super.put(key,value);
        KVOObserverObject observerObject = null;
        if (!allKVOFields.contains(key)) {
            allKVOFields.add(key);
            KVOObserverObject newObserverObject = new KVOObserverObject();
            newObserverObject.setListener(mKVOListener);
            newObserverObject.setPropertyName(key);
            newObserverObject.setFieldId("");
            mKVOManager.addObserver(newObserverObject);
            observerObject = newObserverObject;
        }
        if (observerObject == null) observerObject = Utils.containProperty(mKVOManager,key);
        if (observerObject != null && observerObject.getListener() != null) {
            observerObject.getListener().onValueChange(this, value, observerObject.getPropertyName());
        } else if (observerObject != null && observerObject.getListener() == null){
            mKVOManager.removeObserver(observerObject);
        }
        notifyParent();
        return oldValue;
    }

    @Override
    public V remove(Object key) {
        KVOObserverObject observerObject = Utils.containProperty(mKVOManager, (String) key);
        if (observerObject != null) mKVOManager.removeObserver(observerObject);
        return super.remove(key);
    }

    @Override
    public void setListener(KVOListener kvoListener) {
        this.mKVOListener = kvoListener;
        for (K k : keySet()) {
            allKVOFields.add(k);
            KVOObserverObject observerObject = new KVOObserverObject();
            observerObject.setListener(kvoListener);
            observerObject.setPropertyName(k);
            observerObject.setFieldId("");
            if (!mKVOManager.getObservers().contains(observerObject)) {
                mKVOManager.addObserver(observerObject);
            }
        }
    }

    @Override
    public KVOMap cloneSelf() {
        KVOMap<K,V> result = new KVOMap<K,V>();
        for (K k : keySet()) {
            V ikvoIn = get(k);
            V resultKVO = (V) ikvoIn.cloneSelf();
            result.put(k, resultKVO);
        }
        return result;
    }

    @Override
    public boolean same(KVOMap kvoMap) {
        if (kvoMap == null) return false;
        boolean result = false;
        for (K k : keySet()) {
            IKVO ikvoIn = get(k);
            IKVO ikvoOut = (IKVO) kvoMap.get(k);
            if (ikvoOut == null){
                return false;
            }
            result =  ikvoIn.same(ikvoOut);
        }
        return result;
    }

    @Override
    public boolean updateSelfValue(KVOMap kvoMap, String fieldName) {
        if (kvoMap == null) return false;
        for (K k : keySet()) {
            if (!kvoMap.containsKey(k)){
                this.remove(k);
                continue;
            }

            IKVO ikvoIn = get(k);
            IKVO ikvoOut = (IKVO) kvoMap.get(k);
            if (ikvoOut == null){
                put(k,null);
            } else {
                if (!ikvoIn.same(ikvoIn)){
                    ikvoIn.updateSelfValue(ikvoOut,k);
                }
            }
        }
        return true;
    }

    @Override
    public void notifyParent() {
        if(parent == null) return;
        KVOObserverObject observerObject = this.parent.getObserverObject(mSelfField);
        if (observerObject != null && observerObject.getListener() != null) {
            observerObject.getListener().onValueChange(this.parent, this, mSelfField);
        }
        parent.notifyParent();
    }

    @Override
    public KVOObserverObject getObserverObject(String field) {
        return Utils.containProperty(mKVOManager,field);
    }

    protected KVOObserverObject initKVOProcess(){
        String fieldName = Utils.getFieldName(2);

        return Utils.containProperty(mKVOManager,fieldName);
    }
}
