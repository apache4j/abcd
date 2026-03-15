package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/23 22:03
 * @description:
 */
@Data
@TableName("site_user_invite_config")
public class SiteUserInviteConfigPO extends BasePO implements Serializable {
    //siteCode
    private String siteCode;
    //语言
    private String language;
    //首存金额
    private BigDecimal firstDepositAmount;
    //累计存款金额
    private BigDecimal depositAmountTotal;
}
