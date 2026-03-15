package com.cloud.baowang.agent.api.vo.withdraw;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title = "会员提现申请请求VO")
public class AgentWithDrawApplyVO {




    @Schema(description = "提现金额" ,required = true)
    private BigDecimal amount;


    /**
     * 取款方式id
     */
    @Schema(description = "取款方式id")
    private String withdrawWayId;


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
    @Schema(description = "电子账户")
    private String userAccount;
    @Schema(description = "网络协议")
    private String networkType;

    @Schema(description = "加密货币收款地址")
    private String addressNo;

    @Schema(description = "IFSC码(印度)")
    private String ifscCode;

    @Schema(description = "CPF")
    private String cpf;

    @Schema(description = "取款密码")
    private String withdrawPassWord;

    @Schema(description = "谷歌验证码")
    private String googleAuthCode;

    private Integer origin;

    private String applyIp;

    private String deviceType;

    private String deviceName;

    private String applyDomain;

    private String agentId;

    private String deviceNo;


}
