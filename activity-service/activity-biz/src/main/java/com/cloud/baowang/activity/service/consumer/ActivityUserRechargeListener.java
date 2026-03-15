package com.cloud.baowang.activity.service.consumer;


import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.enums.DepositTypeEnum;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.service.ActivityFirstRechargeService;
import com.cloud.baowang.activity.service.ActivitySecondRechargeService;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.activity.service.base.activityV2.SiteActivityBaseV2Service;
import com.cloud.baowang.activity.service.v2.ActivityFirstRechargeV2Service;
import com.cloud.baowang.activity.service.v2.ActivitySecondRechargeV2Service;
import com.cloud.baowang.activity.service.v2.SiteActivityNewHandService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeTriggerVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 会员充值 消息监听
 * 首次充值
 * 二次充值
 */
@Slf4j
@Component
@AllArgsConstructor
public class ActivityUserRechargeListener {

    private final SiteActivityBaseService siteActivityBaseService;
    private final SiteActivityBaseV2Service siteActivityBaseV2Service;
    private final ActivityFirstRechargeService firstRechargeService;
    private final ActivityFirstRechargeV2Service firstRechargeV2Service;
    private final SiteActivityNewHandService siteActivityNewHandService;
    private final ActivitySecondRechargeService secondRechargeService;
    private final ActivitySecondRechargeV2Service secondRechargeV2Service;
    private final SiteApi siteApi;


    @KafkaListener(topics = TopicsConstants.MEMBER_RECHARGE, groupId = GroupConstants.MEMBER_RECHARGE_GROUP)
    public void memberRechargeMessage(RechargeTriggerVO triggerVO, Acknowledgment ackItem) {
        log.info("首充，次充接收到会员充值消息:{}", triggerVO);
        try{
            Integer depositType = triggerVO.getDepositType();
            //派发操作 不是申请操作
            triggerVO.setApplyFlag(false);
            log.info("站点:{}会员充值类型:{},充值金额:{}", triggerVO.getSiteCode(), depositType, triggerVO.getRechargeAmount());
            SiteVO siteInfo = siteApi.getSiteInfo(triggerVO.getSiteCode()).getData();
            if(siteInfo==null){
                log.info("站点信息没有配置,无需派发奖励");
                return;
            }
            if (Objects.equals(SiteHandicapModeEnum.China.getCode(), siteInfo.getHandicapMode())){
                //充值次数类型
                if (DepositTypeEnum.FIRST_DEPOSIT.getValue().equals(depositType)) {
                    SiteActivityBaseV2PO siteActivityBasePO = siteActivityBaseV2Service.getSiteActivityBasePO(triggerVO.getSiteCode(), ActivityTemplateV2Enum.FIRST_DEPOSIT_V2.getType());
                    if (siteActivityBasePO == null) {
                        log.info("站点:{} 不存在已生效的首存活动v2", triggerVO.getSiteCode());
                        return;
                    }
                    //当前站点存在首存活动
                    firstRechargeV2Service.validateAndReward(triggerVO, siteActivityBasePO);

                    SiteActivityBaseV2PO siteActivityNewHandBasePO = siteActivityBaseV2Service.getSiteActivityBasePO(triggerVO.getSiteCode(), ActivityTemplateV2Enum.NEW_HAND.getType());
                    if (siteActivityNewHandBasePO == null) {
                        log.info("站点:{} 不存在已生效的新人活动", triggerVO.getSiteCode());
                        return;
                    }
                    triggerVO.setTimezone(siteInfo.getTimezone());
                    siteActivityNewHandService.firstDepositValidateAndReward(triggerVO, siteActivityNewHandBasePO);
                } else if (DepositTypeEnum.SECOND_DEPOSIT.getValue().equals(depositType)) {
                    SiteActivityBaseV2PO siteActivityBasePO = siteActivityBaseV2Service.getSiteActivityBasePO(triggerVO.getSiteCode(), ActivityTemplateV2Enum.SECOND_DEPOSIT_V2.getType());
                    if (siteActivityBasePO == null) {
                        log.info("站点:{}不存在已生效的次存活动v2", triggerVO.getSiteCode());
                        return;
                    }
                    //二次充值
                    secondRechargeV2Service.validateAndReward(triggerVO, siteActivityBasePO);
                }
            }else {
                //充值次数类型
                if (DepositTypeEnum.FIRST_DEPOSIT.getValue().equals(depositType)) {
                    SiteActivityBasePO siteActivityBasePO = siteActivityBaseService.getSiteActivityBasePO(triggerVO.getSiteCode(), ActivityTemplateEnum.FIRST_DEPOSIT.getType());
                    if (siteActivityBasePO == null) {
                        log.info("站点:{}不存在已生效的首存活动", triggerVO.getSiteCode());
                        return;
                    }
                    //当前站点存在首存活动
                    firstRechargeService.validateAndReward(triggerVO, siteActivityBasePO);
                } else if (DepositTypeEnum.SECOND_DEPOSIT.getValue().equals(depositType)) {
                    SiteActivityBasePO siteActivityBasePO = siteActivityBaseService.getSiteActivityBasePO(triggerVO.getSiteCode(), ActivityTemplateEnum.SECOND_DEPOSIT.getType());
                    if (siteActivityBasePO == null) {
                        log.info("站点:{}不存在已生效的次存活动", triggerVO.getSiteCode());
                        return;
                    }
                    //二次充值
                    secondRechargeService.validateAndReward(triggerVO, siteActivityBasePO);
                }
            }
        }catch (Exception e){
            log.error("首充,次充接收到会员充值发生异常:",e);
        }finally {
            if (ackItem != null) {
                ackItem.acknowledge();
            }
        }
    }

}
