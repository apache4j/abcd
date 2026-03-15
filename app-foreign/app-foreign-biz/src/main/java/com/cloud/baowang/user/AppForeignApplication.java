package com.cloud.baowang.user;

import com.cloud.baowang.common.feign.annotations.EnableBwFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@EnableBwFeignClients
public class AppForeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppForeignApplication.class, args);
    }

}
