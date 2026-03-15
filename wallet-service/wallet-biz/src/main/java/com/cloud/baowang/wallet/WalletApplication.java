package com.cloud.baowang.wallet;


import com.cloud.baowang.common.feign.annotations.EnableBwFeignClients;
import com.github.jesse.l2cache.spring.EnableL2Cache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * The type wallet application.
 *
 * @author DIM archetype <p> 项目启动类
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableBwFeignClients
@EnableL2Cache
@EnableAsync
public class WalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalletApplication.class, args);
    }
}
