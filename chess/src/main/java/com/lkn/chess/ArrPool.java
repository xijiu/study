package com.lkn.chess;

import java.util.LinkedList;

/**
 * @author xijiu
 * @since 2023/5/5 下午3:28
 */
public class ArrPool {
    private static LinkedList<byte[]> linkedList = new LinkedList<>();

    public static byte[] borrow() {
        return new byte[19];
//        byte[] arr = linkedList.poll();
//        if (arr == null) {
//            arr = new byte[19];
//        }
//        return arr;
    }

    public static void giveBack(byte[] arr) {
//        linkedList.add(arr);
    }
}
