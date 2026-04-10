package com.hisabnikash.erp.masterdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
@EnableKafka
@EnableJpaAuditing(auditorAwareRef = "securityAuditorAware")
public class MasterDataServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MasterDataServiceApplication.class, args);
    }
}
