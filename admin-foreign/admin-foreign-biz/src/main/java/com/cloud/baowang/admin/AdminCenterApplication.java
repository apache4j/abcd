package com.cloud.baowang.admin;

import com.cloud.baowang.common.feign.annotations.EnableBwFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用户统一管理系统
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableBwFeignClients
public class AdminCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminCenterApplication.class, args);
    }

}
