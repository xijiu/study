package file_test;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.Test;
import sun.misc.SharedSecrets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author xijiu
 * @since 2022/1/11 下午3:39
 */
public class MyTest {

    @Test
    public void test4() {
        System.out.println(Math.ceil(2.333D));
        System.out.println(String.valueOf((int) 0.0D));

    }

    @Test
    public void test() {
        ServerlessUpdateDataDo dataDo = new ServerlessUpdateDataDo();
        dataDo.setInstanceId("alikafka_serverless_public_cn-0ju2io2d1001");
        dataDo.setLastUpdateTimestamp(System.currentTimeMillis());
        dataDo.setSleepToTimestamp(System.currentTimeMillis());
        dataDo.setOperator(ServerlessOperatorEnum.REBALANCE);

        Map<String, ServerlessUpdateDataDo.TopicPartitionInfo> partitionInfoMap = Maps.newHashMap();
        ServerlessUpdateDataDo.TopicPartitionInfo topicPartitionInfo = new ServerlessUpdateDataDo.TopicPartitionInfo();
        topicPartitionInfo.setTopic("test");
        Map<String, String> partitionMap = Maps.newHashMap();
        partitionMap.put("0", "101");
        partitionMap.put("1", "102");
        partitionMap.put("2", "101");
        partitionMap.put("3", "102");
        partitionMap.put("4", "101");
        partitionMap.put("5", "102");
        partitionMap.put("6", "101");
        partitionMap.put("7", "102");
        partitionMap.put("8", "101");
        partitionMap.put("9", "102");
        partitionMap.put("10", "101");
        partitionMap.put("11", "102");
        topicPartitionInfo.setPartitionMap(partitionMap);
        partitionInfoMap.put("test", topicPartitionInfo);

        dataDo.setPartitionInfoMap(partitionInfoMap);


        dataDo.setFromNodeSize(2);
        dataDo.setToNodeSize(0);
        System.out.println(JSON.toJSONString(dataDo));
    }

    @Test
    public void test3() {
        String brokerNamePre = "Kafka_alikafka_serverless_public_cn-0ju2io2d1001_";
        String json1 = "{\"0:uk_4kafka_alikafka_serverless_public_cn-0ju2io2d1001_d2_101\":3,\"0:uk_4kafka_alikafka_serverless_public_cn-0ju2io2d1001_d1_103\":2,\"0:uk_4kafka_alikafka_serverless_public_cn-0ju2io2d1001_d3_102\":7,\"0:uk_4kafka_alikafka_serverless_public_cn-0ju2io2d1001_d3_101\":6,\"0:uk_4kafka_alikafka_serverless_public_cn-0ju2io2d1001_d2_103\":5,\"0:uk_4kafka_alikafka_serverless_public_cn-0ju2io2d1001_d1_102\":1,\"0:uk_4kafka_alikafka_serverless_public_cn-0ju2io2d1001_d3_103\":8,\"0:uk_4kafka_alikafka_serverless_public_cn-0ju2io2d1001_d2_102\":4,\"0:uk_4kafka_alikafka_serverless_public_cn-0ju2io2d1001_d1_101\":0,\"0:uk_4kafka_alikafka_serverless_public_cn-0ju2io2d1001_d4_103\":11,\"0:uk_4kafka_alikafka_serverless_public_cn-0ju2io2d1001_d4_102\":10,\"0:uk_4kafka_alikafka_serverless_public_cn-0ju2io2d1001_d4_101\":9}";
        String json2 = "{\"0\":\"101\",\"11\":\"102\",\"1\":\"102\",\"2\":\"101\",\"3\":\"102\",\"4\":\"101\",\"5\":\"102\",\"6\":\"101\",\"7\":\"102\",\"8\":\"101\",\"9\":\"102\",\"10\":\"101\"}";
        Map<String, Integer> curQueueMap = JSON.parseObject(json1, Map.class);
        Map<String, String> partitionMap = JSON.parseObject(json2, Map.class);
        Map<String, Integer> map = generateNewQueueMap(brokerNamePre, curQueueMap, partitionMap);
        System.out.println(JSON.toJSONString(map));
    }

    private Map<String, Integer> generateNewQueueMap(String brokerNamePre, Map<String, Integer> curQueueMap,
                                                     Map<String, String> partitionMap) {

        Map<String, Integer> queueIdUsed = new HashMap<>();
        Map<Integer, Integer> partitionToBroker = new HashMap<>();
        Map<Integer, String> partitionToName = new HashMap<>();
        // 计算当前的数据，生成索引
        for (Map.Entry<String, Integer> entry : curQueueMap.entrySet()) {
            String[] strs = entry.getKey().split(":");
            if (strs[1].length() != 3 + brokerNamePre.length()) {
                return null;
            }
            Integer brokerId = Integer.valueOf(strs[1].substring(brokerNamePre.length()));
            int queueId = Integer.parseInt(strs[0]);
            //
            partitionToName.put(entry.getValue(), entry.getKey());
            queueIdUsed.put(brokerId + "-" + queueId, 0);
            partitionToBroker.put(entry.getValue(), brokerId);
        }

        Map<String, Integer> newQueueMap = new HashMap<>();
        // 新数据，生成索引
        for (Map.Entry<String, String> entry : partitionMap.entrySet()) {
            Integer partition = Integer.valueOf(entry.getKey());
            Integer newBroker = Integer.valueOf(entry.getValue());
            Integer curBroker = partitionToBroker.getOrDefault(partition, null);
            if (curBroker == null) {
                return null;
            }
            if (Objects.equals(curBroker, newBroker)) {
                newQueueMap.put(partitionToName.get(partition), partition);
                continue;
            }
            // 找1个未使用的queueId
            Integer queueId = 0;
            while (queueIdUsed.containsKey(newBroker + "-" + queueId)) {
                queueId++;
            }
            //
            queueIdUsed.put(newBroker + "-" + queueId, 1);
            newQueueMap.put(queueId + ":" + brokerNamePre + newBroker, partition);
        }
        return newQueueMap;
    }


    @Test
    public void abc() {
        try {
            String b = null;
            b.toCharArray();
        } catch (Throwable e) {
            System.out.println("begin");
            System.out.println(Throwables.getStackTraceAsString(e));
            System.out.println("end");
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    private static class User {
        private String name;
    }

    @Test
    public void abc2() throws Exception {
        User user1 = new User();
        System.out.println("init is " + user1);
        Thread thread1 = new Thread(() -> abc2(user1));
        Thread thread2 = new Thread(() -> abc2(user1));
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }

    private void abc2(User user) {
        System.out.println("abc2 is " + user);
        try {
            aaabbb(user);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void aaabbb(User user) throws InterruptedException {
        synchronized (user) {
            System.out.println("begin");
            Thread.sleep(10000000);
            System.out.println("end");
        }
    }


    @Test
    public void test444() {
        LinkedHashSet<Integer> assistSet = new LinkedHashSet<>();
        assistSet.add(1);
        assistSet.add(2);
        assistSet.add(3);
        assistSet.add(4);
        assistSet.add(5);
        assistSet.add(1);
        System.out.println(assistSet);
    }
}
