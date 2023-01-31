package com.lkn.leetcode.algorithm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xijiu
 * @since 2022/7/28 下午4:17
 */
public class LeetCode_1331 {

    public static void main(String[] args) {

        System.out.println(1 << 18);

//        int[] result = new LeetCode_1331().arrayRankTransform(new int[]{37, 12, 28, 9, 100, 56, 80, 5, 12});
//        System.out.println(Arrays.toString(result));
//
//        int[] result1 = new LeetCode_1331().arrayRankTransform(new int[]{100,100,100});
//        System.out.println(Arrays.toString(result1));
//
//        int[] result2 = new LeetCode_1331().arrayRankTransform(new int[]{40,10,20,30});
//        System.out.println(Arrays.toString(result2));
    }




    public int[] arrayRankTransform(int[] arr) {
        Map<Integer, Integer> map = new HashMap<>();
        int[] tmp = new int[arr.length];
        System.arraycopy(arr, 0, tmp, 0, arr.length);

        Arrays.sort(tmp);
        int index = 1;
        int lastEle = Integer.MAX_VALUE;
        for (int ele : tmp) {
            if (ele != lastEle) {
                map.put(ele, index++);
            }
            lastEle = ele;
        }

        int i = 0;
        for (int ele : arr) {
            tmp[i++] = map.get(ele);
        }
        return tmp;
    }
}
