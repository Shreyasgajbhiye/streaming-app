FROM ubuntu:22.04

RUN apt-get update && \
    apt-get install -y openjdk-21-jdk ffmpeg && \
    apt-get clean

WORKDIR /app
COPY target/video-processing-service.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]