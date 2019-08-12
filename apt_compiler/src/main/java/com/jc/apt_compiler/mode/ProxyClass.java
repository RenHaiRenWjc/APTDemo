package com.jc.apt_compiler.mode;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * ClassName:com.jc.apt_compiler.mode
 * Description: 代理类
 * JcChen on 2019/8/11 17:00
 */
public class ProxyClass {
    public TypeElement typeElement; // 类元素
    private Elements elementUtils; // 元素相关辅助类
    private Set<FieldViewBinding> bindViews = new HashSet<>();

    private static final ClassName IPROXY = ClassName.get("com.jc.aptapi", "IProxy");
    private static final ClassName VIEW = ClassName.get("android.view", "View");
    private static final String SUFFIX = "$$Proxy";

    public ProxyClass(TypeElement typeElement, Elements elementUtils) {
        this.typeElement = typeElement;
        this.elementUtils = elementUtils;
    }

    public void add(FieldViewBinding viewBinding) {
        bindViews.add(viewBinding);
    }

    //生成代理类
    public JavaFile generateProxy() {
        //生成public void inject(final T target, View root)方法
        MethodSpec.Builder mAddViewBuilder = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(typeElement.asType()), "target", Modifier.FINAL)
                .addParameter(VIEW, "root");
        // 在 inject 方法中，添加 findViewById 逻辑
        for (FieldViewBinding model : bindViews) {
            mAddViewBuilder.addStatement("target.$N=($T)(root.findViewById($L))", model.getVariableElement()
                    , ClassName.get(model.getTypeMirror()), model.getResId());
        }

        // 添加$$Proxy 为后缀的类
        TypeSpec finderClass = TypeSpec.classBuilder(typeElement.getSimpleName() + SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                // 添加父接口
                .addSuperinterface(ParameterizedTypeName.get(IPROXY, TypeName.get(typeElement.asType())))
                .addMethod(mAddViewBuilder.build())
                .build();

        // 添加包名
        String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
        // 生成 java 文件
        return JavaFile.builder(packageName, finderClass).build();
    }


}
