package com.ark.androidkvo.models.val;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.ark.androidkvo.annotations.KVOField;
import com.ark.androidkvo.manager.KVOManager;
import com.ark.androidkvo.models.FieldObject;
import com.ark.androidkvo.models.KVOListener;
import com.ark.androidkvo.models.KVOObserverObject;
import com.ark.androidkvo.models.KVOVal;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dove on 2017/8/28.
 */

public final class StringKVO  implements Serializable,KVOVal {

    protected java.lang.String value;


    ArrayList<FieldObject> allKVOFields = new ArrayList<FieldObject>() {{
        add(new FieldObject("value",""));
    }};
    public enum FieldName{
        value
    }

    public StringKVO(String s){
        this.value = s;
    }

    @Override
    public java.lang.String getValue() {
        return value;
    }

    /**
     * you can use this method to set a callback for a certain field
     * All You have to do is pass object that implement {@link KVOListener} and pass the field name using {@link FieldName}
     * Which you can find inside the generated class you can access it like this generatedClass.FieldName.Field where generated class is your className+KVO
     * and Field is your field name the purpose of that in case you change the field name you got a compilation error instead of searching for fields name
     * as strings
     *
     * @param listener
     * @param property
     */
    public void setListener(KVOListener listener , FieldName property){
        boolean fieldExist = false;
        String fieldId = "";
        for (FieldObject fieldObj : allKVOFields) {
            if(property.toString().equals(fieldObj.getFieldName())){
                fieldExist = true;
                fieldId = fieldObj.getFieldID();
                break;
            }
        }
        if(!fieldExist)
            throw new RuntimeException("Field with name " + property.name() + " does not exist or it maybe private");
        KVOObserverObject observerObject = new KVOObserverObject();
        observerObject.setListener(listener);
        observerObject.setFieldId(fieldId);
        observerObject.setPropertyName(property.name());
        if(!fieldId.equals("")){
            KVOManager.getInstance().addIdentifiedObserver(fieldId, listener);
        }else {
            if (!KVOManager.getInstance().getObservers().contains(observerObject)) {
                KVOManager.getInstance().addObserver(observerObject);
            }
        }
    }
    /**
     * you can use this method to listen for all the changes that happen on all fields that is annotated by {@link KVOField}
     * if you need a certain field instead use {@link #setListener(KVOListener, FieldName)}
     * @param listener
     */
    public void setListener(KVOListener listener){
        for(FieldObject field : allKVOFields){
            KVOObserverObject observerObject = new KVOObserverObject();
            observerObject.setListener(listener);
            observerObject.setPropertyName(field.getFieldName());
            observerObject.setFieldId(field.getFieldID());
            if(!field.getFieldID().equals("")){
                KVOManager.getInstance().addIdentifiedObserver(field.getFieldID(), listener);
            }else {
                if (!KVOManager.getInstance().getObservers().contains(observerObject)) {
                    KVOManager.getInstance().addObserver(observerObject);
                }
            }
        }
    }

    @Override
    public StringKVO cloneSelf() {
        return new StringKVO(this.value);
    }

    /**
     * use this method to remove the callback listener
     * @param kvoListener
     */
    public void removeListener(KVOListener kvoListener){

        for (Iterator<KVOObserverObject> iterator = KVOManager.getInstance().getObservers().iterator(); iterator.hasNext();) {
            KVOObserverObject observerObject = iterator.next();
            if (observerObject.getListener().equals(kvoListener)) {
                // Remove the current element from the iterator and the list.
                iterator.remove();
            }
        }
        KVOManager.getInstance().removeIdentifiedObserver(kvoListener);
    }

    /**
     * you can use this method to listen for all the changes that happen to a certain field annotated with certain ID
     * {@link KVOField#id()}
     * please note that all the fields annoteted with same ID will trigger the listener so make sure that the id is unique
     * @param listener
     * @param id
     */
    public void setListenerForId(KVOListener listener, String id) {
        boolean fieldExist = false;
        String fieldName = "";
        for (FieldObject fieldObj : allKVOFields) {
            if(id.equals(fieldObj.getFieldID())){
                fieldExist = true;
                break;
            }
        }
        if (!fieldExist)
            throw new RuntimeException("Field with id " + id + " does not exist or it maybe private");
        KVOManager.getInstance().addIdentifiedObserver(id , listener);
    }
    public void setValue(java.lang.String param) {
        this.value = param;
        KVOObserverObject observerObject = initKVOProcess();
        if (observerObject != null && observerObject.getListener() != null) {
            observerObject.getListener().onValueChange(this, param, observerObject.getPropertyName());
        } else if (observerObject != null && observerObject.getListener() == null){
            KVOManager.getInstance().removeObserver(observerObject);
        } else {
            checkIdInManager(param);
        }
    }

    /**
     * class use this method to try to find an observer which is registered to this param
     * if it found one it will notify it that the value has changed
     * @param param
     */
    private void checkIdInManager(Object param){
        for (FieldObject field : allKVOFields) {
            if (field.getFieldName().equalsIgnoreCase(getFieldName())) {
                if (!field.getFieldID().equals("")) {
                    List<KVOListener> listeners = getListenerForId(field.getFieldID());
                    if (listeners != null) {
                        for (KVOListener listener : listeners) {
                            listener.onValueChange(this,param,field.getFieldID());
                        }
                    }
                }
            }

        }
    }
    /**
     * this method get the caller method which is by default the setter method
     * get the name of method so we can get the variable name from that
     * and then call {@link #containProperty(String)}
     * @return
     */
    private List<KVOListener> getListenerForId(String id) {
        List<KVOListener> targetList = new ArrayList<>();
        List<WeakReference<KVOListener>> sourceList = KVOManager.getInstance().getIdentifiedObservers().get(id);
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
    private KVOObserverObject initKVOProcess(){
        String fieldName = getFieldName();

        return containProperty(fieldName);
    }

    private String getFieldName() {
        int position = 0;
        for(int x = 0; x < Thread.currentThread().getStackTrace().length ; x++){
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[x];
            if(stackTraceElement.getMethodName().equals("getFieldName")){
                position = x;
                break;
            }
        }
        String methodName = Thread.currentThread().getStackTrace()[position+ 2].getMethodName();
        return methodName.substring(3);
    }
    /**
     * this method iterate through all the observers and if observer is assigned to field it return that observer in case
     * it's not null since we use weak refrence see {@link KVOObserverObject#listener}
     * and if it's not null but it's an activity or fragment we need to check if activity is finished or not
     * the fragment or activity maybe finished but the GC not yet removed it from memory so weak reference would still got value and not null
     * @param propertyName
     * @return
     */
    private KVOObserverObject containProperty(String propertyName){

        for (Iterator<KVOObserverObject> iterator = KVOManager.getInstance().getObservers().iterator(); iterator.hasNext(); ) {
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
