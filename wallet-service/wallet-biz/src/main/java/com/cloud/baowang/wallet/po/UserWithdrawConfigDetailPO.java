package com.cloud.baowang.wallet.po;


import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员提款配置信息详情-会员特殊配置
 *
 * @author qiqi
 */
@Data
@TableName("user_withdraw_config_detail")
public class UserWithdrawConfigDetailPO extends BasePO {

    /**
     * 会员id
     */
    private String userId;

    /**
     * 会员账号
     */
    private String userAccount;

    /**
     * 会员段位
     */
    private Integer vipRankCode;

    /**
     * 会员等级
     */
    private Integer vipGradeCode;
    /**
     * 货币代码
     */
    private String currencyCode;

    /**
     * 单日提款次数上限
     */
    private Integer dayWithdrawCount;

    /**
     * 单日提款金额上限
     */
    private BigDecimal maxWithdrawAmount;

    /**
     * 单日提款免费次数
     */
    private Integer singleDayWithdrawCount;

    /**
     * 单日提款免费金额
     */
    private BigDecimal singleMaxWithdrawAmount;


    /**
     * 站点编码
     */
    private String siteCode;

}
