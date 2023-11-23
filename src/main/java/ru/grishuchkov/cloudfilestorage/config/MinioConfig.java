package ru.grishuchkov.cloudfilestorage.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.grishuchkov.cloudfilestorage.config.props.MinioProps;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {
    private final MinioProps properties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(properties.getUrl())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }

}
