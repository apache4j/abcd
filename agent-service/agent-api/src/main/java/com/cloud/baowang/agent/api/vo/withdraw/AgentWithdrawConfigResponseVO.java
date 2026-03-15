package com.cloud.baowang.agent.api.vo.withdraw;


import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="代理端提款配置信息")
@I18nClass
public class AgentWithdrawConfigResponseVO {

    @Schema(description = "提款方式ID")
    private String withdrawWayId;

    @Schema(description = "余额")
    private BigDecimal balance;

    @Schema(description = "平台币汇率")
    private BigDecimal platformExchangeRate;

    @Schema(description = "主货币汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "提款最小值")
    private BigDecimal withdrawMinAmount;

    @Schema(description = "提款最大值")
    private BigDecimal withdrawMaxAmount;

    @Schema(title = "手续费率类型")
    @NotNull(message = "手续费率类型 0百分比 1固定金额")
    private Integer feeType;

    @Schema(description = "百分比手续费率/固定金额")
    private BigDecimal feeRate;

    @Schema(description = "单日剩余免费提款次数")
    private Integer singleDayRemindWithdrawCount;

    @Schema(description = "单日剩余免费提款总额")
    private BigDecimal singleDayRemindMaxWithdrawAmount;

    @Schema(description = "银行列表")
    private List<AgentBankManageVO> bankList;

    @Schema(description = "信息收集 bankName银行名称,bankCode 银行代码, bankCard银行卡号,userName姓名," +
            "userEmail邮箱,userPhone联系电话 ,provinceName省份,cityName城市,detailAddress详细地址," +
            "userAccount电子账户,networkType链网络类型,addressNo收款地址")
    private List<WithdrawCollectInfoVO> collectInfoVOS;

    @Schema(description = "上一次提款成功的信息")
    private AgentLastWithdrawInfoVO lastWithdrawInfoVO;


    @Schema(description = "是否绑定支付密码 0否 1是 ")
    private Integer isBindPayPassword;

    @Schema(description = "是否绑定谷歌秘钥 0否 1是")
    private Integer isBindGoogleAuthKey;

    /**
     * 网络协议类型 TRC20 ERC20
     */
    @Schema(description = "网络协议类型 TRC20 ERC20")
    private String networkType;


}
