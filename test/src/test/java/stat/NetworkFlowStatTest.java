package stat;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author xijiu
 * @since 2022/3/22 下午8:42
 */
public class NetworkFlowStatTest {

    private LinkedHashMap<Long, NetworkFlowStat.Network> flowMap = initFlowMap();

    private final int maxMinuteSize = getMAX_SIZE();

    private int getMAX_SIZE() {
        try {
            Field field = NetworkFlowStat.class.getDeclaredField("MAX_SIZE");
            field.setAccessible(true);
            return (Integer) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private LinkedHashMap<Long, NetworkFlowStat.Network> initFlowMap() {
        try {
            Field field = NetworkFlowStat.class.getDeclaredField("FLOW_MAP");
            field.setAccessible(true);
            LinkedHashMap<Long, NetworkFlowStat.Network> flowMap = (LinkedHashMap) field.get(null);
            return flowMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 数据断流超过了5小时
     */
    @Test
    public void test() throws Exception {
        reset();
        mockFlowData("2022-03-21 08:21:00");
        mockFlowData("2022-03-21 08:22:00");
        mockFlowData("2022-03-21 08:23:00");
        mockMaxExistTime("2022-03-21 08:23:00");
        Map<Long, Long[]> networkFlow = new NetworkFlowStat().getNetworkFlow();
        printNetworkFlow(networkFlow);
        printFLOW_MAP();
        Assert.assertEquals(networkFlow.size(), maxMinuteSize);
        Assert.assertEquals(flowMap.size(), maxMinuteSize);
    }

    /**
     * 当数据还没有产生，初次访问
     */
    @Test
    public void test2() throws Exception {
        reset();
        Map<Long, Long[]> networkFlow = new NetworkFlowStat().getNetworkFlow();
        printNetworkFlow(networkFlow);
        printFLOW_MAP();
        Assert.assertEquals(6, networkFlow.size());
        Assert.assertEquals(6, flowMap.size());
        Map.Entry<Long, Long[]> entry = networkFlow.entrySet().iterator().next();
        Long[] value = entry.getValue();
        Assert.assertEquals(value[0], new Long(0));
        Assert.assertEquals(value[1], new Long(0));
    }

    /**
     * 数据刚产生的1分钟
     */
    @Test
    public void test3() throws Exception {
        reset();
        for (int i = 0; i < 10; i++) {
            NetworkFlowStat.collectInBytes(10);
            NetworkFlowStat.collectOutBytes(20);
        }

        Map<Long, Long[]> networkFlow = new NetworkFlowStat().getNetworkFlow();
        printNetworkFlow(networkFlow);
        printFLOW_MAP();
        Assert.assertTrue(networkFlow.size() == 5 || networkFlow.size() == 4);
        Collection<Long[]> values = networkFlow.values();
        for (Long[] value : values) {
            Assert.assertEquals(value[0], new Long(0));
            Assert.assertEquals(value[1], new Long(0));
        }

        Assert.assertEquals(flowMap.size(), 6);
        int index = 0;
        for (NetworkFlowStat.Network network : flowMap.values()) {
            if (index < 5) {
                Assert.assertEquals(networkInBytes(network), 0);
                Assert.assertEquals(networkOutBytes(network), 0);
            } else {
                Assert.assertEquals(networkInBytes(network), 100);
                Assert.assertEquals(networkOutBytes(network), 200);
            }
            index++;
        }
    }

    private void reset() {
        flowMap.clear();
        mockMaxExistTime("-1");
    }

    private void printNetworkFlow(Map<Long, Long[]> networkFlow) {
        System.out.println("total size is " + networkFlow.size());
        for (Map.Entry<Long, Long[]> entry : networkFlow.entrySet()) {
            Long time = entry.getKey();
            Long[] value = entry.getValue();
            System.out.println(longToTimeStr(time) + ": [" + value[0] + ", " + value[1] + "]");
        }
    }


    private void printFLOW_MAP() throws Exception {
        System.out.println("flowMap size is " + flowMap.size());
        Field inBytesField = NetworkFlowStat.Network.class.getDeclaredField("inBytes");
        inBytesField.setAccessible(true);
        Field outBytesField = NetworkFlowStat.Network.class.getDeclaredField("outBytes");
        outBytesField.setAccessible(true);
        for (Map.Entry<Long, NetworkFlowStat.Network> entry : flowMap.entrySet()) {
            Long key = entry.getKey();
            NetworkFlowStat.Network network = entry.getValue();
            System.out.println(longToTimeStr(key) + ": [" + inBytesField.get(network) + ", " + outBytesField.get(network) + "]");
        }
    }

    private long networkInBytes(NetworkFlowStat.Network network) throws Exception {
        Field inBytesField = NetworkFlowStat.Network.class.getDeclaredField("inBytes");
        inBytesField.setAccessible(true);
        AtomicLong inBytes = (AtomicLong) inBytesField.get(network);
        return inBytes.get();
    }

    private long networkOutBytes(NetworkFlowStat.Network network) throws Exception {
        Field outBytesField = NetworkFlowStat.Network.class.getDeclaredField("outBytes");
        outBytesField.setAccessible(true);
        AtomicLong outBytes = (AtomicLong) outBytesField.get(network);
        return outBytes.get();
    }


    private void mockMaxExistTime(String timeStr) {
        try {
            Field field = NetworkFlowStat.class.getDeclaredField("MAX_EXIST_TIME");
            field.setAccessible(true);
            long time = Objects.equals(timeStr, "-1") ? -1 : timeStrToLong(timeStr);
            field.set(null, time);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void mockFlowData(String timeStr) throws ParseException {
        mockFlowData(timeStr, 1L, 1L);
    }

    private void mockFlowData(String timeStr, long inBytes, long outBytes) throws ParseException {
        NetworkFlowStat.Network network = new NetworkFlowStat.Network();
        network.addInBytes(inBytes);
        network.addOutBytes(outBytes);
        flowMap.put(timeStrToLong(timeStr), network);
    }

    private long timeStrToLong(String timeStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(timeStr).getTime();
    }

    private String longToTimeStr(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(time));
    }
}
