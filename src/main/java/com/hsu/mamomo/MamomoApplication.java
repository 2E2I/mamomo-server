package com.hsu.mamomo;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.repository.CampaignRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class MamomoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MamomoApplication.class, args);
	}

}
