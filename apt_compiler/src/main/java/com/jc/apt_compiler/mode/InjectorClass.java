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
public class InjectorClass {
    public TypeElement typeElement; // 类元素
    private Elements elementUtils; // 元素相关辅助类
    private Set<FieldViewBinding> mFieldList = new HashSet<>();
    private Set<OnClickMethod> mOnClickMethods = new HashSet<>();

    private static final ClassName Injector = ClassName.get("com.jc.aptapi", "Injector");
    public static final ClassName VIEW = ClassName.get("android.view", "View");
    public static final ClassName VIEW_ON_CLICK_LISTENER = ClassName.get("android.view",
            "View", "OnClickListener");
    private static final String SUFFIX = "Injector";

    public InjectorClass(TypeElement typeElement, Elements elementUtils) {
        this.typeElement = typeElement;
        this.elementUtils = elementUtils;
    }

    public void addField(FieldViewBinding viewBinding) {
        mFieldList.add(viewBinding);
    }

    public void addMethod(OnClickMethod onClickMethod) {
        mOnClickMethods.add(onClickMethod);
    }

    //生成代理类
    public JavaFile generateProxy() {
        //生成public void inject(final T target, View root)方法
        MethodSpec.Builder mInjectBuilder = MethodSpec
                .methodBuilder("inject")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(typeElement.asType()), "target", Modifier.FINAL)
                .addParameter(VIEW, "root");
        // 在 inject 方法中，添加 findViewById 逻辑
        for (FieldViewBinding field : mFieldList) {
            // target.textView=(TextView)(root.findViewById(2131165326));
            mInjectBuilder.addStatement("target.$N=($T)(root.findViewById($L))", field.getFieldName()
                    , ClassName.get(field.getFieldType()), field.getResId());
        }

//        listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                target.onViewClicked(onViewClicked);
//            }
//        };
        for (OnClickMethod method : mOnClickMethods) {
            mInjectBuilder.addStatement("$T listener", VIEW_ON_CLICK_LISTENER);
            TypeSpec listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(VIEW_ON_CLICK_LISTENER)
                    .addMethod(MethodSpec.methodBuilder("onClick")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.VOID)
                            .addParameter(VIEW, "view")
                            .addStatement("target.$N($L)", method.getMethodName()
                                    , method.hasParameter() ? method.getParameterName() : "")
                            .build()
                    ).build();

            mInjectBuilder.addStatement("listener = $L", listener);
            for (int id : method.getIds()) {
                mInjectBuilder.addStatement("(root.findViewById($L)).setOnClickListener(listener)", id);
            }
        }

        String packageName = getPackageName(typeElement);
        String className = getClassName(typeElement, packageName);
        ClassName bindClassName = ClassName.get(packageName, className);

        // 添加$$Proxy 为后缀的类
        TypeSpec finderClass = TypeSpec.classBuilder(bindClassName.simpleName() + SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                // 添加父接口
                .addSuperinterface(ParameterizedTypeName.get(Injector, TypeName.get(typeElement.asType())))
                .addMethod(mInjectBuilder.build())
                .build();

        // 生成 java 文件
        return JavaFile.builder(packageName, finderClass).build();
    }

    private String getClassName(TypeElement annotatedClassElement, String packageName) {
        int packageLen = packageName.length() + 1;
        return annotatedClassElement.getQualifiedName().toString().substring(packageLen);
    }

    private String getPackageName(TypeElement annotatedClassElement) {
        return elementUtils.getPackageOf(annotatedClassElement).toString();
    }


}
