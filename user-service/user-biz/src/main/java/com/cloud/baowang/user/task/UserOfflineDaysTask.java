package com.cloud.baowang.user.task;

import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.service.UserInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
//@EnableScheduling
public class UserOfflineDaysTask {

    private final UserInfoService userInfoService;

    private final SiteApi siteApi;

    // 会员离线天数 每天凌晨0点15分执行
   // @Scheduled(cron = "0 58 19 * * ?")
    public void userOfflineDays() {

        log.info("会员离线天数 每天凌晨0点15分执行-start");
        try {
            ResponseVO<List<SiteVO>> listResponseVO = siteApi.siteInfoAllstauts();
            if (!listResponseVO.isOk()) {
                return;
            }
            long time = System.currentTimeMillis();
            for (SiteVO siteVO : listResponseVO.getData()) {
                boolean flag = TimeZoneUtils.isInZeroToOneHour(time, siteVO.getTimezone());
                if (flag) {
                    log.info("会员离线天数 每个小时15分执行-start {}", siteVO.getSiteCode());
                    userInfoService.updateOfflineDaysTask(siteVO.getSiteCode());
                    log.info("会员离线天数 每个小时15分执行-end {}", siteVO.getSiteCode());
                }

            }
        } catch (Throwable e) {
            log.error("会员离线天数 每个小时15分执行-error(catch到异常)", e);
        }
        log.info("会员离线天数 每个小时15分执行-end");

    }
}
