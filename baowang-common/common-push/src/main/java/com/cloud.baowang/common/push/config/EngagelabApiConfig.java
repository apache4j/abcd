package com.cloud.baowang.common.push.config;

import com.cloud.baowang.common.push.api.DeviceApi;
import com.cloud.baowang.common.push.api.PushApi;
import com.cloud.baowang.common.push.api.ScheduleApi;
import com.cloud.baowang.common.push.api.StatusApi;
import feign.Logger;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JPushConfigProperties.class)
public class EngagelabApiConfig {

    private final JPushConfigProperties properties;

    // 构造函数注入 JPushConfigProperties
    public EngagelabApiConfig(JPushConfigProperties properties) {
        this.properties = properties;
    }



    @Bean
    public PushApi pushApi() {
        return new PushApi.Builder()
                .setAppKey(properties.getAppKey())
                .setMasterSecret(properties.getMasterSecret())
                .setLoggerLevel(Logger.Level.FULL)
                .build();
    }

    @Bean
    public DeviceApi deviceApi() {
        return new DeviceApi.Builder()
                .setAppKey(properties.getAppKey())
                .setMasterSecret(properties.getMasterSecret())
                .setLoggerLevel(Logger.Level.FULL)
                .build();
    }

    @Bean
    public StatusApi statusApi() {
        return new StatusApi.Builder()
                .setAppKey(properties.getAppKey())
                .setMasterSecret(properties.getMasterSecret())
                .setLoggerLevel(Logger.Level.FULL)
                .build();
    }

    @Bean
    public ScheduleApi scheduleApi() {
        return new ScheduleApi.Builder()
                .setAppKey(properties.getAppKey())
                .setMasterSecret(properties.getMasterSecret())
                .setLoggerLevel(Logger.Level.FULL)
                .build();
    }

    // sdk use feign-okhttp default
    // okhttp config doc：https://square.github.io/okhttp/5.x/okhttp/okhttp3/-ok-http-client/-builder/index.html
    /*@Bean("okHttpClientPush")
    public OkHttpClient okHttpClientPush() {
        okhttp3.OkHttpClient okHttpClient = new okhttp3.OkHttpClient().newBuilder()
                // .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy_host", proxy_port))) // set proxy
                .connectTimeout(5, TimeUnit.SECONDS) // set connect timeout
                .build();
        return new OkHttpClient(okHttpClient);
    }*/

    /*@Bean
    public GroupPushApi groupPushApi( OkHttpClient okHttpClientPush) {
        return new GroupPushApi.Builder()
                .setClient(okHttpClientPush)
                .setAppKey(properties.getAppKey())
                .setMasterSecret(properties.getMasterSecret())
                .setLoggerLevel(Logger.Level.FULL)
                .build();
    }*/

}
