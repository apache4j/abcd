package com.cloud.baowang.activity.api.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 */
@Data
@Schema(title = "查询转盘活动列表入参-app")
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySpinWheelAppReqVO  extends UserBaseReqVO implements Serializable{

    /**
     * id
     */
    @Schema(title = "主键id")
    private String id;
    /**
     * 站点code
     */
   /* @Schema(title = "站点code", hidden = true)
    private String siteCode;*/


    /**
     * 会员id
     */
    /*@Schema(description = "会员 id", hidden = true)
    private String userId;*/

    /**
     * 会员id
     */
   /* @Schema(description = "会员 id", hidden = true)
    private String userAccount;*/


    /**
     * 抽奖的段位，会员抽奖的段位奖项配置
     */
    @Schema(description = "抽奖的段位，会员抽奖的段位奖项配置")
    private Integer vipRankCode;



}