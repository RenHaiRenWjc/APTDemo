package com.jc.apt_compiler.exception;

/**
 * ClassName:com.jc.apt_compiler.exception
 * Description:
 * JcChen on 2019/8/19 23:39
 */
public class ProcessingException extends Exception {
    public ProcessingException(String msg, Object... args) {
        super(String.format(msg, args));
    }
}
