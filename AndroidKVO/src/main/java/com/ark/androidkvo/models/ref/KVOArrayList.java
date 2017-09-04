package com.ark.androidkvo.models.ref;
import com.ark.androidkvo.annotations.AndroidKVO;
import com.ark.androidkvo.annotations.KVOField;

import java.util.ArrayList;

/**
 * Created by dove on 2017/8/28.
 */
@AndroidKVO
public class KVOArrayList<E> {

    @KVOField
    protected ArrayList<E> mArrayList;


    public ArrayList<E> getArrayList() {
        return mArrayList;
    }

    public void setArrayList(ArrayList<E> arrayList) {
        mArrayList = arrayList;
    }

}
