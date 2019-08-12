package com.jc.aptapi;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.View;

/**
 * ClassName:com.jc.aptapi
 * Description: 架构 api，供使用者调用，Android Library类型模块
 * JcChen on 2019/8/12 0:12
 */
public class ProxyTool {
    private static final String SUFFIX = "$$Proxy";

    /**
     * activity
     *
     * @param target Target activity for view binding.
     */
    @UiThread
    public static void bind(@NonNull Activity target) {
        View sourceView = target.getWindow().getDecorView();
        createBinding(target, sourceView);
    }

    /**
     * view
     *
     * @param target Target view for view binding.
     */
    @UiThread
    public static void bind(@NonNull View target) {
        createBinding(target, target);
    }

    /**
     * fragment
     *
     * @param target Target class for view binding.
     * @param source Activity on which IDs will be looked up.
     */
    @UiThread
    public static void bind(@NonNull Object target, @NonNull View source) {
        createBinding(target, source);
    }

    private static void createBinding(@NonNull Object target, @NonNull View root) {
        Class<?> targetClass = target.getClass();
        try {
            Class<?> proxyClass = Class.forName(targetClass.getName() + SUFFIX);
            IProxy proxy = (IProxy) proxyClass.newInstance();
            proxy.inject(target, root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
