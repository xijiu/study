package com.lkn.test;

public class Constants {

    public static final boolean ONLINE;

    static {
        String property = System.getProperty("os.name");
        if (property != null && property.toLowerCase().contains("mac")) {
            ONLINE = false;
        } else {
            ONLINE = true;
        }
    }

    public static final String CLIENT_PROCESS_PORT1 = "8000";
    public static final String CLIENT_PROCESS_PORT2 = "8001";
    public static final String BACKEND_PROCESS_PORT1 = "8002";
    public static final int BACKEND_PROCESS_PORT2 = 8003;
    public static final String BACKEND_PROCESS_PORT3 = "8004";
    public static int BATCH_SIZE = 20000;
    public static int PROCESS_COUNT = 2;
}
