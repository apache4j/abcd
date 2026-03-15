package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 周三免费旋转
 */
@Schema(description = "指定存款日期新增")
@Data
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ActivityAssignDayVO extends ActivityBaseVO implements Serializable {

    @Schema(description = "指定日期存款")
    @NotNull(message = "指定存款日期不能为空")
    private String weekDays;

    /**
     * {@link ActivityDiscountTypeEnum}
     */
    @Schema(description = "优惠方式 0:百分比 1:固定金额")
    @NotNull(message = "优惠方式不能为空")
    private Integer discountType;

    /**
     * 参与方式,0 手动参与 1 自动参与
     * {@link com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum}
     */
    @Schema(description = "参与方式,0 手动参与 1 自动参与")
    @NotNull(message = "参与方式不能为空")
    @Min(value = 0, message = "参与方式不能小于0")
    @Max(value = 1, message = "参与方式不能大于1")
    private Integer participationMode;


    /**
     * 派发方式,0.玩家自领-过期作废，1.玩家自领-过期作废 2.立即派发
     * {@link com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum}
     */
    @Schema(description = "派发方式,0.玩家自领-过期作废，1.玩家自领-过期作废 2.立即派发")
    @NotNull(message = "派发方式不能为空")
    @Min(value = 0, message = "派发方式不能小于0")
    @Max(value = 1, message = "派发方式不能大于1")
    private Integer distributionType;

    @Schema(description = "匹配条件 优惠方式=百分比时 " )
    private  ActivityAssignDayCondVO percentCondVO;

    @Schema(description = "匹配条件 优惠方式=固定金额时")
    private List<ActivityAssignDayCondVO> fixCondVOList;
    /**
     * 游戏大类
     * system_param "venue_type"
     */
    @Schema(description = "游戏大类,可多选,使用逗号隔开")
    private String venueType;

    @Schema(description = "对应选择了游戏大类")
    private List<ActivityAssignDayVenueVO> activityAssignDayVenueVOS;

    /**
     * 场馆类型
     * system_param "venue_type"
     */
    @Schema(description = "场馆类型")
    @NotNull(message = "场馆不能为空,固定PP")
    private String venueCode;
    @Schema(description = "pp游戏code")
    @NotNull(message = "pp游戏code不能为空")
    private String accessParameters;
    @Schema(description = "限注金额")
    @NotNull(message = "限注金额不能为空")
    private BigDecimal betLimitAmount;


    public boolean validate() {

        if(Objects.equals(ActivityDiscountTypeEnum.PERCENTAGE.getType(), discountType)){
            if(percentCondVO==null){
                log.info("百分比时参数不能为空");
                return false;
            }
            if(percentCondVO.getMinDepositAmt()==null){
                log.info("百分比,累计金额最小值为空");
                return false;
            }
            if(percentCondVO.getDepositPercent()==null){
                log.info("百分比,优惠金额为空");
                return false;
            }
            if(percentCondVO.getDepositPercent().compareTo(new BigDecimal("0.01"))<0){
                log.info("优惠金额百分比不能小于0.01");
                return false;
            }
            if(percentCondVO.getDepositPercent().compareTo(new BigDecimal("500"))>0){
                log.info("优惠金额百分比不能大于500");
                return false;
            }
            if(percentCondVO.getAcquireNum()==null){
                log.info("百分比,赠送数量为空");
                return false;
            }
            if(percentCondVO.getAcquireAmountMax()==null){
                log.info("百分比,赠送金额最大值为空");
                return false;
            }
        }else {
            if(CollectionUtils.isEmpty(fixCondVOList)){
                log.info("固定金额,匹配条件不能为空");
                return false;
            }
            BigDecimal beforeAmountMax=null;
            for (ActivityAssignDayCondVO activityAssignDayCondVO:fixCondVOList){
                if(activityAssignDayCondVO.getMinDepositAmt()==null){
                    log.info("固定金额,累计金额最小值为空");
                    return false;
                }
                if(activityAssignDayCondVO.getMaxDepositAmt()==null){
                    log.info("固定金额,累计金额最大值为空");
                    return false;
                }
                if(activityAssignDayCondVO.getAcquireNum()==null){
                    log.info("固定金额,赠送数量为空");
                    return false;
                }
                if(activityAssignDayCondVO.getAcquireAmount()==null){
                    log.info("固定金额,赠送金额为空");
                    return false;
                }

                if(activityAssignDayCondVO.getMaxDepositAmt().compareTo(activityAssignDayCondVO.getMinDepositAmt())<=0){
                    log.info("固定金额,累计金额最大值小于累计金额最小值");
                    return false;
                }
                if(beforeAmountMax!=null && beforeAmountMax.compareTo(activityAssignDayCondVO.getMinDepositAmt())>=0){
                    log.info("上一行的最大充值金额大于这一行的最小充值金额");
                    return false;
                }
                beforeAmountMax=activityAssignDayCondVO.getMaxDepositAmt();
            }
        }
        return true;
    }
}
