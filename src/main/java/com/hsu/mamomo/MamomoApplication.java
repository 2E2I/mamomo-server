package com.hsu.mamomo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableJpaRepositories("com.hsu.mamomo.repository.jpa")
public class MamomoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MamomoApplication.class, args);
    }

}
