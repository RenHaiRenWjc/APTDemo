package com.jc.apt_compiler.mode;

import com.jc.aptannotations.ViewById;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * ClassName:com.jc.apt_compiler.mode
 * Description:
 * JcChen on 2019/8/11 17:05
 */
public class FieldViewBinding {
    private VariableElement mVariableElement; //注解元素
    private int mResId; // id
    private String fieldName; //变量名
    private TypeMirror fieldType; //变量类型

    public FieldViewBinding(Element element) {
//        if (element.getKind() != ElementKind.FIELD) {
//            throw new Exception("error not field");
//        }
        this.mVariableElement = (VariableElement) element;
        ViewById getViewById = element.getAnnotation(ViewById.class);
        mResId = getViewById.value();
        fieldName = element.getSimpleName().toString();
        fieldType = element.asType();
    }

    public VariableElement getVariableElement() {
        return mVariableElement;
    }

    public int getResId() {
        return mResId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public TypeMirror getFieldType() {
        return fieldType;
    }
}
