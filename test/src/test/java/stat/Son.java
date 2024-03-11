package stat;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xijiu
 * @since 2022/10/11 下午3:26
 */
public class Son extends Father {
    private static int a = sum();

    private ByteBuffer byteBuffer = ByteBuffer.allocate(50 * 1024 * 1024);

    static {
        System.out.println("son static");
    }

    private static int sum() {
        System.out.println("sum");

        return 0;
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("a", "a");
        map.put("b", "b");
        map.put("c", "c");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equals("a")) {
                map.remove(entry.getKey());
            }
        }
        System.out.println(map);
    }





}
