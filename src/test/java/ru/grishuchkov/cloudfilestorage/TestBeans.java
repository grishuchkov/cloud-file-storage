package ru.grishuchkov.cloudfilestorage;

import io.minio.MinioClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MinIOContainer;

@TestConfiguration
public class TestBeans {

    @Bean(initMethod = "start", destroyMethod = "stop")
    MinIOContainer minIOContainer(){
        return new MinIOContainer("minio/minio:latest");
    }

    @Bean
    MinioClient minioClient(MinIOContainer minIOContainer){
        return MinioClient.builder()
                .endpoint(minIOContainer.getS3URL())
                .credentials(minIOContainer.getUserName(), minIOContainer.getPassword())
                .build();
    }
}
