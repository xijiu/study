package com.lkn.leetcode.algorithm;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author xijiu
 * @since 2023/3/28 上午8:39
 */
public class LeetCode2279 {

    public static void main(String[] args) {
        int i = new LeetCode2279().maximumBags(new int[]{2, 3, 4, 5}, new int[]{1, 2, 4, 4}, 2);
        System.out.println(i);
    }

    public int maximumBags(int[] capacity, int[] rocks, int additionalRocks) {
        for (int i = 0; i < capacity.length; i++) {
            capacity[i] = capacity[i] - rocks[i];
        }
        Arrays.sort(capacity);
        int sum = 0;
        int result = 0;
        for (int value : capacity) {
            sum += value;
            if (sum > additionalRocks) {
                break;
            }
            result++;
        }
        return result;
    }


    @Test
    public void aa() {
        System.out.println(isPalindrome(121));
    }

    public int reverse(int x) {
        long result = 0;
        while (true) {
            if (x == 0) {
                break;
            }
            int tmp = x % 10;
            result = result * 10L + tmp;
            x /= 10;
        }
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
            return 0;
        }
        return (int) result;
    }



    public boolean isPalindrome(int x) {
        if (x < 0) {
            return false;
        }
        int origin = x;
        int sum = 0;
        while (true) {
            int tmp = x % 10;
            x /= 10;
            sum = sum * 10 + tmp;
            if (x == 0) {
                break;
            }
        }
        return sum == origin;
    }

}
