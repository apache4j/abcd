package com.cloud.baowang.es.sync.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @className: ElasticsearchProperties的配置读取
 * @author: wade
 * @description: ElasticsearchProperties的配置读取
 * @date: 2024/3/13 14:59
 */
@Component
@ConfigurationProperties(prefix = "elasticsearch")
@Data
public class ESProperties {
    private List<String> host;
    private int port;
    private String user;
    private String password;

}
