package ru.grishuchkov.cloudfilestorage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("ru.grishuchkov.cloudfilestorage.repository")
public class AppConfig {
}
