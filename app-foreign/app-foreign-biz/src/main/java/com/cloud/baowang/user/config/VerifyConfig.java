package com.cloud.baowang.user.config;

import com.aliyun.captcha20230305.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author: fangfei
 * @createTime: 2024/09/14 15:07
 * @description: 验证码平台配置
 */
@Configuration
public class VerifyConfig {
    @Value("${accessKeyId}")
    private String accessKeyId;
    @Value("${accessKeySecret}")
    private String accessKeySecret;
    @Value("${endpoint}")
    private String endpoint;

    @Bean("codeConfig")
    public Config getConfig(){
        Config config = new Config();
        // AccessKey ID 和 AccessKey Secret。
        config.accessKeyId = accessKeyId;
        config.accessKeySecret = accessKeySecret;
        config.endpoint = endpoint;
        // 设置连接超时为5000毫秒
        config.connectTimeout = 5000;
        // 设置读超时为5000毫秒
        config.readTimeout = 5000;

        return config;
    }

    @Bean("codeClient")
    public Client getClient(Config codeConfig){
        try {
            return new Client(codeConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
