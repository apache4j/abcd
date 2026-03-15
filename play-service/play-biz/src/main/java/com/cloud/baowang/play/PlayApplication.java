package com.cloud.baowang.play;


import com.cloud.baowang.common.feign.annotations.EnableBwFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.elasticsearch.ElasticsearchRestHealthContributorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * The type play application.
 *
 * @author DIM archetype <p> 项目启动类
 */
@EnableDiscoveryClient
@SpringBootApplication(exclude = ElasticsearchRestHealthContributorAutoConfiguration.class)
@EnableBwFeignClients
public class PlayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlayApplication.class, args);
    }
}
