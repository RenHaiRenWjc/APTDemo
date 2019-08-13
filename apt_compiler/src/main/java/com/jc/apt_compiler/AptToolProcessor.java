package com.jc.apt_compiler;

import com.google.auto.service.AutoService;
import com.jc.apt_compiler.mode.FieldViewBinding;
import com.jc.apt_compiler.mode.ProxyClass;
import com.jc.aptannotations.ViewById;
import com.jc.aptannotations.OnClick;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

//@SupportedSourceVersion(SourceVersion.RELEASE_7)
//@SupportedAnnotationTypes({})

/**
 * 注解处理器
 */

//属于auto-service库，可以自动生成META-INF/services/javax.annotation.processing.Processor文件
// （该文件是所有注解处理器都必须定义的），免去了我们手动配置的麻烦。
@AutoService(Processor.class)
public class AptToolProcessor extends AbstractProcessor {
    private Filer mFiler;  //文件相关工具类
    private Elements mElementsUtils; // 元素相关工具类
    private Messager mMessager; //日志相关工具类
    private Map<String, ProxyClass> mProxyClassMap = new HashMap<>();


    /**
     * 处理器的初始化方法，可以获取相关的工具类
     *
     * @param processingEnvironment 拿到一些实用的工具类
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        info("AptToolProcessor","info init");
        super.init(processingEnvironment);
        String d = null;
        d.toString();
        mFiler = processingEnvironment.getFiler();
        mElementsUtils = processingEnvironment.getElementUtils();
        mMessager = processingEnvironment.getMessager();
    }

    /**
     * 处理器的主方法，用于扫描处理注解，生成 java 文件
     *
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(ViewById.class)) {
            if (!isValidPass(ViewById.class, "fields", element)) {
                return true;
            }
            parseViewById(element);
            error(element, "@%s %s AptToolProcessor may only be contained in classes. (%s.%s)",
                    "ViewById.class", "fields", "test",
                    element.getSimpleName());
        }
       info("AptToolProcessor","info process");
        for (ProxyClass proxyClass : mProxyClassMap.values()) {
            try {
                proxyClass.generateProxy().writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mProxyClassMap.clear();// 防止生成重复的代理类

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(ViewById.class.getName());
        types.add(OnClick.class.getName());
        return types;
    }

//    /**
//     * 指定哪些注解应用被注解处理器注册
//     *
//     * @return 返回一个 String 集合，包含了你的注解处理器想要处理的注解类型的全限定名。
//     */
//    @Override
//    public Set<String> getSupportedOptions() {
//        Set<String> types = new LinkedHashSet<>();
//        types.add(ViewById.class.getName());
//        types.add(OnClick.class.getName());
//        return types;
//    }

    /**
     * 用来指定使用 java 版本
     *
     * @return 通常我们返回SourceVersion.latestSupported()即可
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 匹配验证
     *
     * @param annotationClass 自定义注解
     * @param targetThing     应用注解的地方
     * @param element         Element元素
     * @return
     */
    private boolean isValidPass(Class<? extends Annotation> annotationClass,
                                String targetThing, Element element) {
        boolean isValid = true;
        // 获取变量所在的父元素，肯定是类、接口、枚举
        TypeElement typeElement = (TypeElement) element.getEnclosedElements();

        // 父元素的全限定名, com.jc.aptannotations.OnClick
        String qualiFiedName = typeElement.getQualifiedName().toString();

        //所在的类不能是 private 或 static
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.STATIC)) {
            error(typeElement, "@%s %s AptToolProcessor must not be private or static. (%s.%s)"
                    , annotationClass.getSimpleName(), targetThing, typeElement.getQualifiedName()
                    , element.getSimpleName());
            isValid = false;
        }

        // 父元素必须是类，而不能是接口或枚举
        if (typeElement.getKind() != ElementKind.CLASS) {
            error(typeElement, "@%s %s AptToolProcessor may only be contained in classes. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, typeElement.getQualifiedName(),
                    element.getSimpleName());
            isValid = false;

        }

        //不能在Android框架层注解
        if (qualiFiedName.startsWith("android.")) {
            error(element, "@%s-annotated AptToolProcessor class incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualiFiedName);
            return false;
        }
        //不能在java框架层注解
        if (qualiFiedName.startsWith("java.")) {
            error(element, "@%s-annotated AptToolProcessor class incorrectly in Java framework package. (%s)",
                    annotationClass.getSimpleName(), qualiFiedName);
            return false;
        }
        return isValid;
    }

    private void error(Element e, String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private void info(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }

    private void parseViewById(Element element) {
        ProxyClass proxyClass = getProxyClass(element);
        FieldViewBinding binding = new FieldViewBinding(element);
        proxyClass.add(binding);
    }

    /**
     * 生成或获取注解对于的 ProxyClass
     *
     * @param element 元素
     * @return ProxyClass
     */
    private ProxyClass getProxyClass(Element element) {
        // 被注解所在类
        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        String name = classElement.getQualifiedName().toString();
        ProxyClass proxyClass = mProxyClassMap.get(name);
        if (proxyClass == null) {
            proxyClass = new ProxyClass(classElement, mElementsUtils);
            mProxyClassMap.put(name, proxyClass);
        }
        return proxyClass;
    }
}





























