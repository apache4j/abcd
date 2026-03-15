package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 勋章数量奖励配置
 * </p>
 *
 * @author ford
 * @since 2024-07-27 03:13:58
 */
@Getter
@Setter
@TableName("medal_reward_config")
@FieldNameConstants
public class MedalRewardConfigPO extends BasePO {

    /**
     * 激励编号  固定值:1,2,3,4,5
     */
    private Integer rewardNo;
    /**
     * 站点代码
     */
    private String siteCode;

    /**
     * 解锁勋章数
     */
    private Integer unlockMedalNum;

    /**
     * 奖励金额
     */
    private BigDecimal rewardAmount;

    /**
     * 打码倍数
     */
    private BigDecimal typingMultiple;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

}
