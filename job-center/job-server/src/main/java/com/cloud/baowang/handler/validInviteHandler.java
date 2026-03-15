package com.cloud.baowang.handler;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.system.api.api.redissonOperate.RedissonOperateApi;
import com.cloud.baowang.user.api.api.SiteUserInviteRecordApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class validInviteHandler {

    private SiteUserInviteRecordApi siteUserInviteRecordApi;
    /**
     * 兼容有效邀请老数据 {"siteCode":"10002"}
     */
    @XxlJob(value = "validInviteRecoup")
    public void validInviteRecoup() {
        log.info("***************** 有效邀请 老数据兼容handler begin *****************");
        String jobParam = XxlJobHelper.getJobParam();
        if (ObjectUtil.isNotEmpty(jobParam)){
            JSONObject paramJson=new JSONObject();
            paramJson= JSONObject.parseObject(jobParam);
            String siteCode=paramJson.getString("siteCode");
            siteUserInviteRecordApi.validInviteRecoup(siteCode,true);
        }
        log.info("***************** 有效邀请 老数据兼容handler end *****************");
    }
}
