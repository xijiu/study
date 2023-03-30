package com.lkn.flink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkKafkaPartitioner;
import org.apache.flink.util.Collector;

import java.util.Properties;

/**
 * author: wanglei
 * create: 2022-09-21
 */
public class KafkaCount {


    public static final class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
            String[] tokens = value.toLowerCase().split("\\W+");
            for(String token: tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        }
    }

    public static void run() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        System.out.println("set kafka parameters!");
        Properties props = new Properties();
        String topic = "test";
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("group.id", topic);

        FlinkKafkaConsumer<String> myConsumer = new FlinkKafkaConsumer<>(topic, new SimpleStringSchema(), props);
        // 从队列的最起始位置开始消费
        myConsumer.setStartFromEarliest();
        DataStream<String> stream = env.addSource(myConsumer);
        DataStream<Tuple2<String, Integer>> counts = stream.flatMap(new LineSplitter())
                .keyBy(0)
                .sum(1);

        counts.print();
        env.execute("word count from kafka");


//        DataStreamSource<String> data = env.readTextFile("datas/1.txt");
//        FlinkKafkaProducer<String> flinkKafkaProducer = new FlinkKafkaProducer<>(topic, new SimpleStringSchema(), props, new FlinkKafkaPartitioner<String>() {
//            @Override
//            public int partition(String s, byte[] bytes, byte[] bytes1, String s2, int[] ints) {
//                return 0;
//            }
//        });
//        data.addSink(flinkKafkaProducer);
//        env.execute();
    }

//    private void producerTest() {
//        Properties props = new Properties();
//        SerializationSchema<String> serializationSchema = null;
//        FlinkKafkaProducer09<String> flinkKafkaProducer = new FlinkKafkaProducer09<String>("topicName", serializationSchema, props, new FlinkKafkaPartitioner<String>() {
//            @Override
//            public int partition(String s, byte[] bytes, byte[] bytes1, String s2, int[] ints) {
//                return 0;
//            }
//        });
//    }

    public static void main(String[] args) throws Exception {
        run();
    }

    public void produceTest() {

    }
}
