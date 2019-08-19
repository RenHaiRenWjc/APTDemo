package com.jc.apt_compiler.mode;

import com.jc.apt_compiler.exception.ProcessingException;
import com.jc.aptannotations.OnClick;

import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;

/**
 * ClassName:com.jc.apt_compiler.mode
 * Description:
 *
 * @OnClick({R.id.bt_test02}) public void onViewClicked(View view) {
 * }
 * JcChen on 2019/8/19 23:35
 */
public class OnClickMethod {
    private String mMethodName;
    //    private Messager messager;
    private int[] ids;
    private boolean hasParameter;
    private String parameterName;

    public OnClickMethod(Element element) throws ProcessingException {
//        this.messager = messager;
        if (element.getKind() != ElementKind.METHOD) {
            throw new ProcessingException("Only method can be annotation width @%s", OnClick.class.getSimpleName());
        }
        ExecutableElement methodElement = (ExecutableElement) element;
        mMethodName = methodElement.getSimpleName().toString(); //onViewClicked
        OnClick onClick = methodElement.getAnnotation(OnClick.class);
        ids = onClick.value(); //({R.id.bt_test02})
        if (ids.length <= 0) {
            throw new IllegalArgumentException(String.format("Must set valid ids for @%s",
                    OnClick.class.getSimpleName()));
        } else {
            for (int id : ids) {
                if (id < 0) {
                    throw new IllegalArgumentException(String.format("Must set valid ids for @%s",
                            OnClick.class.getSimpleName()));
                }
            }
        }
        List<? extends VariableElement> parameters = methodElement.getParameters();  //(View view)
        if (parameters.size() == 1) {
            VariableElement variableElement = parameters.get(0);
            if (!variableElement.asType().toString().equals(ProxyClass.VIEW.toString())) {
                throw new IllegalArgumentException(
                        String.format("The method parameter must be %s type", ProxyClass.VIEW.toString()));
            }
            hasParameter = true;
            parameterName = variableElement.getSimpleName().toString();
        }
    }

    public String getMethodName() {
        return mMethodName;
    }

    public int[] getIds() {
        return ids;
    }

    public boolean hasParameter() {
        return hasParameter;
    }

    public String getParameterName() {
        return parameterName;
    }
}
