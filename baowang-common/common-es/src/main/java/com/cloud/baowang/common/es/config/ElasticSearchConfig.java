package com.cloud.baowang.common.es.config;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.es.properties.ESProperties;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Author: sheldon
 * @Date: 3/20/24 10:58 上午
 */
@Configuration
public class ElasticSearchConfig {

    private ESProperties eSProperties;
    public ElasticSearchConfig(ESProperties eSProperties){
        this.eSProperties = eSProperties;
    }
    /**
     * 如果@Bean没有指定bean的名称，那么这个bean的名称就是方法名
     */
    @Bean
    public RestHighLevelClient restHighLevelClient() {
//        return new RestHighLevelClient(
//                RestClient.builder(
//                        new HttpHost(host, port, "http")
//                )
//        );



        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(eSProperties.getUser(), eSProperties.getPassword()));
        List<String> host = eSProperties.getHost();
        HttpHost[] httpHosts = host.stream()
                .map(address -> address.split(CommonConstant.COLON))
                .map(s -> new HttpHost(s[0], Integer.parseInt(s[1]), "http"))
                .toArray(HttpHost[]::new);

        return new RestHighLevelClient(RestClient.builder(httpHosts)
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)));

    }

}