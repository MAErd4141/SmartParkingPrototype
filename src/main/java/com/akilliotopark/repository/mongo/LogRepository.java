package com.akilliotopark.repository.mongo;

import com.akilliotopark.document.SystemLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends MongoRepository<SystemLog, String> {
    List<SystemLog> findByTypeOrderByTimestampDesc(String type);
}