package com.hsu.mamomo.config.jpa;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
@PropertySource("classpath:application.yml")
public class JpaConfig {

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://34.64.53.227:3306/mamomo?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul")
                .username("root")
                .password("mamomo!mysql")
                .build();
    }
}

