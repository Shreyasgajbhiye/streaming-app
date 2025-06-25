package com.example.uploader_service.controller;

import com.example.uploader_service.model.VideoUploadedEvent;
import com.example.uploader_service.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UploadController {

    private final KafkaProducerService producer;
    private static final String STORAGE_DIR = "/videos/raw/";

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "title", required = false) String title) {
        try {
            if (!file.getContentType().equals("video/mp4")) {
                return ResponseEntity.badRequest().body("Only .mp4 files allowed.");
            }

            String videoId = UUID.randomUUID().toString();
            Path path = Path.of(STORAGE_DIR, videoId + ".mp4");
            Files.createDirectories(path.getParent());
            file.transferTo(path.toFile());

            VideoUploadedEvent event = new VideoUploadedEvent(videoId, path.toString(), title != null ? title : "Untitled");
            producer.send(event);

            return ResponseEntity.ok("Uploaded. Video ID: " + videoId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }
}
