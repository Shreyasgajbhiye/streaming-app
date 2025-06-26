package com.example.uploader_service.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.example.uploader_service.model.VideoUploadedEvent;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, VideoUploadedEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, VideoUploadedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}


//  Annotations:
// @Configuration: Marks this class as a configuration class so Spring knows to load the beans defined inside.

// @Bean: Tells Spring to create and manage the returned object as a bean in the application context.

// Creates a Kafka Producer Factory that knows how to:

// Connect to Kafka on kafka:9092

// Serialize keys as String

// Serialize values as JSON using JsonSerializer (for VideoUploadedEvent)