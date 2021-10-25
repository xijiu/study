package com.lkn.leetcode.algorithm;

import org.junit.Test;

public class LeetCode_393 {

    @Test
    public void test() {
        // 10000000 -128
        // 10000001 -127
//        System.out.println(parseByte("10111111", 2));
//        System.out.println(parseByte("11000101", 2));
//        System.out.println(validUtf8(new int[]{197, 130, 1}));
//        System.out.println(validUtf8(new int[]{235, 140, 4}));
//        System.out.println(validUtf8(new int[]{255}));
        System.out.println(validUtf8(new int[]{248,130,130,130}));
    }

    public boolean validUtf8(int[] data) {
        // < -16   4个1
        // < -32   3个1
        // < -64   2个1
        int flag = 0;
        for (int datum : data) {
            byte b = (byte) datum;
//            System.out.println(b);
//            System.out.println(Long.toBinaryString(b));
            if (flag == 0) {
                if (b >= 0) {
                    continue;
                } else {
                    if (b >= -64 && b < -32) {
                        flag = 1;
                    } else if (b >= -32 && b < -16) {
                        flag = 2;
                    } else if (b >= -16 && b < -8) {
                        flag = 3;
                    } else {
                        return false;
                    }
                }
            } else {
                if (b >= -128 && b <= -65) {
                    flag--;
                    continue;
                } else {
                    return false;
                }
            }
        }
        return flag == 0;
    }

    public static byte parseByte(String s, int radix)
            throws NumberFormatException {
        int i = Integer.parseInt(s, radix);
        return (byte)i;
    }
}
