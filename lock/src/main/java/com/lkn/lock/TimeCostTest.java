package com.lkn.lock;

import org.junit.After;
import org.junit.Before;

/**
 * @author LiKangning
 * @since 2018/3/7 上午7:35
 */
public class TimeCostTest {
    private Long begin = 0L;

    @Before
    public void before() {
        begin = System.currentTimeMillis();
    }

    @After
    public void after() {
        Long cost = System.currentTimeMillis() - begin;
        System.out.println("当前测试耗时： " + cost);
    }
}
