package com.cloud.baowang;

import com.cloud.baowang.common.feign.annotations.EnableBwFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 调度中心
 * 所有模块的定时任务入口统一在这个模块里处理
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableBwFeignClients
public class JobServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobServerApplication.class, args);
    }
}