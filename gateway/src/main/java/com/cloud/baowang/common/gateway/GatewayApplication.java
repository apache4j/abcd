package com.cloud.baowang.common.gateway;


import com.cloud.baowang.common.gateway.config.CorsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

/**
 * 网关应用
 */
@EnableDiscoveryClient
@SpringBootApplication
@Import(CorsConfig.class)
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
