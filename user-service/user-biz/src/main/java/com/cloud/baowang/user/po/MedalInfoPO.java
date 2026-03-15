package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 勋章信息表
 * </p>
 *
 * @author ford
 * @since 2024-07-27 03:13:36
 */
@Getter
@Setter
@TableName("medal_info")
public class MedalInfoPO extends BasePO {

    /**
     * 语言代码
     */
    private String languageCode;


    /**
     * 勋章代码
     */
    private String medalCode;

    /**
     * 勋章名称
     */
    private String medalName;

    /**
     * 解锁条件名称
     */
    private String unlockCondName;

    /**
     * 奖励金额
     */
    private BigDecimal rewardAmount;

    /**
     * 打码倍数
     */
    private BigDecimal typingMultiple;

    /**
     * 达成条件1 N
     */
    private String condNum1;


    /**
     * 达成条件2 N
     */
    private String condNum2;

    /**
     * 达成条件1 标签名
     */
    private String condLabel1;


    /**
     * 达成条件2 标签名
     */
    private String condLabel2;


    /**
     * 解锁条件说明
     */
    private String medalDesc;

    /**
     * 激活图片
     */
    private String activatedPic;

    /**
     * 未激活图片
     */
    private String inactivatedPic;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

    /**
     * 排列顺序
     */
    private Integer sortOrder;

}
