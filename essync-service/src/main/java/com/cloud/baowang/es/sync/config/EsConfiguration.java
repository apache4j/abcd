package com.cloud.baowang.es.sync.config;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.es.sync.properties.ESProperties;
import lombok.AllArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class EsConfiguration {

    private final ESProperties eSProperties;

    /**
     * 异步客户端
     *
     * @return
     */
    @Bean
    public ElasticsearchAsyncClient elasticsearchAsyncClient() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(eSProperties.getUser(), eSProperties.getPassword()));

        HttpHost[] httpHosts = eSProperties.getHost().stream()
                .map(address -> address.split(CommonConstant.COLON))
                .map(s -> new HttpHost(s[0], Integer.parseInt(s[1]), "http"))
                .toArray(HttpHost[]::new);

        RestClient client = RestClient.builder(httpHosts)
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                                .setMaxConnTotal(200)
                                .setMaxConnPerRoute(20)
                )
                .build();

        ElasticsearchTransport transport = new RestClientTransport(client, new JacksonJsonpMapper());
        return new ElasticsearchAsyncClient(transport);
    }

    /**
     * tong步客户端
     *
     * @return
     */
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(eSProperties.getUser(), eSProperties.getPassword()));

        HttpHost[] httpHosts = eSProperties.getHost().stream()
                .map(address -> address.split(CommonConstant.COLON))
                .map(s -> new HttpHost(s[0], Integer.parseInt(s[1]), "http"))
                .toArray(HttpHost[]::new);

        RestClient client = RestClient.builder(httpHosts)
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                                .setMaxConnTotal(200)
                                .setMaxConnPerRoute(20)
                )
                .build();

        ElasticsearchTransport transport = new RestClientTransport(client, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
