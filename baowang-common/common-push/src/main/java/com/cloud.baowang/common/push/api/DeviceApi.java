package com.cloud.baowang.common.push.api;



import com.cloud.baowang.common.push.bean.device.DeviceGetResult;
import com.cloud.baowang.common.push.bean.device.DeviceSetParam;
import com.cloud.baowang.common.push.bean.device.TagSetParam;
import com.cloud.baowang.common.push.bean.device.DeviceStatusGetResult;
import com.cloud.baowang.common.push.bean.device.TagsGetResult;
import com.cloud.baowang.common.push.bean.device.TagsCountGetResult;
import com.cloud.baowang.common.push.bean.device.TagQuotaGetResult;
import com.cloud.baowang.common.push.bean.device.AliasStatusGetResult;
import com.cloud.baowang.common.push.client.DeviceClient;
import com.cloud.baowang.common.push.codec.ApiErrorDecoder;
import com.cloud.baowang.common.push.enums.Platform;
import feign.Client;
import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DeviceApi {

    private final DeviceClient deviceClient;

    protected DeviceApi(@NonNull DeviceClient deviceClient) {
        this.deviceClient = deviceClient;
    }

    public DeviceStatusGetResult getDeviceStatus() {
        return deviceClient.getDeviceStatus();
    }

    public DeviceGetResult getDevice(String registrationId) {
        return deviceClient.getDevice(registrationId);
    }

    public void setDevice(String registrationId, DeviceSetParam param) {
        deviceClient.setDevice(registrationId, param);
    }

    public TagsGetResult getTags() {
        return deviceClient.getTags();
    }

    public void setTag(String tag, TagSetParam param) {
        deviceClient.setTag(tag, param);
    }

    public void deleteTag(String tag, List<Platform> platforms) {
        deviceClient.deleteTag(tag, platforms);
    }

    public TagsCountGetResult getTagCount(List<String> tags, List<Platform> platforms) {
        return deviceClient.getTagCount(tags, platforms);
    }

    public TagsGetResult getTagStatus(String tag, String registrationId) {
        return deviceClient.getTagStatus(tag, registrationId);
    }

    public TagQuotaGetResult getTagQuota(List<String> tags, List<Platform> platforms) {
        return deviceClient.getTagQuota(tags, platforms);
    }

    public AliasStatusGetResult getAliasStatus(String alias, List<Platform> platforms) {
        return deviceClient.getAliasStatus(alias, platforms);
    }

    public void deleteAlas(String alias, List<Platform> platform) {
        deviceClient.deleteAlas(alias, platform);
    }

    public static class Builder {

        private String host = "https://push.api.engagelab.cc";
        private Client client = new OkHttpClient();
        private String appKey;
        private String masterSecret;
        private Logger.Level loggerLevel = Logger.Level.BASIC;

        public Builder setHost(@NonNull String host) {
            this.host = host;
            return this;
        }

        public Builder setClient(@NonNull Client client) {
            this.client = client;
            return this;
        }

        public Builder setAppKey(@NonNull String appKey) {
            this.appKey = appKey;
            return this;
        }

        public Builder setMasterSecret(@NonNull String masterSecret) {
            this.masterSecret = masterSecret;
            return this;
        }

        public Builder setLoggerLevel(@NonNull Logger.Level loggerLevel) {
            this.loggerLevel = loggerLevel;
            return this;
        }

        public DeviceApi build() {
            DeviceClient deviceClient = Feign.builder()
                    .client(client)
                    .requestInterceptor(new BasicAuthRequestInterceptor(appKey, masterSecret))
                    .encoder(new JacksonEncoder())
                    .decoder(new JacksonDecoder())
                    .errorDecoder(new ApiErrorDecoder())
                    .logger(new Slf4jLogger())
                    .logLevel(loggerLevel)
                    .target(DeviceClient.class, host);
            return new DeviceApi(deviceClient);
        }
    }

}
