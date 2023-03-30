package com.lkn.flink;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * author: wanglei
 * create: 2022-09-21
 */
public class Producer {
    public static void main(String[] args) {
        System.setProperty("java.security.auth.login.config", "/Users/likangning/study/study/flink-demo/src/test/resources/kafka_client_jaas.conf");

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "alikafka-post-cn-uax30uivm00a-1.alikafka.aliyuncs.com:9093,alikafka-post-cn-uax30uivm00a-2.alikafka.aliyuncs.com:9093,alikafka-post-cn-uax30uivm00a-3.alikafka.aliyuncs.com:9093");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put("ssl.truststore.location", "/Users/likangning/study/study/flink-demo/src/test/resources/kafka.client.truststore.jks");
        properties.put("java.security.auth.login.config", "/Users/likangning/study/study/flink-demo/src/test/resources/kafka_client_jaas.conf");

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        String topic = "test";
        ProducerRecord record = new ProducerRecord(topic, "v1");
        producer.send(record);
        ProducerRecord record2 = new ProducerRecord(topic, "v2");
        producer.send(record2);
        producer.close();
    }
}
