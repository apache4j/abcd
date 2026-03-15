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
@TableName("site_user_invite_record")
public class SiteUserInviteRecordPO extends BasePO implements Serializable {
    //siteCode
    private String siteCode;
    //邀请人账号
    private String userAccount;
    //用户id
    private String userId;
    //邀请码
    private String inviteCode;
    //被邀请会员账号
    private String targetAccount;
    //被邀请会员id
    private String targetUserId;
    //币种
    private String currency;
    //注册时间
    private Long registerTime;

    //首存达标
    private int validFirstDeposit;
    //累计存款达标
    private int validTotalDeposit;

    //首存时间
    private Long firstDepositTime;
    //首存金额
    private BigDecimal firstDepositAmount;
    //累计存款金额
    private BigDecimal depositAmountTotal;

}
