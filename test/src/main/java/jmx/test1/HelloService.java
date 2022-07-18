package jmx.test1;

import java.util.Map;

public interface HelloService {
    String getName();
    void setName(String name);
    String printHello();
    String printHello(String whoName);
    Map<Long, Long> getNetworkFlow();
}

