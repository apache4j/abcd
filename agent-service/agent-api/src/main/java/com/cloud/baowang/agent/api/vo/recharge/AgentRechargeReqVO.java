package com.cloud.baowang.agent.api.vo.recharge;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title = "代理存款请求VO")
public class AgentRechargeReqVO {

    @Schema(description = "存款金额" ,required = true)
    private BigDecimal amount;

    /**
     * 充值方式id
     */
    @Schema(description = "方式id")
    private String depositWayId;

     /**
             * 手机区号
     */
    @Schema(description = "手机区号")
    private String areaCode;


    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String telephone;




    /**
     * 存充值名字
     */
    @Schema(description = "存充值名字")
    private String depositName;



    private String agentId;


    private String agentAccount;

    private Integer origin;

    private String applyIp;

    private String deviceType;

    private String deviceName;

    private String applyDomain;

    private String siteCode;

    private String orderNo;

    private String deviceNo;

}
