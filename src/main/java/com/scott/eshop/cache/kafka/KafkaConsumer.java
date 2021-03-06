package com.scott.eshop.cache.kafka;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * kafka消费者
 * @ClassName KafkaConsumer
 * @Description
 * @Author 47980
 * @Date 2020/5/1 22:14
 * @Version V_1.0
 **/
public class KafkaConsumer implements Runnable {

    private ConsumerConnector consumerConnector;

    private String topic;

    public KafkaConsumer(String topic) {
        this.consumerConnector = Consumer.createJavaConsumerConnector(createConsumerConfig());
        this.topic = topic;
    }

    @Override
    public void run() {
        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(topic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

        for (final KafkaStream stream: streams) {
            new Thread(new KafkaMassageProcessor(stream)).start();
        }
    }

    /**
     * 创建kafka consumer config
     * @return
     */
    private static ConsumerConfig createConsumerConfig() {
        Properties props = new Properties();
        props.put("zookeeper.connect", "192.168.52.115:2181,192.168.52.113:2181,192.168.52.107:2181");
        props.put("group.id", "eshop-cache-group");
        props.put("zookeeper.session.timeout.ms", "40000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        return new ConsumerConfig(props);
    }
}
