package com.jc.apt_compiler.mode;

import com.jc.apt_compiler.exception.ProcessingException;
import com.jc.aptannotations.ViewById;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
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

    public FieldViewBinding(Element element) throws ProcessingException {
        if (element.getKind() != ElementKind.FIELD) {
            throw new ProcessingException("Only field can be annotated with @%s", ViewById.class.getSimpleName());
        }
        this.mVariableElement = (VariableElement) element;
        ViewById getViewById = element.getAnnotation(ViewById.class);
        mResId = getViewById.value();
        if (mResId < 0) {
            throw new ProcessingException("value() in %s for field % is not valid",
                    ViewById.class.getSimpleName(), mVariableElement.getSimpleName());
        }
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
