package com.lkn.lock;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiKangning
 * @since 2018/3/6 下午4:07
 */
public class VolatileTest {

    private static List<Integer> list = new ArrayList<>();
//    private static List<Integer> list = new CopyOnWriteArrayList<>();


    public static void main(String[] args) throws InterruptedException {

        int size = 100;
        for (int i = 0; i < size; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    list.add(finalI);
                }
            }).start();

        }
//        Thread.sleep(1000);
        for (int i = 0; i < size; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(list.get(finalI));
                }
            }).start();
        }


    }
}
