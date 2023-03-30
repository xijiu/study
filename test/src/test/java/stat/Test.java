package stat;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author xijiu
 * @since 2023/2/2 上午8:38
 */
public class Test implements MyService<String> {

    private int num = 8;

    private GraphUtil graphUtil = new GraphUtil();

    private Test() {
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
}
