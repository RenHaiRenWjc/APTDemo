package com.jc.aptapi;

import android.view.View;

/**
 * ClassName:com.jc.aptapi
 * Description:
 * JcChen on 2019/8/12 0:10
 */
public interface IProxy<T> {
    /**
     * @param target 所在类
     * @param root   查 View 的地方
     */
    public void inject(final T target, View root);
}
