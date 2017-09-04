/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Ahmed basyouni
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ark.androidkvo.models;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * object that hold all the information about listener (the property name or id that this
 * listener is registered with)
 * Created by ahmed-basyouni on 12/31/16.
 */

public class KVOObserverObject {

    private WeakReference<KVOListener> listener;
    private WeakReference<List<KVOListener>> listeners;

    private String propertyName;
    private String fieldId;

    public KVOListener getListener() {
        return listener.get();
    }

    public void setListener(KVOListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    public void addListener(KVOListener listener){
        if (this.listeners == null) listeners = new WeakReference<List<KVOListener>>(new ArrayList<KVOListener>());
        List<KVOListener> listeners = this.listeners.get();
        if (!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    public WeakReference<List<KVOListener>> getListeners() {
        if (this.listeners == null) listeners = new WeakReference<List<KVOListener>>(new ArrayList<KVOListener>());
        return listeners;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KVOObserverObject that = (KVOObserverObject) o;

        return listener.equals(that.listener) && propertyName.equals(that.propertyName);

    }

    @Override
    public int hashCode() {
        int result = listener.hashCode();
        result = 31 * result + propertyName.hashCode();
        return result;
    }
}
