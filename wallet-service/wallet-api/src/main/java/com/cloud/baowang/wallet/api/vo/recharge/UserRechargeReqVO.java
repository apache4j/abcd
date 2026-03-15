package com.cloud.baowang.wallet.api.vo.recharge;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title = "用户存款请求VO")
public class UserRechargeReqVO {

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



    private String userId;


    private String userAccount;

    private Integer origin;

    private String applyIp;

    private String deviceType;

    private String deviceName;

    private String applyDomain;

    private String siteCode;

    private String orderNo;

    private String deviceNo;


    @Schema(description = "事件id")
    private String eventId;


}
