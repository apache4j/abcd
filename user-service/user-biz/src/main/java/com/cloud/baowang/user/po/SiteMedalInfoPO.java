package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

/**
 * <p>
 * 站点勋章信息表
 * </p>
 *
 * @author ford
 * @since 2024-07-27 03:13:36
 */
@Getter
@Setter
@TableName("site_medal_info")
@FieldNameConstants
public class SiteMedalInfoPO extends BasePO {

    /**
     * 上级勋章id
     */
    private Long parentId;

    /**
     * 站点代码
     */
    private String siteCode;

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
     * 排序
     */
    private Integer sortOrder;

    /**
     * 勋章名称多语言
     */
    private String medalNameI18;
    /**
     * 勋章描述多语言
     */
    private String medalDescI18;

    /**
     * 勋章名称多语言
     */
    private String unlockCondNameI18;


    /**
     * 达成条件1 标签名 多语言
     */
    private String condLabel1I18;


    /**
     * 达成条件2 标签名 多语言
     */
    private String condLabel2I18;


}
