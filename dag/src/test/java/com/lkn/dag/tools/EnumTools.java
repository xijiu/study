package com.lkn.dag.tools;


import com.lkn.dag.handlers.Handler;

import java.util.Objects;

/**
 * 枚举动态添加的工具类
 *
 * @author xijiu
 * @since 2022/4/12 上午8:30
 */
public class EnumTools {

    private static final EnumBuster<Handler> buster = new EnumBuster<>(Handler.class);


    /**
     * 根据字符串内容添加枚举值，如果存在，则直接返回结果
     *
     * @param str   字符串形似
     */
    public static Handler add(String str) {
        Handler[] values = Handler.values();
        for (Handler value : values) {
            if (Objects.equals(value.toString(), str)) {
                return value;
            }
        }
        Handler newHandler = buster.make(str);
        buster.addByValue(newHandler);
        return newHandler;
    }

    public static Handler add(String str, Class clazz, Object obj) {
        Handler[] values = Handler.values();
        for (Handler value : values) {
            if (Objects.equals(value.toString(), str)) {
                return value;
            }
        }
        Handler newHandler = buster.make(str, 0, new Class[]{clazz}, new Object[]{obj});
        buster.addByValue(newHandler);
        return newHandler;
    }

    public static Handler get(String str) {
        Handler[] values = Handler.values();
        for (Handler value : values) {
            if (Objects.equals(value.toString(), str)) {
                return value;
            }
        }
        throw new RuntimeException();
    }

}
