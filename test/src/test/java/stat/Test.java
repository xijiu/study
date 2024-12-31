package stat;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author xijiu
 * @since 2023/2/2 上午8:38
 */
public class Test implements MyService<String> {

    private int num = 8;

    protected GraphUtil graphUtil = new GraphUtil();

    public Test() {
        new Thread(() -> System.out.println(num)).start();
    }

    public static void main(String[] args) throws Exception {
        CompletableFuture<String> future = new CompletableFuture<>();
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
                future.complete("success");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();

        System.out.println("wait thread completed");

        String result = future.get();
        System.out.println("thread completed, result is " + result);
    }


    public static class Son1 extends Test {
        public Son1() {}
    }

    public static class Son2 extends Test {
        public Son2() {}
    }

    @org.junit.Test
    public void test3() {
        Son1 son1 = new Son1();
        Son2 son2 = new Son2();
        System.out.println(son1.graphUtil);
        System.out.println(son2.graphUtil);
    }
}
