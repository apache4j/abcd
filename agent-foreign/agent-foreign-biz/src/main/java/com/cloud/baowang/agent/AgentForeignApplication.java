package com.cloud.baowang.agent;

import com.cloud.baowang.common.feign.annotations.EnableBwFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * agent对外服务管理系统
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableBwFeignClients
public class AgentForeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentForeignApplication.class, args);
    }

}
