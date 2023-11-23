package ru.grishuchkov.cloudfilestorage.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProps {
    private String url;
    private String accessKey;
    private String secretKey;
}
