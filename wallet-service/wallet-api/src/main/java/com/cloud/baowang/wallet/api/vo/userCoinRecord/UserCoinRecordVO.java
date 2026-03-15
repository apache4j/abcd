package com.cloud.baowang.wallet.api.vo.userCoinRecord;

import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author qiqi
 */
@Schema(title = "会员账变记录返回对象")
@Data
@I18nClass
public class UserCoinRecordVO {

    @Schema(description="id")
    private String id;
    /**
     * 关联订单号
     */
    @Schema(description="关联订单号")
    private String orderNo;

    @Schema(description="会员ID")
    private String userAccount;

    /**
     * 会员ID
     */
    private String userId;

    @Schema(description = "会员注册信息")
    private String userRegister;

    @Schema(description = "币种")
    private String currency;


    /**
     * 代理名称
     */
    @Schema(description="上级代理")
    private String agentName;

    @Schema(description="风控级别名称")
    private String riskControlLevel;

    /**
     * 会员标签Id
     */
    @Schema(description="会员标签Id")
    private String userLabelId;


    /**
     * 会员标签
     */
    @Schema(description="会员标签")
    private String userLabel;


    /**
     * VIP等级
     */
    @Schema(description="VIP等级")
    private Integer vipGradeCode;

    /**
     * VIP等级
     */
    @Schema(description="VIP等级名称")
    private String vipGradeCodeName;

    /**
     * VIP段位段位名称
     */
    @Schema(description="VIP段位段位名称")
    @I18nField
    private String vipRankName;


    @Schema(description="VIP段位")
    private Integer vipRank;


    @Schema(description="业务类型名称")
    private String businessCoinTypeText;

    @Schema(description="账变类型名称")
    private String coinTypeText;

    @Schema(description="收支类型名称")
    private String balanceTypeText;

    /**
     * 账号状态
     */
    @Schema(description="账号状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_STATUS)
    private String accountStatus;


    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定 - Text")
    private String accountStatusText;

    @Schema(description = "账号类型 1测试 2正式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @Schema(title = "账号类型名称")
    private String accountTypeText;


    @Schema(description="风控级别ID")
    private String riskControlLevelId;





    /**
     * 代理ID
     */
    @Schema(description="代理ID")
    private String  agentId;


    /**
     */
    @Schema(description="业务类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.BUSINESS_COIN_TYPE)
    private String businessCoinType;

    /**
     */
    @Schema(description="账变类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COIN_TYPE)
    private String coinType;

    /**
     * 收支类型1收入,2支出 3冻结 4 解冻
     */
    @Schema(description="收支类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COIN_BALANCE_TYPE)
    private String balanceType;

    /**
     * 账变前金额
     */
    @Schema(description="账变前余额")
    private BigDecimal coinFrom;

    /**
     * 金额改变数量
     */
    @Schema(description="账变金额")
    private BigDecimal coinValue;

    /**
     * 账变后金额
     */
    @Schema(description="账变后余额")
    private BigDecimal coinTo;

    @Schema(description="账变时间;导出需要")
    private String createdTimeStr;


    /**
     * 当前金额
     */
    @Schema(description="当前金额")
    private BigDecimal coinAmount;



    /**
     * 备注
     */
    @Schema(description="备注")
    private String remark;


    @Schema(description="备注")
    private String descInfo;

    /**
     * 描述信息，用于存特殊场馆的一些备注
     */
    @Schema(description="备注和描述信息")
    private String remarkAndDescInfo;

    public String getRemarkAndDescInfo(){
        return StringUtils.isEmpty(descInfo) ? remark : descInfo;
    }

    @Schema(description="账变时间")
    private Long createdTime;

  /*  public String getAccountStatusStr(){
        if(!CollectionUtils.isEmpty(this.accountStatusName)) {
            Set<String> set = this.accountStatusName.stream().map(CodeValueVO::getValue).collect(Collectors.toSet());
            return StringUtils.join(set, ",");
        }
        return StringUtils.EMPTY;
    }*/

    public String getCreatedTimeStr(){
        if(!ObjectUtils.isEmpty(this.createdTime)){
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(new Date(this.createdTime));
        }
        return StringUtils.EMPTY;
    }
}
