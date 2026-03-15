package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("site_vip_change_record")
public class SiteVipChangeRecordPO extends BasePO implements Serializable {
    /**
     * 用户id,预留
     */
    private String userId;
    /**
     * siteCode
     */
    private String siteCode;
    /**
     * 操作记录类型(0:VIP段位,1:VIP等级)
     */
    private Integer operationType;
    /**
     * 会员账号
     */
    private String userAccount;
    /**
     * 变更类型 变更类型-升级/降级--同system_param vip_level_change_type 类型的code
     */
    private Integer changeType;

    /**
     * 变更前vip等级
     */
    private String beforeChange;
    /**
     * 变更后vip等级
     */
    private String afterChange;
    /**
     * 变更时间
     */
    private Long changeTime;
    /**
     * 操作人
     */
    private String operator;
}
