package com.cloud.baowang.common.push.api;

import com.cloud.baowang.common.push.bean.push.GroupPushParam;
import com.cloud.baowang.common.push.bean.push.GroupPushResult;
import com.cloud.baowang.common.push.client.GroupPushClient;
import com.cloud.baowang.common.push.codec.ApiErrorDecoder;
import feign.Client;
import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

import lombok.NonNull;

public class GroupPushApi {

    private final GroupPushClient groupPushClient;

    protected GroupPushApi(@NonNull GroupPushClient groupPushClient) {
        this.groupPushClient = groupPushClient;
    }

    public GroupPushResult push(@NonNull GroupPushParam param) {
        return groupPushClient.push(param);
    }

    public static class Builder {

        private Client client = new OkHttpClient();
        /**
         * 新加坡
         * 数据中心	Base URL
         * 新加坡	https://push.api.engagelab.cc
         * 美国弗吉尼亚	https://push-usva.api.engagelab.cc
         * 德国法兰克福	https://push-defra.api.engagelab.cc
         * 中国香港	https://push-hk.api.engagelab.cc
         */
        private String host = "https://push.api.engagelab.cc";
        private String groupAppKey;
        private String groupMasterSecret;
        private Logger.Level loggerLevel = Logger.Level.BASIC;

        public Builder setHost(@NonNull String host) {
            this.host = host;
            return this;
        }

        public Builder setClient(@NonNull Client client) {
            this.client = client;
            return this;
        }

        public Builder setAppKey(@NonNull String groupAppKey) {
            this.groupAppKey = groupAppKey;
            return this;
        }

        public Builder setMasterSecret(@NonNull String groupMasterSecret) {
            this.groupMasterSecret = groupMasterSecret;
            return this;
        }

        public Builder setLoggerLevel(@NonNull Logger.Level loggerLevel) {
            this.loggerLevel = loggerLevel;
            return this;
        }

        public GroupPushApi build() {
            GroupPushClient groupPushClient = Feign.builder()
                    .client(client)
                    .requestInterceptor(new BasicAuthRequestInterceptor("group-" + groupAppKey, groupMasterSecret))
                    .encoder(new JacksonEncoder())
                    .decoder(new JacksonDecoder())
                    .errorDecoder(new ApiErrorDecoder())
                    .logger(new Slf4jLogger())
                    .logLevel(loggerLevel)
                    .target(GroupPushClient.class, host);
            return new GroupPushApi(groupPushClient);
        }
    }

}
