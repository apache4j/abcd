package com.cloud.baowang.handler;

import com.cloud.baowang.activity.api.api.ActivityBaseApi;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PlayJobHandler {

    @Resource
    PlayVenueInfoApi playVenueInfoApi;

    /**
     * 初始化同步总控场馆的配置信息到站点场馆配置信息
     */
    @XxlJob(value = "initVenueSiteConfig")
    public void initVenueSiteConfig() {
        log.info("初始化同步总控场馆的配置信息到站点场馆配置信息job开始");
        playVenueInfoApi.initVenueSiteConfig();
        log.info("初始化同步总控场馆的配置信息到站点场馆配置信息job结束");
    }

}
