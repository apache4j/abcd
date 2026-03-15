package com.cloud.baowang.es.sync;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.elasticsearch.ElasticsearchRestHealthContributorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 同步es服务
 */
@EnableDiscoveryClient
@SpringBootApplication(exclude = {ElasticsearchClientAutoConfiguration.class, ElasticsearchRestHealthContributorAutoConfiguration.class})
public class EsSyncServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsSyncServiceApplication.class, args);
    }

}
