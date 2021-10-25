package com.lkn;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
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
        String str = "8876543210123456789";
        byte[] bytes = str.getBytes();

        long begin = System.currentTimeMillis();
//        common(bytes);
//        common2(bytes);
        common3(bytes);

        long cost = System.currentTimeMillis() - begin;
        System.out.println("time cost " + cost);
    }

    private void common3(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.nativeOrder());
        long long1 = byteBuffer.getLong();
        long long2 = byteBuffer.getLong();
        long byte1 = byteBuffer.get();
        long byte2 = byteBuffer.get();
        long byte3 = byteBuffer.get();
        for (long i = 0; i < 300000000L; i++) {
            long1 = transLong2(long1);
            long2 = transLong2(long2);
            byte1 &= 0x0f;
            byte2 &= 0x0f;
            byte3 &= 0x0f;
            long tail = byte1 * 100 + byte2 * 10 + byte3;
            long res_0 = long1 * 100000000000L + long2 * 1000L + tail;

            byteBuffer.position(0);
            byteBuffer.limit(19);
        }
    }


    private long transLong2(long half) {
        long upper = (half & 0x000f000f000f000fL) * 10;
        long lower = (half & 0x0f000f000f000f00L) >> 8;
        half = lower + upper;

        upper = (half & 0x000000ff000000ffL) * 100;
        lower = (half & 0x00ff000000ff0000L) >> 16;
        half = lower + upper;

        upper = (half & 0x000000000000ffffL) * 10000;
        lower = (half & 0x0000ffff00000000L) >> 32;

        return lower + upper;
    }

    private void common2(byte[] bytes) {
        long total = 0L;
        for (long i = 0; i < 300000000L; i++) {
            total = 0L;
            total += (bytes[0] - 48) * 1000000000000000000L;
            total += (bytes[1] - 48) * 100000000000000000L;
            total += (bytes[2] - 48) * 10000000000000000L;
            total += (bytes[3] - 48) * 1000000000000000L;
            total += (bytes[4] - 48) * 100000000000000L;
            total += (bytes[5] - 48) * 10000000000000L;
            total += (bytes[6] - 48) * 1000000000000L;
            total += (bytes[7] - 48) * 100000000000L;
            total += (bytes[8] - 48) * 10000000000L;
            total += (bytes[9] - 48) * 1000000000L;
            total += (bytes[10] - 48) * 100000000L;
            total += (bytes[11] - 48) * 10000000L;
            total += (bytes[12] - 48) * 1000000L;
            total += (bytes[13] - 48) * 100000L;
            total += (bytes[14] - 48) * 10000L;
            total += (bytes[15] - 48) * 1000L;
            total += (bytes[16] - 48) * 100L;
            total += (bytes[17] - 48) * 10L;
            total += (bytes[18] - 48);
        }
        System.out.println(total);
    }

    private void common(byte[] bytes) {
        long total = 0L;
        for (long i = 0; i < 300000000L; i++) {
            total = 0L;
            total = total * 10 + bytes[0] - 48;
            total = total * 10 + bytes[1] - 48;
            total = total * 10 + bytes[2] - 48;
            total = total * 10 + bytes[3] - 48;
            total = total * 10 + bytes[4] - 48;
            total = total * 10 + bytes[5] - 48;
            total = total * 10 + bytes[6] - 48;
            total = total * 10 + bytes[7] - 48;
            total = total * 10 + bytes[8] - 48;
            total = total * 10 + bytes[9] - 48;
            total = total * 10 + bytes[10] - 48;
            total = total * 10 + bytes[11] - 48;
            total = total * 10 + bytes[12] - 48;
            total = total * 10 + bytes[13] - 48;
            total = total * 10 + bytes[14] - 48;
            total = total * 10 + bytes[15] - 48;
            total = total * 10 + bytes[16] - 48;
            total = total * 10 + bytes[17] - 48;
            total = total * 10 + bytes[18] - 48;
        }
        System.out.println(total);
    }
}
