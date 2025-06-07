package com.cinemaabyss.events.kafka;

import com.cinemaabyss.events.model.BaseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class EventProducer<T extends BaseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private final Producer<String, String> producer;
    private final ObjectMapper objectMapper;

    public EventProducer(String bootstrapServers) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
        this.objectMapper = new ObjectMapper();
    }

    public void send(String topic, T event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, event.getId(), json);
            producer.send(
                    record, (metadata, exception) -> {
                        if (exception == null) {
                            logger.info(
                                    "Отправлено событие: topic={}, partition={}, offset={}",
                                    metadata.topic(), metadata.partition(), metadata.offset()
                            );
                        } else {
                            logger.error("Ошибка отправки события: {}", exception.getMessage(), exception);
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("Ошибка сериализации события: {}", e.getMessage(), e);
        }
    }

    public void close() {
        producer.close();
    }
}