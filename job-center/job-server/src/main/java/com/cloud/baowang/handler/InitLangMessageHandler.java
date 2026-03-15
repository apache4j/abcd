package com.cloud.baowang.handler;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author : kimi
 * @Date : 2024-06-19
 */
@Slf4j
@Component
public class InitLangMessageHandler {

    @Resource
    private I18nApi i18nApi;


    /**
     * 会员报表
     */
    @XxlJob(value = "initI18nMessagesForLang")
    public void addUserInfoStatement() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("根据指定语言初始化国际化消息jobParam:{}", JSONObject.toJSONString(jobParam));
        XxlJobHelper.log("----------- 根据指定语言初始化国际化消息job 结束统计-----------{}", JSONObject.toJSONString(jobParam));
        i18nApi.initI18nMessagesForLang(jobParam);
        XxlJobHelper.log("----------- 根据指定语言初始化国际化消息job 结束统计-----------");

    }


}
