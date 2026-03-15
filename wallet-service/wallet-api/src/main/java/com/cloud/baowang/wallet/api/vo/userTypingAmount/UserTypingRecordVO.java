package com.cloud.baowang.wallet.api.vo.userTypingAmount;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qiqi
 */
@Schema(title = "会员流水变更记录返回对象")
@Data
@I18nClass
public class UserTypingRecordVO {


    /**
     * 关联订单号
     */
    @Schema(description="关联订单号")
    private String orderNo;

    @Schema(description="流水变更时间")
    private Long createdTime;

    @Schema(description="会员账号")
    private String userAccount;

    @Schema(description="账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE )
    private String accountType;

    @Schema(description="账号类型名称")
    private String accountTypeText;


    @Schema(description = "币种")
    private String currency;



    @Schema(description="增减类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TYPING_BALANCE_TYPE)
    private String adjustWay;

    @Schema(description="增减类型名称")
    private String adjustWayText;


    @Schema(description="流水类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TYPING_ADJUST_TYPE)
    private String adjustType;

    @Schema(description="流水类型名称")
    private String adjustTypeText;



    /**
     * 变动前流水金额
     */
    @Schema(description="变动前流水金额")
    private BigDecimal coinFrom;

    /**
     * 变更金额
     */
    @Schema(description="变更金额")
    private BigDecimal coinValue;

    /**
     * 变更后流水金额
     */
    @Schema(description="变更后流水金额")
    private BigDecimal coinTo;

    @Schema(description="变更时间;导出需要")
    private String createdTimeStr;



    /**
     * 备注
     */
    @Schema(description="备注")
    private String remark;


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
