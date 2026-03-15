package com.cloud.baowang.admin.controller.base;


import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.redis.config.RedisUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ExportBaseController {
    private static final String EXPORT_REDIS_UNIQUE_KEY = "tableExport:centerControl:%s:%s:%s";

    /**
     * 校验导出频率
     *
     * @param uniqueMark 导出唯一标识
     */
    public static void checkExportFrequency(String uniqueMark) {
        String oneId = CurrReqUtils.getOneId();
        String siteCode = CurrReqUtils.getSiteCode();
        String uniqueKey = String.format(EXPORT_REDIS_UNIQUE_KEY, uniqueMark, siteCode, oneId);
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn(remain + "秒后才能导出下载");
            //return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
    }
}
