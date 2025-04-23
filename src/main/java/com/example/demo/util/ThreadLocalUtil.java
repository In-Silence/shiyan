package com.example.demo.util;

import org.springframework.stereotype.Component;

/**
 * ThreadLocal工具类
 */
@Component
public class ThreadLocalUtil {
//创建一个ThreadLocal对象
    private static final ThreadLocal<Object> THREAD_LOCAL = new ThreadLocal<>();
//根据键获取值
    public static <T> T get() {
        return (T) THREAD_LOCAL.get();
    }
//存储键值对
    public static void set(Object value) {
        THREAD_LOCAL.set(value);
    }
//清除ThreadLocal，防止内存泄漏
    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
