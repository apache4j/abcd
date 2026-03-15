package com.cloud.baowang.activity.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.activity.api.enums.ActivityEventStatusEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.enums.task.TaskReceiveStatusEnum;
import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.activity.api.vo.ActivityConfigDetailReq;
import com.cloud.baowang.activity.api.vo.ActivitySpinWheelRespVO;
import com.cloud.baowang.activity.api.vo.ToActivityVO;
import com.cloud.baowang.activity.api.vo.task.TaskOrderRecordListResVO;
import com.cloud.baowang.activity.po.SiteActivityEventRecordPO;
import com.cloud.baowang.activity.po.SiteTaskOrderRecordPO;
import com.cloud.baowang.activity.repositories.SiteActivityBaseRepository;
import com.cloud.baowang.activity.repositories.SiteTaskOrderRecordRepository;
import com.cloud.baowang.activity.service.base.ActivityBaseContext;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 订单一致性解决
 */
@AllArgsConstructor
@Slf4j
@Service
public class OrderConsistencyService {

    private final SiteActivityBaseRepository siteActivityBaseRepository;

    private final SiteTaskOrderRecordService taskOrderRecordService;

    private final SiteTaskOrderRecordRepository taskOrderRecordRepository;

    private final UserCoinRecordApi userCoinRecordApi;


    private final SiteApi siteApi;


    public void processTaskRecord() {
        // 查询所有的站点
        ResponseVO<List<SiteVO>> listResponseVO = siteApi.siteInfoAllstauts();
        if (!listResponseVO.isOk()) {
            log.error("获取站点信息错误");
            return;
        }
        for (SiteVO siteVO : listResponseVO.getData()) {
            String siteCode = siteVO.getSiteCode();
            List<String> taskOrders = taskOrderRecordRepository.noReceivedList(siteCode);
            // 查询
            if (CollectionUtil.isEmpty(taskOrders)) {
                continue;
            }
            List<String> coinRecords = userCoinRecordApi.getOrderNoByOrders(taskOrders);
            // 存在说明已经发生了帐变
            // 更新对应的帐变 是否添加打码量
            if(CollectionUtil.isEmpty(coinRecords)){
                continue;
            }
           /* LambdaUpdateWrapper<SiteTaskOrderRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(SiteTaskOrderRecordPO::getOrderNo,coinRecords);
            updateWrapper.set(SiteTaskOrderRecordPO::getReceiveStatus, TaskReceiveStatusEnum.CLAIMED.getCode());
            updateWrapper.set(SiteTaskOrderRecordPO::getReceiveTime,System.currentTimeMillis());
            taskOrderRecordRepository.update(null,updateWrapper);*/
            // 发送打码量



        }
        // 查询未领取的状态，且未过期的任务记录。

    }


}
