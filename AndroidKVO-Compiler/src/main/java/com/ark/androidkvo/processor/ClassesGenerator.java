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

package com.ark.androidkvo.processor;

import com.ark.androidkvo.annotations.KVOField;
import com.ark.androidkvo.models.AnnotatedClass;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

/**
 * Here is where all the magic happen we pass the class here and take care of all the imports
 * extending the original class make an enum with all fields that we need and overwriting the setter
 * to make an interception of changing the value and notify the listener with the new value
 *
 * String string everywhere :D
 *
 * Created by ahmed-basyouni on 12/31/16.
 */

public class ClassesGenerator {

    static List<String> fieldsName = new ArrayList<>();

    static StringBuilder generateClass(AnnotatedClass annotatedClass) {

        StringBuilder builder = new StringBuilder()
                .append("/**\n" +
                        " * The MIT License (MIT)\n" +
                        " * Copyright (c) 2016 Ahmed basyouni\n" +
                        " * <p>\n" +
                        " * Permission is hereby granted, free of charge, to any person obtaining a copy of this software\n" +
                        " * and associated documentation files (the \"Software\"), to deal in the Software without restriction,\n" +
                        " * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,\n" +
                        " * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,\n" +
                        " * subject to the following conditions:\n" +
                        " * The above copyright notice and this permission notice shall be included in all copies or substantial portions\n" +
                        " * of the Software.\n" +
                        " * THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,\n" +
                        " * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND\n" +
                        " * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,\n" +
                        " * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
                        " * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.\n" +
                        " */\n").append("package ").append(annotatedClass.packageName).append(".kvo;\n\n");
        importRef(builder,annotatedClass);
        // class start
        builder.append("public final class ").append(annotatedClass.annotatedClass.getSimpleName()).append("KVO extends ")
                .append(annotatedClass.annotatedClass.getSimpleName())
                .append(" implements Serializable,IKVO")
//                .append("<").append(annotatedClass.annotatedClass.getSimpleName()).append("KVO>")
                .append("{\n\n");// open class
        fieldClassAndEnum(builder,annotatedClass);
        builder.append("    private KVOManager mKVOManager = new KVOManager();\n");

//        getObserverObject(builder);
//        getSelfFieldAndSetParent(builder);
        for (ExecutableElement cons :
                ElementFilter.constructorsIn(annotatedClass.annotatedClass.getEnclosedElements())) {

            TypeElement declaringClass =
                    (TypeElement) cons.getEnclosingElement();
            generateConstruct(builder,declaringClass,cons);
//            String className = declaringClass.getSimpleName().toString() + "KVO";
//            String variable = "m"+ capitalize(className);
//            cloneSelf(builder,className,annotatedClass);
//            same(builder,className,variable,annotatedClass);
//            updateSelfValue(builder,variable,className,annotatedClass);
        }
        setListenerWithField(builder);
        setListener(builder);
        removeListener(builder);
        setListenerForId(builder);
        set(builder,annotatedClass);
//        notifyParent(builder);
        checkIdInManager(builder);
        initKVOProcess(builder)
        .append("}\n"); // end close class

        return builder;
    }

    static StringBuilder fieldClassAndEnum(StringBuilder builder,AnnotatedClass annotatedClass){
        builder .append("   ArrayList<FieldObject> allKVOFields = new ArrayList<FieldObject>() {{\n");
        for (VariableElement field : annotatedClass.annotatedFields) {
            fieldsName.add(field.getSimpleName().toString());
            builder.append("        add(new FieldObject(\"").append(field.getSimpleName()).append("\"").append(",").append("\"").append(field.getAnnotation(KVOField.class).id()).append("\"").append("));\n");
        }
        builder.append("   }};\n")
                .append("   public enum FieldName{\n")
                .append("       ");
        for(int x = 0 ; x < annotatedClass.annotatedFields.size() ; x++){
            VariableElement field = annotatedClass.annotatedFields.get(x);
            builder.append(field.getSimpleName());
            if(x != annotatedClass.annotatedFields.size() -1){
                builder.append(",");
            }else {
                builder.append("\n   }\n\n");
            }
        }
        return builder;
    }

    static StringBuilder importRef(StringBuilder builder,AnnotatedClass annotatedClass){
        // TODO: 2017/9/14 待修改删除无用的引用导入
        return builder.append("import com.ark.androidkvo.manager.Utils;")
                .append("import com.ark.androidkvo.models.IKVO;\n")
//                .append("import com.ark.androidkvo.models.KVORef;\n")
                .append("import com.ark.androidkvo.models.KVOListener;\n")
                .append("import com.ark.androidkvo.manager.KVOManager;\n")
                .append("import java.io.Serializable;\n")
                .append("import android.app.Activity;\n")
                .append("import android.support.v4.app.Fragment;\n")
                .append("import com.ark.androidkvo.annotations.KVOField;\n")
                .append("import java.lang.ref.WeakReference;\n")
                .append("import java.util.List;")
                .append("import java.util.Iterator;\n")
                .append("import java.util.ArrayList;\n")
                .append("import com.ark.androidkvo.models.FieldObject;\n")
                .append("import com.ark.androidkvo.models.KVOObserverObject;\n").append("import ").append(annotatedClass.packageName).append(".").append(annotatedClass.annotatedClass.getSimpleName()).append(";\n");

    }

    static StringBuilder setListenerWithField(StringBuilder builder){
        builder.append("    /**\n" +
                "     * you can use this method to set a callback for a certain field \n" +
                "     * All You have to do is pass object that implement {@link KVOListener} and pass the field name using {@link FieldName}\n" +
                "     * Which you can find inside the generated class you can access it like this generatedClass.FieldName.Field where generated class is your className+KVO\n" +
                "     * and Field is your field name the purpose of that in case you change the field name you got a compilation error instead of searching for fields name\n" +
                "     * as strings\n" +
                "     * \n" +
                "     * @param listener\n" +
                "     * @param property\n" +
                "     */\n");
        return builder.append("   public void setListener(KVOListener listener , FieldName property){\n")
                .append("       boolean fieldExist = false;\n" +
                        "        String fieldId = \"\";\n" +
                        "        for (FieldObject fieldObj : allKVOFields) {\n" +
                        "            if(property.toString().equals(fieldObj.getFieldName())){\n" +
                        "                fieldExist = true;\n" +
                        "                fieldId = fieldObj.getFieldID();\n" +
                        "                break;\n" +
                        "            }\n" +
                        "        }\n")
                .append("     if(!fieldExist)\n")
                .append("         throw new RuntimeException(\"Field with name \" + property.name() + \" does not exist or it maybe private\");\n")
                .append("     KVOObserverObject observerObject = new KVOObserverObject();\n")
                .append("     observerObject.setListener(listener);\n")
                .append("      observerObject.setFieldId(fieldId);\n")
                .append("     observerObject.setPropertyName(property.name());\n")
                .append("           if(!fieldId.equals(\"\")){\n")
                .append("               mKVOManager.addIdentifiedObserver(fieldId, listener);\n")
                .append("           }else {\n")
                .append("               if (!mKVOManager.getObservers().contains(observerObject)) {\n")
                .append("                   mKVOManager.addObserver(observerObject);\n")
                .append("               }\n")
                .append("           }\n")
                .append("   }\n");
    }

    static StringBuilder setListener(StringBuilder builder){
        builder.append("    /**\n" +
                "     * you can use this method to listen for all the changes that happen on all fields that is annotated by {@link KVOField}\n" +
                "     * if you need a certain field instead use {@link #setKvo(KVOListener, FieldName)}\n" +
                "     * @param listener\n" +
                "     */\n");
        return builder.append("    public void setListener(KVOListener listener){\n" +
                "       for(FieldObject field : allKVOFields){\n" +
                "           KVOObserverObject observerObject = new KVOObserverObject();\n" +
                "           observerObject.setListener(listener);\n" +
                "           observerObject.setPropertyName(field.getFieldName());\n" +
                "           observerObject.setFieldId(field.getFieldID());\n"+
                "           if(!field.getFieldID().equals(\"\")){\n" +
                "               mKVOManager.addIdentifiedObserver(field.getFieldID(), listener);\n" +
                "           }else {\n" +
                "               if (!mKVOManager.getObservers().contains(observerObject)) {\n" +
                "                   mKVOManager.addObserver(observerObject);\n" +
                "               }\n" +
                "           }\n"+
                "        }\n" +
                "   }\n\n");
    }

    static StringBuilder removeListener(StringBuilder builder){
        builder.append("    /**\n" +
                "     * use this method to remove the callback listener \n" +
                "     * @param kvoListener\n" +
                "     */\n");
        return builder.append("    public void removeListener(KVOListener kvoListener){\n" +
                "\n" +
                "        for (Iterator<KVOObserverObject> iterator = mKVOManager.getObservers().iterator(); iterator.hasNext();) {\n" +
                "            KVOObserverObject observerObject = iterator.next();\n" +
                "            if (observerObject.getListener().equals(kvoListener)) {\n" +
                "                // Remove the current element from the iterator and the list.\n" +
                "                iterator.remove();\n" +
                "            }\n" +
                "        }\n" +
                "           mKVOManager.removeIdentifiedObserver(kvoListener);\n"+
                "    }\n\n");
    }

    static StringBuilder setListenerForId(StringBuilder builder){
        builder.append("    /**\n" +
                "     * you can use this method to listen for all the changes that happen to a certain field annotated with certain ID\n" +
                "     * {@link KVOField#id()}\n" +
                "     * please note that all the fields annoteted with same ID will trigger the listener so make sure that the id is unique\n" +
                "     * @param listener\n" +
                "     * @param id\n" +
                "     */\n");

        return builder.append("    public void setListenerForId(KVOListener listener, String id) {\n")
                .append("           boolean fieldExist = false;\n" +
                        "        String fieldName = \"\";\n" +
                        "        for (FieldObject fieldObj : allKVOFields) {\n" +
                        "            if(id.equals(fieldObj.getFieldID())){\n" +
                        "                fieldExist = true;\n" +
                        "                break;\n" +
                        "            }\n" +
                        "        }\n")
                .append("        if (!fieldExist)\n")
                .append("            throw new RuntimeException(\"Field with id \" + id + \" does not exist or it maybe private\");\n")
                .append("        mKVOManager.addIdentifiedObserver(id , listener);\n")
                .append("    }\n");
    }

    static StringBuilder set(StringBuilder builder, AnnotatedClass annotatedClass){
        for (VariableElement field : annotatedClass.annotatedFields) {

            builder.append("    public void set").append(capitalize(field.getSimpleName().toString())).append("(").append(field.asType().toString()).append(" param)").append(" {\n")
                    .append("        this.").append(field.getSimpleName()).append(" = param;\n")
                    .append("        KVOObserverObject observerObject = initKVOProcess();\n")
                    .append("        if (observerObject != null && observerObject.getListener() != null) {\n" +
                            "            observerObject.getListener().onValueChange(this, param, observerObject.getPropertyName());\n" +
                            "        } else if (observerObject != null && observerObject.getListener() == null){\n")
                    .append("            mKVOManager.removeObserver(observerObject);\n")
                    .append("        } else {\n" +
                            "            checkIdInManager(param);\n" +
                            "        }\n")
//                    .append("        if (this.").append(field.getSimpleName()).append(" != null) {\n" )
//                    .append("           this.").append(field.getSimpleName()).append(".setParent(this);\n" )
//                    .append("           this.").append(field.getSimpleName()).append(".setSelfField(Utils.getFieldName(1));\n")
//                    .append("        }\n")
                    .append("    }\n\n");
        }
        return builder;
    }

    static StringBuilder notifyParent(StringBuilder builder){
        return builder
                .append("     public void notifyParent(){\n")
                .append("        if(parent == null) return;\n")
                .append("        KVOObserverObject observerObject = this.parent.getObserverObject(mSelfField);\n")
                .append("        if (observerObject != null && observerObject.getListener() != null) {\n")
                .append("            observerObject.getListener().onValueChange(this.parent, this, mSelfField);\n")
                .append("        }\n")
                .append("        parent.notifyParent();\n")
                .append( "    }");
    }

    static StringBuilder checkIdInManager(StringBuilder builder){
        builder.append("    /**\n" +
                "     * class use this method to try to find an observer which is registered to this param\n" +
                "     * if it found one it will notify it that the value has changed\n" +
                "     * @param param\n" +
                "     */\n");
        return builder.append("    private void checkIdInManager(Object param){\n" +
                "               for (FieldObject field : allKVOFields) {\n" +
                "            if (field.getFieldName().equalsIgnoreCase(Utils.getFieldName(2))) {\n" +
                "                if (!field.getFieldID().equals(\"\")) {\n" +
                "                    List<KVOListener> listeners = Utils.getListenerForId(mKVOManager,field.getFieldID());\n" +
                "                    if (listeners != null) {\n" +
                "                        for (KVOListener listener : listeners) {\n" +
                "                            listener.onValueChange(this,param,field.getFieldID());\n" +
                "                        }\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "\n" +
                "        }\n"+
                "    }\n");
    }

    static StringBuilder initKVOProcess(StringBuilder builder){
        return builder.append("    protected KVOObserverObject initKVOProcess(){\n" +
                "       String fieldName = Utils.getFieldName(2);\n" +
                "\n" +
                "       return Utils.containProperty(mKVOManager,fieldName);\n" +
                "    }\n" +
                "\n");
    }

    static StringBuilder getObserverObject(StringBuilder stringBuilder){
        return stringBuilder
                .append("   @Override\n")
                .append("   public KVOObserverObject getObserverObject(String fieldName) {\n")
                .append("       return Utils.containProperty(mKVOManager,fieldName);\n")
                .append("   }\n");
    }
    
    static StringBuilder getSelfFieldAndSetParent(StringBuilder stringBuilder){
        return stringBuilder
                .append("        private KVORef parent;\n")
                .append("        private String mSelfField;\n")
                .append("\n" )
                .append("        public KVORef getParent() {\n" )
                .append("            return parent;\n" )
                .append("        }\n" )
                .append("\n" )
                .append("        public void setParent(KVORef parent) {\n" )
                .append("            this.parent = parent;\n" )
                .append("        }\n" )
                .append("\n" )
                .append("        public void setSelfField(String selfField){\n" )
                .append("            this.mSelfField = selfField;\n" )
                .append("        }\n" )
                .append("\n" )
                .append("        public String getSelfField() {\n" )
                .append("            return mSelfField;\n" )
                .append("        }\n");
    }
    
    static StringBuilder updateSelfValue(StringBuilder builder, String variable, String className, AnnotatedClass annotatedClass){
        builder.append("    public boolean updateSelfValue(" ).append(className).append(" ").append(variable).append(",String fieldName){\n")
                .append("       if (").append(variable).append(" == null) return false;\n");

        for(int x = 0 ; x < annotatedClass.annotatedFields.size() ; x++){
            VariableElement field = annotatedClass.annotatedFields.get(x);
            String fieldName = field.getSimpleName().toString();
            builder.append("        ").append(field.asType().toString()).append(" ").append(fieldName).append(" = ").append(" ").append(variable).append(".get").append(capitalize(fieldName)).append("();\n")
                    .append("       if(").append(fieldName).append(" == null){\n")
                    .append("           this.").append(fieldName).append( "= null;\n")
                    .append("       } else{\n")
                    .append("           if(!").append(fieldName).append(".same(this.").append(fieldName).append(")){\n")
                    .append("               this.").append(fieldName).append(".updateSelfValue(").append(fieldName).append(",\"").append(field).append("\");\n")
                    .append("           }\n")
                    .append("       }\n");

        }
        builder.append("        return true;\n")
                .append("   }\n");
        return builder;
    }

    static StringBuilder generateConstruct(StringBuilder builder, TypeElement declaringClass, ExecutableElement cons){
        builder.append("    public ").append(declaringClass.getSimpleName().toString()).append("KVO(");
        for(int x = 0 ; x < cons.getParameters().size() ; x++){
            if(x > 0)
                builder.append(",");
            VariableElement variableElement = cons.getParameters().get(x);
            builder.append(variableElement.asType().toString()).append(" ").append(variableElement.getSimpleName().toString());
        }
        builder.append("){\n");
        builder.append("        super(");
        for(int x = 0 ; x < cons.getParameters().size() ; x++){
            if(x > 0)
                builder.append(",");
            VariableElement variableElement = cons.getParameters().get(x);
            builder.append(variableElement.getSimpleName().toString());
        }

        builder.append(");\n");
        for(int x = 0 ; x < cons.getParameters().size() ; x++){
            VariableElement variableElement = cons.getParameters().get(x);
            if(fieldsName.contains(variableElement.getSimpleName().toString()))
                builder.append("        set").append(capitalize(variableElement.getSimpleName().toString())).append("(").append(variableElement.getSimpleName().toString()).append(");\n");
            else
                builder.append("        this.").append(variableElement.getSimpleName().toString()).append(" = ").append(variableElement.getSimpleName().toString()).append(";\n");
        }

        builder.append("    }\n");
        return builder;
    }

    static StringBuilder cloneSelf(StringBuilder builder, String className, AnnotatedClass annotatedClass){
        builder.append("   @Override\n")
                .append("   public ").append(className).append(" cloneSelf() {\n")
                .append("       ").append(className).append(" m").append(className).append(" = new ").append(className).append("();\n");

        for(int x = 0 ; x < annotatedClass.annotatedFields.size() ; x++){
            VariableElement field = annotatedClass.annotatedFields.get(x);
            builder.append("        if(this.").append(field.getSimpleName()).append(" == null){\n")
                    .append("           m").append(className).append(".set").append(capitalize(field.getSimpleName().toString())).append("(null);\n")
                    .append("       } else{\n")
                    .append("           m").append(className).append(".set").append(capitalize(field.getSimpleName().toString())).append("(this.").append(field.getSimpleName()).append(".cloneSelf());\n")
                    .append("       }\n");
        }
        builder.append("       return m").append(className).append(";\n")
                .append("   }\n");
        return builder;
    }

    static StringBuilder same(StringBuilder builder, String className, String variable, AnnotatedClass annotatedClass){
        builder.append("    @Override\n")
                .append("   public boolean same(").append(className).append(" ").append(variable).append("){\n")
                .append("       if(").append(variable).append(" == null) return false;\n")
                .append("       boolean same = false;\n");
        for(int x = 0 ; x < annotatedClass.annotatedFields.size() ; x++){
            VariableElement field = annotatedClass.annotatedFields.get(x);
            String fieldName = field.getSimpleName().toString();
            builder .append("       if(this.").append(fieldName).append(" == null").append("){\n")
                    .append("            if(").append(variable).append(".get").append(capitalize(fieldName)).append("() == null){\n")
                    .append("               same = true;\n")
                    .append("            }\n")
                    .append("       } else {\n")
                    .append("           same = this.").append(fieldName).append(".same(").append(variable).append(".get").append(capitalize(fieldName)).append("());\n")
                    .append("       }\n");
        }
        builder.append("       return same;\n")
                .append("   }\n\n");
        return builder;
    }

    private static String capitalize(String s) {
        if (s == null)
            return null;
        if (s.length() == 1) {
            return s.toUpperCase();
        }
        if (s.length() > 1) {
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }
        return "";
    }
}
