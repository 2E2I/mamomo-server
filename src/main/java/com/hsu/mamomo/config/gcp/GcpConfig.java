package com.hsu.mamomo.config.gcp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
@PropertySource("classpath:application.yml")
public class GcpConfig {

    @Bean
    public Storage storage() throws IOException {
        StorageOptions storageOptions = StorageOptions.newBuilder()
                .setProjectId("digital-shadow-343012")
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(
                        "src\\main\\resources\\digital-shadow-343012-fbddea22c098.json"))).build();
        return storageOptions.getService();
    }

}
