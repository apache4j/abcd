package com.cloud.baowang.common.es.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;



/**
 * @className: ElasticsearchProperties的配置读取
 * @author: wade
 * @description: ElasticsearchProperties的配置读取
 * @date: 2024/3/13 14:59
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
public class ESProperties {
    private List<String> host;
    private String user;
    private String password;

}
