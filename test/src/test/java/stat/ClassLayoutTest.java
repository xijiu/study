package stat;

import org.openjdk.jol.info.ClassLayout;

/**
 * @author xijiu
 * @since 2023/2/14 上午9:44
 */
public class ClassLayoutTest {
    private static class D {
        private boolean a = false;
        private boolean[] arr = new boolean[10000];
        private short b = 5;
    }

    public static void main(String[] args) {
        D d = new D();
        System.out.println(ClassLayout.parseInstance(d).toPrintable());
    }
}


