package com.cloud.baowang.agent;


import com.cloud.baowang.common.feign.annotations.EnableBwFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * The type agent application.
 *
 * @author DIM archetype <p> 项目启动类
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableBwFeignClients
public class AgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
    }
}
