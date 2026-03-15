package com.cloud.baowang.account.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员平台币钱包账变记录对象
 *
 * @author qiqi
 */
@Data
@TableName("user_platform_coin_record")
public class UserPlatformCoinRecordPO extends BasePO {

    /**
     * 站点CODE
     */
    private String siteCode;

    /**
     * 会员名称
     */
    private String userName;

    /**
     * 会员账号
     */
    private String userAccount;
    /**
     * 会员ID
     */
    private String userId;

    /**
     * 账号状态
     */
    private String accountStatus;

    /**
     * 账号类型1测试 2正式
     */
    private String accountType;

    /**
     * UserTypingAmountRecordPOIDS
     */
    private String userLabelId;


    /**
     * 风控级别Id
     */
    private String riskControlLevelId;



    /**
     * VIP段位
     */
    private Integer vipRank;

    /**
     * VIP等级
     */
    private Integer vipGradeCode;


    /**
     * 代理ID
     */
    private String  agentId;

    /**
     * 代理名称
     */
    private String agentName;


    /**
     * 币种
     */
    private String currency;

    /**
     * 关联订单号
     */
    private String orderNo;


    /**
     * 业务类型
     */
    private String businessCoinType;

    /**
     *账变类型
     */
    private String coinType;

    /**
     * 客户端账变类型
     */
    private String customerCoinType;

    /**
     * 收支类型1收入,2支出 3冻结 4 解冻
     */
    private String balanceType;

    /**
     * 账变金额
     */
    private BigDecimal coinValue;

    /**
     * 账变前金额
     */
    private BigDecimal coinFrom;

    /**
     * 账变后金额
     */
    private BigDecimal coinTo;

    /**
     * 当前金额
     */
    private BigDecimal coinAmount;



    /**
     * 备注
     */
    private String remark;



}

