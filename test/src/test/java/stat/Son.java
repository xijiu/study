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


}
