package com.ark.androidkvo.manager;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.ark.androidkvo.models.KVOListener;
import com.ark.androidkvo.models.KVOObserverObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dove on 2017/9/7.
 */

public class Utils {
    public static String getFieldName(int pos) {
        int position = 0;
        for(int x = 0; x < Thread.currentThread().getStackTrace().length ; x++){
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[x];
            if(stackTraceElement.getMethodName().equals("getFieldName")){
                position = x;
                break;
            }
        }
        String methodName = Thread.currentThread().getStackTrace()[position+ pos].getMethodName();
        return methodName.substring(3);
    }

    public static List<KVOListener> getListenerForId(KVOManager manager,String id) {
        List<KVOListener> targetList = new ArrayList<>();
        List<WeakReference<KVOListener>> sourceList = manager.getIdentifiedObservers().get(id);
        if (sourceList != null && !sourceList.isEmpty()) {
            for (Iterator<WeakReference<KVOListener>> iterator = sourceList.iterator(); iterator.hasNext(); ) {
                KVOListener observerObject = iterator.next().get();
                if (observerObject != null) {
                    if (observerObject instanceof Activity)
                        if (((Activity) observerObject).isFinishing()) {
                            iterator.remove();
                            return null;
                        }
                    if (observerObject instanceof Fragment)
                        if (((Fragment) observerObject).getActivity().isFinishing()) {
                            iterator.remove();
                            return null;
                        }
                    if (observerObject instanceof android.app.Fragment)
                        if (((android.app.Fragment) observerObject).getActivity().isFinishing()) {
                            iterator.remove();
                            return null;
                        }
                    targetList.add(observerObject);
                } else if (observerObject == null) {
                    iterator.remove();
                }
            }
        }

        return targetList;
    }

    public static KVOObserverObject containProperty(KVOManager manager, String propertyName){
        for (Iterator<KVOObserverObject> iterator = manager.getObservers().iterator(); iterator.hasNext(); ) {
            KVOObserverObject observerObject = iterator.next();
            if (observerObject.getPropertyName().equalsIgnoreCase(propertyName) && observerObject.getListener() != null) {
                if(observerObject.getListener() instanceof Activity)
                    if(((Activity)observerObject.getListener()).isFinishing()){
                        iterator.remove();
                        return null;
                    }
                if(observerObject.getListener() instanceof Fragment)
                    if(((Fragment)observerObject.getListener()).getActivity().isFinishing()){
                        iterator.remove();
                        return null;
                    }
                if(observerObject.getListener() instanceof android.app.Fragment)
                    if(((android.app.Fragment)observerObject.getListener()).getActivity().isFinishing()){
                        iterator.remove();
                        return null;
                    }
                return observerObject;
            }else if(observerObject.getListener() == null){
                iterator.remove();
            }
        }
        return null;
    }
}
