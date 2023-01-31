package com.lkn.dag.junit_test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xijiu
 * @since 2022/4/11 下午2:29
 */
@Getter
@Setter
@AllArgsConstructor
public class Record {
    private String name;
    private long nodeId;
    private long time;

    public static Record of(String name, long nodeId, long time) {
        return new Record(name, nodeId, time);
    }

    public static Record of(String content) {
        String[] split = content.split("-");
        if (split.length == 2) {
            return of(split[0], Long.parseLong(split[1]), -1);
        } else if (split.length == 3) {
            return of(split[0], Long.parseLong(split[1]), Long.parseLong(split[2]));
        }
        throw new RuntimeException();
    }
}
