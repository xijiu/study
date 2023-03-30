package stat;

import org.junit.Test;

/**
 * @author xijiu
 * @since 2022/10/11 下午3:26
 */
public class Son extends Father {
    private static int a = sum();

    static {
        System.out.println("son static");
    }

    private static int sum() {
        System.out.println("sum");

        return 0;
    }

    public static void main(String[] args) {
        String str = "593G+357G+296G+253G";
        String[] split = str.split("\\+");
        int sum = 0;
        for (String s : split) {
            sum += Integer.parseInt(s.substring(0, 3));
        }
        System.out.println("sum is " + sum);
    }


}
