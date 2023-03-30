package stat;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xijiu
 * @since 2023/3/3 上午11:15
 */
public class FileTest {

    @Test
    public void test() throws Exception {
        File file = new File("/Users/likangning/test/00000000000001234.log");
        if (file.exists()) {
            file.delete();
        }
//        if (1 == 1) {
//            file.createNewFile();
//            FileChannel channel = FileChannel.open(file.toPath());
//            ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
//            channel.read(byteBuffer);
//            channel.close();
//            return;
//        }

        if (1 == 2) {
            FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.READ,
                    StandardOpenOption.WRITE);
        } else {
            long initFileSize = 1024 * 1024 * 1024;
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.setLength(initFileSize);
            FileChannel channel = randomAccessFile.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

            System.out.println(channel.size());
            System.out.println(file.length());

            channel.write(byteBuffer);
            channel.close();
        }
    }


    @Test
    public void test3() throws Exception {
        String line = "Disk /dev/vdd: 76.2GB";
        Pattern patternFdisk = Pattern.compile("^Disk (/dev/\\S+):\\s+([0-9.]+.)B$");

        Matcher matcher = patternFdisk.matcher(line);
        if (!matcher.matches() || matcher.groupCount() != 2) {
            System.out.println("error");
        }
        // NOTE(hjd): 1是kafka单盘固定1个分区
        System.out.println(matcher.group(1) + "1");
        System.out.println((long) Math.ceil(string2Kb(matcher.group(2), 1000) / 1000.0 / 1000));
    }

    public static long string2Kb(String strValue, long base) throws Exception {
        char unit = strValue.charAt(strValue.length() - 1);
        if (Character.isDigit(unit)) {
            return Long.parseLong(strValue);
        }
        double value = Double.parseDouble(strValue.substring(0, strValue.length() - 1));
        if (unit == 'T' || unit == 't') {
            return (long) (base * base * base * value);
        } else if (unit == 'G' || unit == 'g') {
            return (long) (base * base * value);
        } else if (unit == 'M' || unit == 'm') {
            return (long) (base * value);
        } else if (unit == 'K' || unit == 'k') {
            return (long) value;
        } else {
            throw new Exception("not support strValue=" + strValue);
        }
    }





    @Test
    public void test5() {
        FileTest fileTest = new FileTest();
        System.out.println(fileTest.lengthOfLongestSubstring2("abcdecabxyz"));
    }

    public int lengthOfLongestSubstring(String s) {
        int result = 0;
        int beginIndex = 0;
        int endIndex = 0;
        int invalid = 0;
        char[] chars = s.toCharArray();
        int[] exist = new int[127];
        for (int i = 0; i < chars.length; i++) {
            char currChar = chars[i];
            endIndex = i;
            if (exist[currChar] == 0 || exist[currChar] <= invalid) {
                result = endIndex - beginIndex + 1 > result ? endIndex - beginIndex + 1 : result;
            } else {
                beginIndex = exist[currChar];
                invalid = beginIndex;
            }
            exist[currChar] = i + 1;
        }
        return result;
    }



    public int lengthOfLongestSubstring2(String s) {
        if (s.length()==0) return 0;
        HashMap<Character, Integer> map = new HashMap<Character, Integer>();
        int max = 0;
        int left = 0;
        for(int i = 0; i < s.length(); i ++){
            if(map.containsKey(s.charAt(i))){
                left = Math.max(left,map.get(s.charAt(i)) + 1);
            }
            map.put(s.charAt(i),i);
            max = Math.max(max,i-left+1);
        }
        return max;

    }

    public static void main(String[] args) {
        System.out.println(new FileTest().checkArithmeticSubarrays(new int[]{4,6,5,9,3,7}, new int[]{0,0,2}, new int[]{2,3,5}));
    }

    public List<Boolean> checkArithmeticSubarrays(int[] nums, int[] l, int[] r) {
        List<Boolean> result = new ArrayList<>(l.length);
        int arrLen = 0;
        for (int i = 0; i < l.length - 1; i++) {
            arrLen = Math.max(arrLen, r[i] - l[i] + 1);
        }
        int[] arr = new int[arrLen];
        for (int i = 0; i < l.length; i++) {
            int beginIndex = l[i];
            int endIndex = r[i];
            int length = endIndex - beginIndex + 1;
            System.arraycopy(nums, beginIndex, arr, 0, length);
            Arrays.sort(arr, 0, length);
            int diff = arr[1] - arr[0];
            boolean val = true;
            for (int j = 0; j < length - 1; j++) {
                if (arr[j + 1] - arr[j] != diff) {
                    val = false;
                    break;
                }
            }
            result.add(val);
        }
        return result;
    }

    @Test
    public void test9() {
        int i = new FileTest().searchInsert(new int[]{1, 3, 5, 6}, 5);
        System.out.println(i);
    }


    public int searchInsert(int[] nums, int target) {
        if (nums.length == 1) {
            return nums[0] >= target ? 0 : 1;
        }
        int left = 0;
        int right = nums.length - 1;
        int mid = (left + right) / 2;
        while (true) {
            if (left == right) {
                return nums[left] >= target ? left : left + 1;
            }
            if (right - left == 1) {
                if (nums[left] == target) {
                    return left;
                }
                if (nums[right] == target) {
                    return right;
                }
                if (nums[right] > target && nums[left] < target) {
                    return left + 1;
                } else if (nums[left] > target) {
                    return left;
                } else {
                    return right + 1;
                }
            }
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid;
            } else {
                right = mid;
            }
            mid = (left + right) / 2;
        }
    }



    @Test
    public void test55() {

    }

    /**
     * Definition for singly-linked list.
     * public class ListNode {
     *     int val;
     *     ListNode next;
     *     ListNode() {}
     *     ListNode(int val) { this.val = val; }
     *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
     * }
     */
    public ListNode reverseBetween(ListNode head, int left, int right) {
        int i = 1;
        ListNode prev = null;
        ListNode curr = head;
        ListNode littleHead = null;
        ListNode littleTail = null;
        ListNode wBegin = null;
        ListNode wEnd = null;
        while (true) {
            if (i == right + 1) {
                wEnd = curr;
            }

            if (curr == null || i > right) {
                break;
            }

            ListNode next = curr.next;


            if (i >= left && i <= right) {
                curr.next = prev;
            }
            if (i == left) {
                littleTail = curr;
            }
            if (i == right) {
                littleHead = curr;
            }

            if (i == left - 1) {
                wBegin = curr;
            }

            prev = curr;
            curr = next;
            i++;
        }

        if (wBegin != null) {
            wBegin.next = littleHead;
        }
        littleTail.next = wEnd;

        if (left > 1) {
            return head;
        } else {
            return littleHead;
        }

    }

    public class ListNode {
      int val;
      ListNode next;
      ListNode() {}
      ListNode(int val) { this.val = val; }
      ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

}
