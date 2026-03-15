package com.cloud.baowang.user.config;

import com.cloud.baowang.user.properties.AliCloudAuthConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @className: AliCloudAuthConfig
 * @author: wade
 * @description: 阿里云服务客户端
 * @date: 18/9/25 13:18
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(AliCloudAuthConfigProperties.class)
public class AliCloudAuthConfig {

    private final static String ENDPOINT = "cloudauth-intl.cn-hongkong.aliyuncs.com";

    private final AliCloudAuthConfigProperties properties;

    public AliCloudAuthConfig(AliCloudAuthConfigProperties properties) {
        this.properties = properties;
    }

    /**
     * <b>description</b> :
     * <p>Initialize the Client with the credentials</p>
     *
     * @return Client
     */
    @Bean
    @Primary
    public com.aliyun.cloudauth_intl20220809.Client createClient() {
        try {
            com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                    .setAccessKeyId(properties.getAccessKeyId())  // 必须用 setAccessKeyId
                    .setAccessKeySecret(properties.getAccessKeySecret()); // 必须用 setAccessKeySecret
            config.endpoint = ENDPOINT; // 国际版 endpoint

            return new com.aliyun.cloudauth_intl20220809.Client(config);
        } catch (Exception e) {
            log.error("createClient error", e);
            return null;
        }

    }

}
