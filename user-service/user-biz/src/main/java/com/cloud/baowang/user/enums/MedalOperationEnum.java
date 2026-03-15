package com.cloud.baowang.user.enums;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.user.po.MedalRewardConfigPO;
import com.cloud.baowang.user.po.SiteMedalInfoPO;

/**
 * 勋章变更枚举
 * @Author: ford
 * @Date 2024-08-16
 * system_param中的 medal_operation
 */
public enum MedalOperationEnum {

    MEDAL_NAME(SiteMedalInfoPO.Fields.medalName,"勋章名称"),
    MEDAL_DESC(SiteMedalInfoPO.Fields.medalDesc,"解锁条件说明"),
    STATUS(SiteMedalInfoPO.Fields.status,"状态"),
    ACTIVATED_PIC(SiteMedalInfoPO.Fields.activatedPic,"勋章图片(已激活)"),
    INACTIVATED_PIC(SiteMedalInfoPO.Fields.inactivatedPic,"勋章图片(未激活)"),
    COND_NUM1(SiteMedalInfoPO.Fields.condNum1,"解锁达成条件1"),
    COND_NUM2(SiteMedalInfoPO.Fields.condNum2,"解锁达成条件2"),
    TYPING_MULTIPLE(SiteMedalInfoPO.Fields.typingMultiple,"打码倍数"),
    REWARD_AMOUNT(SiteMedalInfoPO.Fields.rewardAmount,"奖励金额"),
    UNLOCK_REWARD_NUM(CommonConstant.REWARD_CONFIG.concat(MedalRewardConfigPO.Fields.unlockMedalNum),"奖励配置-解锁勋章数"),
    CONFIG_REWARD_AMOUNT(CommonConstant.REWARD_CONFIG.concat(MedalRewardConfigPO.Fields.rewardAmount),"奖励配置-奖励金额"),
    CONFIG_TYPING_MULTIPLE(CommonConstant.REWARD_CONFIG.concat(MedalRewardConfigPO.Fields.typingMultiple),"奖励配置-打码倍数"),
    ;
    /**
     * 字段代码
     */
    private String fieldCode;
    /**
     * 描述
     */
    private String desc;


    MedalOperationEnum(String fieldCode, String desc) {
        this.fieldCode = fieldCode;
        this.desc = desc;
    }

    public static MedalOperationEnum parseByFieldCode(String fieldCode) {
        for(MedalOperationEnum medalOperationEnum:MedalOperationEnum.values()){
            if(medalOperationEnum.getFieldCode().equals(fieldCode)){
                return medalOperationEnum;
            }
        }
        return null;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public String getDesc() {
        return desc;
    }
}
