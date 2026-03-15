package com.cloud.baowang.wallet.api.vo.withdraw;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.wallet.api.vo.bank.BankManageVO;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(title ="提款配置信息")
@I18nClass
public class WithdrawConfigVO {

    @Schema(description = "提款方式ID")
    private String withdrawWayId;

    /**
     * 图标
     */
    @Schema(description = "提款方式图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String wayIcon;

    @Schema(description = "提款方式图标url")
    private String wayIconFileUrl;

    @Schema(description = "余额")
    private BigDecimal balance;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "提款最小值")
    private BigDecimal withdrawMinAmount;

    @Schema(description = "提款最大值")
    private BigDecimal withdrawMaxAmount;


    @Schema(description = "剩余流水")
    private BigDecimal remainingFlow;

    @Schema(description = "费率类型 0百分比 1固定金额")
    private Integer feeType;

    @Schema(description = "百分手续费率/固定金额")
    private BigDecimal feeRate;

    @Schema(description = "是否存在通道")
    private Boolean isExistChannel;

    @Schema(description = "单日剩余免费提款次数")
    private Integer singleDayRemindWithdrawCount;

    @Schema(description = "单日剩余免费提款总额")
    private BigDecimal singleDayRemindMaxWithdrawAmount;

    @Schema(description = "单日剩余提款次数")
    private Integer dayRemindWithdrawCount;

    @Schema(description = "单日剩余提款金额")
    private BigDecimal dayRemindMaxWithdrawAmount;

    @Schema(description = "大额提款标志金额",hidden = true)
    private BigDecimal largeWithdrawMarkAmount;

    @Schema(description = "银行列表")
    private List<BankManageVO> bankList;

    @Schema(description = "信息收集 bankName银行名称,bankCode 银行代码, bankCard银行卡号,userName姓名," +
            "userEmail邮箱,userPhone联系电话 ,provinceName省份,cityName城市,detailAddress详细地址," +
            "userAccount电子账户,networkType链网络类型,addressNo收款地址")
    private List<WithdrawCollectInfoVO> collectInfoVOS;

    @Schema(description = "上一次提款成功的信息")
    private LastWithdrawInfoVO lastWithdrawInfoVO;

    /**
     * 网络协议类型 TRC20 ERC20
     */
    @Schema(description = "网络协议类型 TRC20 ERC20")
    private String networkType;



}
