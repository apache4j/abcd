package com.cloud.baowang.activity.api.vo.v2;

import cn.hutool.core.collection.CollUtil;
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
public class ActivityAssignDayV2VO extends ActivityBaseV2VO implements Serializable {

    @Schema(description = "指定日期存款")
    @NotNull(message = "指定存款日期不能为空")
    private String weekDays;

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

    /**
     * 游戏大类
     * system_param "venue_type"
     */
    @Schema(description = "游戏大类,可多选,使用逗号隔开")
    private String venueType;

    @Schema(description = "对应选择了游戏大类")
    private List<ActivityAssignDayVenueV2VO> activityAssignDayVenueVOS;


    public boolean validate() {

        List<ActivityAssignDayVenueV2VO> list = this.getActivityAssignDayVenueVOS();

        if (CollUtil.isEmpty(list)) {
            log.error("activityAssignDayVenueVOS 数据为空");
            return false;
        }

        for (ActivityAssignDayVenueV2VO activityAssignDayVenueV2VO : list) {
            Integer discountType = activityAssignDayVenueV2VO.getDiscountType();

            if (Objects.equals(ActivityDiscountTypeEnum.PERCENTAGE.getType(), discountType)) {
                return percentCondVOProcess(activityAssignDayVenueV2VO.getPercentCondVO());
            } else {
                return fixCondVOListProcess(activityAssignDayVenueV2VO.getFixCondVOList());
            }
        }

        return true;
    }

    @Schema(description = "优惠方式类型，0.百分比，1.固定")
    @NotNull(message = "优惠方式类型不能为空")
    @Min(value = 0, message = "优惠方式类型不能小于0")
    @Max(value = 1, message = "优惠方式类型不能大于1")
    private Integer discountType;
    private BigDecimal washRatio;
    private String platformOrFiatCurrency;

    @Schema(description = "匹配条件 优惠方式=百分比时 ")
    private List<AssignDayCondV2VO> percentCondVO;

    @Schema(description = "匹配条件 优惠方式=固定金额时")
    private List<ActivityAssignDayCondV2VO> fixCondVOList;


    public Boolean fixCondVOListProcess(List<ActivityAssignDayCondV2VO> fixCondVOList) {

        if (CollectionUtils.isEmpty(fixCondVOList)) {
            log.info("固定金额,匹配条件不能为空");
            return false;
        }

        for (ActivityAssignDayCondV2VO assignDayCondV2VO : fixCondVOList) {

            List<AssignDayCondV2VO> amountList = assignDayCondV2VO.getAmount();
            BigDecimal beforeAmountMax = null;
            for (AssignDayCondV2VO dayCondV2VO : amountList) {

                if (dayCondV2VO.getMinDepositAmt() == null) {
                    log.info("固定金额,累计金额最小值为空");
                    return false;
                }
                if (dayCondV2VO.getMaxDepositAmt() == null) {
                    log.info("固定金额,累计金额最大值为空");
                    return false;
                }
                /*if (dayCondV2VO.getAcquireNum() == null) {
                    log.info("固定金额,赠送数量为空");
                    return false;
                }*/
                if (dayCondV2VO.getAcquireAmount() == null) {
                    log.info("固定金额,赠送金额为空");
                    return false;
                }

                if (dayCondV2VO.getMaxDepositAmt().compareTo(dayCondV2VO.getMinDepositAmt()) <= 0) {
                    log.info("固定金额,累计金额最大值小于累计金额最小值");
                    return false;
                }
                if (beforeAmountMax != null && beforeAmountMax.compareTo(dayCondV2VO.getMinDepositAmt()) >= 0) {
                    log.info("上一行的最大充值金额大于这一行的最小充值金额");
                    return false;
                }
                beforeAmountMax = dayCondV2VO.getMaxDepositAmt();
            }
        }

        return true;
    }

    public Boolean percentCondVOProcess(List<AssignDayCondV2VO> percentCondVOList) {

        if (CollUtil.isEmpty(percentCondVOList)) {
            log.info("百分比时参数不能为空");
            return false;
        }
        for (AssignDayCondV2VO condVO : percentCondVOList) {
            if (condVO.getMinDepositAmt() == null) {
                log.info("百分比,累计金额最小值为空");
                return false;
            }
            if (condVO.getDepositPercent() == null) {
                log.info("百分比,优惠金额为空");
                return false;
            }
            if (condVO.getDepositPercent().compareTo(new BigDecimal("0.01")) < 0) {
                log.info("优惠金额百分比不能小于0.01");
                return false;
            }
            if (condVO.getDepositPercent().compareTo(new BigDecimal("500")) > 0) {
                log.info("优惠金额百分比不能大于500");
                return false;
            }
            /*if (condVO.getAcquireNum() == null) {
                log.info("百分比,赠送数量为空");
                return false;
            }*/
            if (condVO.getAcquireAmountMax() == null) {
                log.info("百分比,赠送金额最大值为空");
                return false;
            }
        }
        return true;
    }
}
