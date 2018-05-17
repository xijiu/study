package com.lkn;


import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void test() {
        for (int i = 0; i < 10; i++) {
            long time = (long)(Math.random() * 10L);
            System.out.println(time * 10);
        }
    }
}
