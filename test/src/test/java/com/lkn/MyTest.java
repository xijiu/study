package com.lkn;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MyTest {

    @Test
    public void test() {
        String str = "1234567890123456789";
        byte[] bytes = str.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.nativeOrder());

        long long1 = byteBuffer.getLong();
        long1 = transLong(long1);
        long long2 = byteBuffer.getLong();
        long2 = transLong(long2);
        long byte1 = byteBuffer.get();
        byte1 &= 0x0f;
        long byte2 = byteBuffer.get();
        byte2 &= 0x0f;
        long byte3 = byteBuffer.get();
        byte3 &= 0x0f;

        long tail = byte1 * 100 + byte2 * 10 + byte3;
        long res_0 = long1 * 100000000000L + long2 * 1000L + tail;
        System.out.println(res_0);
    }

    private long transLong(long half) {
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
}
