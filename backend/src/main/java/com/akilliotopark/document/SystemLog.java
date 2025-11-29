package com.akilliotopark.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "system_logs")
public class SystemLog {

    @Id
    private String id;

    private String serviceName;
    private String type;
    private String message;

    private Object metadata;

    private LocalDateTime timestamp;
}