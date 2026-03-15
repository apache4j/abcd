package com.cloud.baowang.site;


import com.cloud.baowang.common.feign.annotations.EnableBwFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 商务对外接口系统
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableBwFeignClients
public class BusinessForeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessForeignApplication.class, args);
    }

}
