package com.example.uploader_service.service;

import com.example.uploader_service.model.VideoUploadedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, VideoUploadedEvent> kafkaTemplate;
    private static final String TOPIC = "video-uploaded";

    public void send(VideoUploadedEvent event) {
        kafkaTemplate.send(TOPIC, event.getVideoId(), event);
    }
}
