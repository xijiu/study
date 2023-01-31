package com.lkn.dag.tools;


import com.lkn.dag.junit_test.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author xijiu
 * @since 2022/4/11 上午9:59
 */
public class Global {
    public static List<Record> records = Collections.synchronizedList(new ArrayList<>());

    public static void reset() {
        records.clear();
    }
}
