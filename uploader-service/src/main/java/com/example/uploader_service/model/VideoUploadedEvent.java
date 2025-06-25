// Lombok is a Java library that automatically generates boilerplate code like:

// Getters and setters

// Constructors

// toString()

// equals() and hashCode()

// @Data → All getters, setters, toString(), equals(), and hashCode().

// @AllArgsConstructor → A constructor with all fields as parameters.

// @NoArgsConstructor → A no-argument constructor.

package com.example.uploader_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoUploadedEvent {
    private String videoId;
    private String filePath;
    private String title;
}
