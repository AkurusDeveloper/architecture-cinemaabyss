package com.cinemaabyss.events.kafka;

import com.cinemaabyss.events.model.BaseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class EventConsumer<T extends BaseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final org.apache.kafka.clients.consumer.Consumer<String, String> consumer;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;
    private boolean running = false;

    public EventConsumer(String bootstrapServers, String groupId, String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        this.consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
        this.consumer.subscribe(Collections.singletonList(topic));
        this.objectMapper = new ObjectMapper();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void start(Consumer<String> messageHandler) {
        if (running) {
            return;
        }

        running = true;
        executorService.submit(() -> {
            try {
                while (running) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    records.forEach(record -> {
                        messageHandler.accept(record.value());
                    });
                }
            } catch (Exception e) {
                logger.error("Ошибка в consumer loop: {}", e.getMessage(), e);
            } finally {
                consumer.close();
            }
        });
    }

    public void stop() {
        running = false;
        executorService.shutdown();
    }
}