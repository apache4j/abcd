package com.cloud.baowang.websocket;


import com.cloud.baowang.common.feign.annotations.EnableBwFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * The type Websocket application.
 *
 * @author DIM archetype <p> 项目启动类
 */
@EnableBwFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class WebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsocketApplication.class, args);
    }
}
