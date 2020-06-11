package com.lkn.cpu;

import com.google.common.base.Stopwatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ArticleApplication {

    private long beginTime;

    @Before
    public void before() {
        beginTime = System.currentTimeMillis();
    }

    @After
    public void after() {
        System.out.println("耗时： " + (System.currentTimeMillis() - beginTime));
    }

    private int total = 5000000;

    @Test
    public void main1() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            list.add(String.valueOf(i));
        }
    }

    @Test
    public void main2() {
        List<String> list = new LinkedList<>();
        for (int i = 0; i < total; i++) {
            list.add(String.valueOf(i));
        }
    }
}