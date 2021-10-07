package com.lkn;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MyTest2 {

    @Test
    public void test() {
        String str = "12345678";
        byte[] bytes = str.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.nativeOrder());

        long long1 = byteBuffer.getLong();
        long1 = transLong(long1);

        System.out.println(long1);
    }
    
    private long transLong(long half) {
        System.out.println(half);
        System.out.println(format(half));
//        System.out.println(format(48));
//        System.out.println(format(57));
        long upper = (half & 0x000f000f000f000fL) * 10;
        System.out.println(format((half & 0x000f000f000f000fL)));
        System.out.println(format(upper));

        long lower = (half & 0x0f000f000f000f00L) >> 8;
        half = lower + upper;
        System.out.println(format(half));

        upper = (half & 0x000000ff000000ffL) * 100;
        lower = (half & 0x00ff000000ff0000L) >> 16;
        half = lower + upper;

        upper = (half & 0x000000000000ffffL) * 10000;
        lower = (half & 0x0000ffff00000000L) >> 32;

        return lower + upper;
    }

    public static String format(long value) {
        String target = Long.toBinaryString(value);
        StringBuilder sb = new StringBuilder();
        if (target.length() < 64) {
            int len = 64 - target.length();
            for (int i = 0; i < len; i++) {
                sb.append("0");
            }
            sb.append(target);
        } else {
            sb.append(target);
        }
        sb.insert(56, " ");
        sb.insert(48, " ");
        sb.insert(40, " ");
        sb.insert(32, " ");
        sb.insert(24, " ");
        sb.insert(16, " ");
        sb.insert(8, " ");
        return sb.toString();
    }


    @Test
    public void test3() {
        List<User> list = new ArrayList<>();
    }
}
