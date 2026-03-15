package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "签到活动历史")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@I18nClass
public class CheckInRewardResultVO {


    @Schema(description = "AMOUNT-金额，FREE_WHEEL-免费旋转，SPIN_WHEEL-转盘")
    private String rewardType ;

    @Schema(description = "1-每天奖励，2-全月奖励，3-累计奖励")
    private Integer rewardFrom ;

    @Schema(description = "配置阶梯值")
    private Integer rewardTypeCode ;


    @Schema(description = "2-全月奖励，3-累计奖励")
    private Integer rewardDayCount ;
    @Schema(description = "转盘次数，0表示没有")
    private Integer acquireSpinNum = 0;
    @Schema(description = "免费旋转次数，0表示没有")
    private Integer acquireFreeNum = 0;

    @Schema(description = "赠送金额,0表示没有")
    private BigDecimal acquireAmount = BigDecimal.ZERO;
    /**
     * 免费游戏参数
     */
    private List<CheckInRewardFreeGamePPDTO> freeGamePPDTOList;


}
