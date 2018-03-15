package com.lkn.lock;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author LiKangning
 * @since 2018/3/13 上午11:27
 */
public class Tools {


    public static String currentTime() {
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(day) + " ";
    }

    public static void randomSleep() {
        long time = (long)(Math.random() * 1000L);
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
