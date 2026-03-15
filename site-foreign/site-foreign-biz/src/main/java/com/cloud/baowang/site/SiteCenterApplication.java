package com.cloud.baowang.site;


import com.cloud.baowang.common.feign.annotations.EnableBwFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 商户后台管理系统
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableBwFeignClients
public class SiteCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SiteCenterApplication.class, args);
    }

}
