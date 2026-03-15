package com.cloud.baowang.wallet.api.vo.userCoinRecord;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author qiqi
 */
@Data
@Schema(title ="会员平台币账变记录请求对象")
public class UserPlatformCoinRecordRequestVO extends PageVO {

    @Schema(description="站点编码",hidden = true)
    private String siteCode;

    @Schema(description="账变开始时间")
    private Long coinRecordStartTime;

    @Schema(description="账变结束时间")
    private Long coinRecordEndTime;

    @Schema(description="订单号")
    private String orderNo;

    @Schema(description="币种")
    private String currencyCode;


    @Schema(description="会员账号")
    private String userAccount;

    @Schema(description="代理账号")
    private String agentAccount;

    @Schema(description="账号状态 字典类型code:account_status")
    private String accountStatus;

    @Schema(description = "账号类型 字典类型code: account_type")
    private String accountType;

    @Schema(description="风控级别 取风控层级下拉框公共接口")
    private String riskLevelId;

    @Schema(description="VIP段位最小值")
    private String minVipRank;

    @Schema(description="VIP段位最大值")
    private String maxVipRank;

    @Schema(description="VIP等级最小值")
    private String minVipGradeCode;

    @Schema(description="VIP等级最大值")
    private String maxVipGradeCode;


    @Schema(description="业务类型 字典类型code:paltform_business_coin_type")
    private String businessCoinType;

    @Schema(description="业务类型列表,多选")
    private List<String> businessCoinTypeList;

    @Schema(description="账变类型 字典类型code:paltform_coin_type")
    private String coinType;

    @Schema(description="账变类型，多选")
    private List<String> coinTypeList;

    @Schema(description="收支类型 字典类型code:paltform_coin_balance_type")
    private String balanceType;

    @Schema(description="账变金额最小值")
    private BigDecimal minCoinValue;

    @Schema(description="账变金额最大值")
    private BigDecimal maxCoinValue;

    @Schema(description="是否导出 true 是 false 否")
    private Boolean exportFlag = false;
}
