package com.cloud.baowang.play.wallet;

import com.cloud.baowang.common.feign.annotations.EnableBwFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@EnableBwFeignClients
public class PlayWalletApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlayWalletApiApplication.class, args);
    }

}
