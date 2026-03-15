package com.cloud.baowang.agent.api.vo.withdraw;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="上一次提款成的提款信息")
public class AgentLastWithdrawInfoVO {

    @Schema(description = "银行名称")
    private String bankName;

    @Schema(description = "银行编码")
    private String bankCode;

    @Schema(description = "银行卡号")
    private String bankCard;

    @Schema(description = "姓")
    private String surname;

    @Schema(description = "名")
    private String userName;

    @Schema(description = "邮箱")
    private String userEmail;

    @Schema(description = "手机区号")
    private String areaCode;

    @Schema(description = "手机号")
    private String userPhone;

    @Schema(description = "省")
    private String provinceName;

    @Schema(description = "市")
    private String cityName;
    @Schema(description = "详细地址")
    private String detailAddress;

    @Schema(description = "IFSC码(印度)")
    private String ifscCode;

    @Schema(description = "CPF")
    private String cpf;

    @Schema(description = "电子账户")
    private String userAccount;
    @Schema(description = "网络协议")
    private String networkType;

    @Schema(description = "加密货币收款地址")
    private String addressNo;

}
