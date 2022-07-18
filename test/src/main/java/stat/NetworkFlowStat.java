package stat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 网络流量统计
 * 按照分钟粒度统计
 *
 * @author xijiu
 * @since 2022/3/22 上午10:34
 */
public class NetworkFlowStat implements NetworkFlowStatService {

    /** 统计的分钟 */
    private static final int MAX_SIZE = 5 * 60;

    /** 分钟长度 */
    private static final long MINUTE_LEN = 60 * 1000;

    /** 用来暂存流量数据 */
    private static final LinkedHashMap<Long, Network> FLOW_MAP = new FixedSizeLinkedHashMap<>();

    /** FLOW_MAP中存储的最大时间戳 */
    private static volatile long MAX_EXIST_TIME = -1L;

    /**
     * 收集入流量
     *
     * @param bytes 流量
     */
    public static void collectInBytes(long bytes) {
        Network network = getNetwork();
        network.addInBytes(bytes);
    }

    /**
     * 收集出流量
     *
     * @param bytes 流量
     */
    public static void collectOutBytes(long bytes) {
        Network network = getNetwork();
        network.addOutBytes(bytes);
    }

    /**
     * 获取网络包装对象，如果没有则新建
     *
     * @return  保证对象
     */
    private static Network getNetwork() {
        long minuteBegin = System.currentTimeMillis() / MINUTE_LEN * MINUTE_LEN;
        return getNetwork(minuteBegin);
    }

    /**
     * 获取当前时间对应的Network对象，如果没有就新建
     * 如果FLOW_MAP当前的key组成为： [20,21,22]，而当前时间已经达到了30分钟，说明在23-29期间没有流量，因此需要将key补齐
     *
     * @param time  时间
     */
    private static Network getNetwork(long time) {
        Network network = FLOW_MAP.get(time);
        if (network == null) {
            synchronized (NetworkFlowStat.class) {
                network = FLOW_MAP.get(time);
                if (network == null) {
                    if (MAX_EXIST_TIME == -1L) {
                        // 系统初始化时，将当前时间前5分钟的数据都设置为0
                        for (long tmpTime = time - 5 * MINUTE_LEN; tmpTime <= time; tmpTime += MINUTE_LEN) {
                            FLOW_MAP.put(tmpTime, new Network());
                        }
                    } else {
                        for (long tmpTime = MAX_EXIST_TIME + MINUTE_LEN; tmpTime <= time; tmpTime += MINUTE_LEN) {
                            FLOW_MAP.put(tmpTime, new Network());
                        }
                    }
                    MAX_EXIST_TIME = Math.max(time, MAX_EXIST_TIME);
                    network = FLOW_MAP.get(time);
                }
            }
        }
        return network;
    }

    @Override
    public Map<Long, Long[]> getNetworkFlow() {
        // 定义返回结果
        Map<Long, Long[]> resultMap = new LinkedHashMap<>();
        // 计算最大有效时间
        long maxValidTime = calculateMaxValidTime();
        // 采集数据
        collectData(resultMap, maxValidTime);
        return resultMap;
    }

    /**
     * 收集数据
     *
     * @param resultMap 最终结果
     * @param maxValidTime  最大有效时间戳
     */
    private void collectData(Map<Long, Long[]> resultMap, long maxValidTime) {
        // 请求的时间戳已经大于标记的最大时间戳了（说明数据断流了，有些分钟数据没有收集上来），需要将数据补齐
        if (maxValidTime > MAX_EXIST_TIME) {
            getNetwork(maxValidTime);
        }

        // 数据收集，从FLOW_MAP列表中最小时间戳开始收集
        Map.Entry<Long, Network> first = FLOW_MAP.entrySet().iterator().next();
        Long minTime = first.getKey();
        for (long time = minTime; time <= maxValidTime; time += MINUTE_LEN) {
            Network network = FLOW_MAP.get(time);
            resultMap.put(time, new Long[] {network.inBytes.get(), network.outBytes.get()});
        }
    }

    /**
     * 计算最大的有效时间，分以下两种情况：
     * 1、如果采集时间是[5:01-6:00)  说明第4分钟的数据已经全部生成，那么返回第4分钟的时间
     * 2、如果采集时间是[5:00-5:01)  虽然已经超过了5分，但数据可能还未准备就绪，故返回第3分钟的时间
     *
     * @return  最大有效时间
     */
    private long calculateMaxValidTime() {
        long now = System.currentTimeMillis();
        long minuteBegin = now / MINUTE_LEN * MINUTE_LEN;
        long maxValidTime;
        if (now - minuteBegin > 1000) {
            maxValidTime = minuteBegin - MINUTE_LEN;
        } else {
            maxValidTime = minuteBegin - 2 * MINUTE_LEN;
        }
        return maxValidTime;
    }

    /**
     * 封装map，使其设定为固定大小，在超过最大size时自动删除过期数据
     *
     * @param <K>   泛型
     * @param <V>   泛型
     */
    private static class FixedSizeLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > MAX_SIZE;
        }
    }

    public static class Network {
        /** 进流量 */
        private final AtomicLong inBytes = new AtomicLong();

        /** 出流量 */
        private final AtomicLong outBytes = new AtomicLong();

        public void addInBytes(long flow) {
            inBytes.addAndGet(flow);
        }

        public void addOutBytes(long flow) {
            outBytes.addAndGet(flow);
        }
    }

}
