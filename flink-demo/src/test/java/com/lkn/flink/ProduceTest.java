package com.lkn.flink;

import kafka_client_test.JavaKafkaConfigurer;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkKafkaPartitioner;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.junit.Test;

import java.util.Optional;
import java.util.Properties;

/**
 * @author xijiu
 * @since 2023/1/31 下午4:37
 */
public class ProduceTest {

    @Test
    public void produce() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        System.out.println("set kafka parameters!");
        String topic = "test1";

        Properties props = new Properties();
        //设置接入点，请通过控制台获取对应Topic的接入点
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "alikafka-post-cn-c4d32j52i006-1.alikafka.aliyuncs.com:9093,alikafka-post-cn-c4d32j52i006-2.alikafka.aliyuncs.com:9093,alikafka-post-cn-c4d32j52i006-3.alikafka.aliyuncs.com:9093");
        //设置SSL根证书的路径，请记得将XXX修改为自己的路径
        //与sasl路径类似，该文件也不能被打包到jar中
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/Users/likangning/study/study/flink-demo/src/test/resources/mix.4096.client.truststore.jks");
        //根证书store的密码，保持不变
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "KafkaOnsClient");
        //接入协议，目前支持使用SASL_SSL协议接入
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");

        // 设置SASL账号
        String saslMechanism = "PLAIN";
        String username = "alikafka_post-cn-c4d32j52i006";
        String password = "aEO7VSGhzG5qjPqFQdkAkAiETwT7HrIo";
        if (!JavaKafkaConfigurer.isEmpty(username)
                && !JavaKafkaConfigurer.isEmpty(password)) {
            String prefix = "org.apache.kafka.common.security.scram.ScramLoginModule";
            if ("PLAIN".equalsIgnoreCase(saslMechanism)) {
                prefix = "org.apache.kafka.common.security.plain.PlainLoginModule";
            }
            String jaasConfig = String.format("%s required username=\"%s\" password=\"%s\";", prefix, username, password);
            props.put("sasl.jaas.config", jaasConfig);
        }

        //SASL鉴权方式，保持不变
        props.put("sasl.mechanism", saslMechanism);
        //Kafka消息的序列化方式
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        //请求的最长等待时间
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30 * 1000);
        //设置客户端内部重试次数
        props.put(ProducerConfig.RETRIES_CONFIG, 5);
        //设置客户端内部重试间隔
        props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 3000);

        //hostname校验改成空
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");





//        DataStreamSource<String> data = env.readTextFile("/Users/likangning/test/flink_test.txt");
        int size = 200;
        String[] strings = new String[size];
        for (int i = 0; i < size; i++) {
            strings[i] = String.valueOf(i + 1);
        }
        DataStreamSource<String> data = env.fromElements(strings);
        System.out.println(1);
        FlinkKafkaProducer<String> flinkKafkaProducer = new FlinkKafkaProducer<>(topic, new SimpleStringSchema(), props, Optional.of(new FlinkKafkaPartitioner<String>() {
            @Override
            public int partition(String s, byte[] key, byte[] value, String s2, int[] ints) {
                return Math.abs(s.hashCode()) % 12;
            }
        }));

//        FlinkKafkaProducer<String> flinkKafkaProducer = new FlinkKafkaProducer<>(topic, new SimpleStringSchema(), props);
        System.out.println(2);
        data.addSink(flinkKafkaProducer);
        System.out.println(3);
        env.execute();
        System.out.println(4);
    }

}
