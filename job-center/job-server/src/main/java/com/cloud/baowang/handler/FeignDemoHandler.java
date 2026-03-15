package com.cloud.baowang.handler;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/3/13 11:21
 * @Version: V1.0
 **/
@Component
@Slf4j
public class FeignDemoHandler {
    @Resource
    I18nApi i18nApi;

    /**
     * 测试使用 可删除
     */
    @XxlJob(value = "feignDemoJob")
    public void doExecute() {
        log.info("测试Feign Job开始......");
        XxlJobHelper.log("-----------开始翻译-----------");
        ResponseVO<String> responseVO = i18nApi.getMessage("aa", "add", "en");
        XxlJobHelper.log("-----------翻译结果:{}-----------", responseVO);
        log.info("测试Feign Job翻译结果:{}", responseVO);
    }
}
