package com.cloud.baowang.common.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "encrypt")
public class EncryptProperties {

    private boolean enable;

    private Aes aes;

    @Data
    public static class Aes {
        /**
         * 密钥
         */
        private String secretKey;
        /**
         * 偏移量
         */
        private String iv;
    }
}
