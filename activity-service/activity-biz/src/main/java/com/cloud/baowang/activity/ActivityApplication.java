package com.cloud.baowang.activity;

import com.cloud.baowang.common.feign.annotations.EnableBwFeignClients;
import com.github.jesse.l2cache.spring.EnableL2Cache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@EnableBwFeignClients
@EnableL2Cache
public class ActivityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActivityApplication.class, args);
    }

}
