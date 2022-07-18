package jmx.test1;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xijiu
 * @since 2022/3/18 下午4:43
 */
public class Hello implements HelloService {
    private String name;
    private Map<Long, Long> networkFlow;

    @Override
    public Map<Long, Long> getNetworkFlow() {
        System.out.println("invoke getNetworkFlow");
        Map<Long, Long> map = new HashMap<>();
        map.put(1L, 1L);
        map.put(2L, 2L);
        map.put(3L, 3L);
        return map;
    }

    @Override
    public String getName() {
        System.out.println("invoke getName");
        return "my name is hello";
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String printHello() {
        return "Hello "+ name;
    }

    @Override
    public String printHello(String whoName) {
        return "Hello  " + whoName;
    }
}
