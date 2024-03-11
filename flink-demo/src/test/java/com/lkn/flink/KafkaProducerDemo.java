package com.lkn.flink;

import kafka_client_test.JavaKafkaConfigurer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaProducerDemo {

    public static void main(String args[]) {
        //设置sasl文件的路径
        JavaKafkaConfigurer.configureSasl();

        //加载kafka.properties
        Properties kafkaProperties =  JavaKafkaConfigurer.getKafkaProperties();

        Properties props = new Properties();
        //设置接入点，请通过控制台获取对应Topic的接入点
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "alikafka-post-cn-c4d32j52i006-1.alikafka.aliyuncs.com:9093,alikafka-post-cn-c4d32j52i006-2.alikafka.aliyuncs.com:9093,alikafka-post-cn-c4d32j52i006-3.alikafka.aliyuncs.com:9093");
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/Users/likangning/study/study/flink-demo/src/test/resources/mix.4096.client.truststore.jks");
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "KafkaOnsClient");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");

        // 设置SASL账号
        String saslMechanism = "PLAIN";
        String username = "alikafka_post-cn-c4d32j52i006";
        String password = "aEO7VSGhzG5qjPqFQdkAkAiETwT7HrIo";

        String prefix = "org.apache.kafka.common.security.scram.ScramLoginModule";
        prefix = "org.apache.kafka.common.security.plain.PlainLoginModule";
        String jaasConfig = String.format("%s required username=\"%s\" password=\"%s\";", prefix, username, password);
        props.put("sasl.jaas.config", jaasConfig);

        //SASL鉴权方式，保持不变
        props.put("sasl.mechanism", saslMechanism);
        //Kafka消息的序列化方式
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        //请求的最长等待时间
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30 * 1000);
        //设置客户端内部重试次数
        props.put(ProducerConfig.RETRIES_CONFIG, 5);
        //设置客户端内部重试间隔
        props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 3000);

        //hostname校验改成空
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");

        //构造Producer对象，注意，该对象是线程安全的，一般来说，一个进程内一个Producer对象即可；
        //如果想提高性能，可以多构造几个对象，但不要太多，最好不要超过5个
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);

        //构造一个Kafka消息
        String topic = "test"; //消息所属的Topic，请在控制台申请之后，填写在这里
        String value = "this is the message's value"; //消息的内容

        try {
            //批量获取 futures 可以加快速度, 但注意，批量不要太大
            List<Future<RecordMetadata>> futures = new ArrayList<>(128);
            for (int i =0; i < 100; i++) {
                //发送消息，并获得一个Future对象
                ProducerRecord<String, String> kafkaMessage =  new ProducerRecord<String, String>(topic, value + ": " + i);
                Future<RecordMetadata> metadataFuture = producer.send(kafkaMessage);
                futures.add(metadataFuture);

            }
            producer.flush();
            for (Future<RecordMetadata> future: futures) {
                //同步获得Future对象的结果
                try {
                    RecordMetadata recordMetadata = future.get();
                    System.out.println("Produce ok:" + recordMetadata.toString());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } catch (Exception e) {
            //客户端内部重试之后，仍然发送失败，业务要应对此类错误
            //参考常见报错: https://help.aliyun.com/document_detail/68168.html?spm=a2c4g.11186623.6.567.2OMgCB
            System.out.println("error occurred");
            e.printStackTrace();
        }
    }


}