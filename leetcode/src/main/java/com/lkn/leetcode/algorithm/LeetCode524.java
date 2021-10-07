package com.lkn.leetcode.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LeetCode524 {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("ale");
        list.add("apple");
        list.add("monkey");
        list.add("plea");
        System.out.println(new LeetCode524().findLongestWord2("abpcplea", list));

        List<String> list2 = new ArrayList<>();
        list2.add("ba");
        list2.add("ab");
        list2.add("a");
        list2.add("b");
        System.out.println(new LeetCode524().findLongestWord2("bab", list2));
    }


    private static int maxLen = 1000;

    private static int[] posArr = new int[maxLen];

    private static int[] resultArr = new int[maxLen];

    private static int resultPos = 0;

    public String findLongestWord(String s, List<String> dictionary) {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char target = s.charAt(i);
            for (int j = 0; j < dictionary.size(); j++) {
                int posIndex = posArr[j];
                if (posIndex != -1) {
                    String matchStr = dictionary.get(j);
                    if (matchStr.charAt(posIndex) == target) {
                        posArr[j]++;
                        if (posIndex + 1 == matchStr.length()) {
                            posArr[j] = -1;
                            resultArr[resultPos++] = j;
                        }
                    }
                }
            }
        }

        int maxLen = 0;
        int resultIndex = -1;

        for (int i = 0; i < resultPos; i++) {
            String s1 = dictionary.get(resultArr[i]);
            if (s1.length() > maxLen) {
                maxLen = s1.length();
                resultIndex = resultArr[i];
            } else if (s1.length() == maxLen) {
                String originStr = dictionary.get(resultIndex);
                if (s1.compareTo(originStr) < 0) {
                    resultIndex = resultArr[i];
                }
            }
        }

        resultPos = 0;
        int size = dictionary.size();
        for (int i = 0; i < size; i++) {
            posArr[i] = 0;
        }

        if (resultIndex == -1) {
            return "";
        } else {
            return dictionary.get(resultIndex);
        }
    }


    public String findLongestWord2(String s, List<String> dictionary) {
        dictionary.sort((o1, o2) -> {
            if (o1.length() > o2.length()) {
                return -1;
            } else if (o1.length() < o2.length()) {
                return 1;
            } else {
                return o1.compareTo(o2);
            }
        });

        int length = s.length();
        char[] chars = s.toCharArray();
        for (String s1 : dictionary) {
            int pos = 0;
            char[] chars1 = s1.toCharArray();
            for (int i = 0; i < length; i++) {
                if (chars[i] == chars1[pos]) {
                    if (++pos == s1.length()) {
                        return s1;
                    }
                }
            }
        }
        return "";
    }
}
