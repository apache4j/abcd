package com.cloud.baowang;

import com.cloud.baowang.common.feign.annotations.EnableBwFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@EnableBwFeignClients
public class PayServiceRunner {
    public static void main(String[] args) {
        SpringApplication.run(PayServiceRunner.class, args);
    }
}
