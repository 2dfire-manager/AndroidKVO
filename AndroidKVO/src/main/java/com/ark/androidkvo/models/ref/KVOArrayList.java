package com.ark.androidkvo.models.ref;

import com.ark.androidkvo.models.IKVO;
import com.ark.androidkvo.models.KVOListener;
import com.ark.androidkvo.models.KVORef;

import java.util.ArrayList;

/**
 * Created by dove on 2017/8/28.
 * 包裹 ArrayList 对象
 */
public class KVOArrayList<E extends IKVO> extends ArrayList<E> implements KVORef{

    private KVOListener mKVOListener;

    @Override
    public E set(int index, E element) {
        E origin = super.get(index);
        E e = super.set(index, element);
        if (mKVOListener != null ){
            if (origin == null ){
                if (element != null) {
                    mKVOListener.onValueChange(null, element, String.valueOf(index));
                }
            } else {
                if (!origin.equals(element)) {
                    mKVOListener.onValueChange(origin, element, String.valueOf(index));
                }
            }
        }
        return e;
    }

    @Override
    public void setListener(KVOListener kvoListener) {
        this.mKVOListener = kvoListener;
    }

    @Override
    public KVOArrayList cloneSelf() {
        return null;
    }
}
