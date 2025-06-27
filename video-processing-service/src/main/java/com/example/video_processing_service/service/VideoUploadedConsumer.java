package com.example.video_processing_service.service;

import java.io.File;
import java.io.IOException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VideoUploadedConsumer {

    private static final Logger logger = LoggerFactory.getLogger(VideoUploadedConsumer.class);

    private static final String RAW_DIR = "/video-storage/raw/";
    private static final String PROCESSED_DIR = "/video-storage/processed/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "video-uploaded", groupId = "video-processor-group")
    public void handleVideoUpload(ConsumerRecord<String, String> record) throws IOException, InterruptedException {
        logger.info("✅ Received message: " + record.value());
        // ✅ Parse JSON string into Java object
        String messageJson = record.value();
        logger.info("✅ Message JSON: " + messageJson);


        VideoUploadPayload payload = objectMapper.readValue(messageJson, VideoUploadPayload.class);
        String videoId = payload.getVideoId();
        String filePath = payload.getFilePath();

        logger.info("✅ Processing video ID: " + videoId);
        logger.info("✅ File path: " + filePath);
        File input = new File(filePath);

        if (!input.exists()) {
            System.err.println("❌ File not found: " + input.getAbsolutePath());
            return;
        }
        else{
            logger.info("✅ File found: " + input.getAbsolutePath());
        }

        File outputFolder = new File(PROCESSED_DIR + videoId);
        outputFolder.mkdirs();

        runFFmpegCommands(input.getAbsolutePath(), outputFolder.getAbsolutePath());
    }

    private void runFFmpegCommands(String inputPath, String outputDir) throws IOException, InterruptedException {
        String[] resolutions = {"360", "480", "720"};

        for (String res : resolutions) {
            String output = outputDir + "/" + res + "p.m3u8";

            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-i", inputPath,
                    "-vf", "scale=-2:" + res,
                    "-c:a", "aac",
                    "-ar", "48000",
                    "-c:v", "h264",
                    "-profile:v", "main",
                    "-crf", "20",
                    "-sc_threshold", "0",
                    "-g", "48",
                    "-keyint_min", "48",
                    "-hls_time", "4",
                    "-hls_playlist_type", "vod",
                    output
            );
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        }

        // Master playlist
        File master = new File(outputDir + "/master.m3u8");
        String content =
                "#EXTM3U\n" +
                "#EXT-X-VERSION:3\n" +
                "#EXT-X-STREAM-INF:BANDWIDTH=800000,RESOLUTION=640x360\n360p.m3u8\n" +
                "#EXT-X-STREAM-INF:BANDWIDTH=1400000,RESOLUTION=854x480\n480p.m3u8\n" +
                "#EXT-X-STREAM-INF:BANDWIDTH=2800000,RESOLUTION=1280x720\n720p.m3u8\n";

        java.nio.file.Files.writeString(master.toPath(), content);
    }
}
