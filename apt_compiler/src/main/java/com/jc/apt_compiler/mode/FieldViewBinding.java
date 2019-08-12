package com.jc.apt_compiler.mode;

import com.jc.aptannotations.GetViewById;

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
    private String name; //变量名
    private TypeMirror typeMirror; //变量类型

    public FieldViewBinding(Element element) {
        this.mVariableElement = (VariableElement) element;
        GetViewById getViewById = element.getAnnotation(GetViewById.class);
        mResId = getViewById.value();
        name = element.getSimpleName().toString();
        typeMirror = element.asType();
    }

    public VariableElement getVariableElement() {
        return mVariableElement;
    }

    public int getResId() {
        return mResId;
    }

    public String getName() {
        return name;
    }

    public TypeMirror getTypeMirror() {
        return typeMirror;
    }
}
