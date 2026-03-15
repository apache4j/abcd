package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.activity.api.enums.DisCountTypeEnum;
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

/**
 * 周三免费旋转
 */
@Schema(description = "免费旋转新增")
@Data
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ActivityFreeWheelVO extends ActivityBaseVO implements Serializable {

    @Schema(description = "匹配条件 优惠方式=固定次数 时传递")
    ActivityFreeWheelCondVO fixCondVO;
    /**
     * 场馆不能为空
     * system_param "venue_type"
     */
    @Schema(description = "场馆类型")
    @NotNull(message = "场馆不能为空,固定PP")
    private String venueCode;
    /**
     * 游戏大类
     * system_param "venue_type"
     */
    @Schema(description = "pp游戏code")
    @NotNull(message = "pp游戏code不能为空")
    private String accessParameters;
    @Schema(description = "限注金额")
    @NotNull(message = "限注金额不能为空")
    private BigDecimal betLimitAmount;
    @Schema(description = "指定日期存款")
    @NotNull(message = "指定存款日期不能为空")
    private String weekDays;
    /**
     * {@link com.cloud.baowang.activity.api.enums.DisCountTypeEnum}
     */
    @Schema(description = "优惠方式 0:阶梯次数 1:固定次数")
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
    @Schema(description = "匹配条件 优惠方式=阶梯次数 时传递")
    private List<ActivityFreeWheelCondVO> stepCondVOList;


    public boolean validate() {
        if (betLimitAmount == null) {
            log.info("限注金额不能为空");
            return false;
        }
        if (venueCode == null) {
            log.info("场馆不能为空");
            return false;
        }
        if (accessParameters == null) {
            log.info("pp游戏code不能为空");
            return false;
        }
        if (DisCountTypeEnum.FIX.getValue() == discountType) {
            if (fixCondVO == null) {
                log.info("固定次数 不能为空");
                return false;
            }
            if (fixCondVO.getMinDepositAmt() == null) {
                log.info("固定次数时,累计金额最小值为空");
                return false;
            }
            if (fixCondVO.getAcquireNum() == null) {
                log.info("固定次数时,赠送数量为空");
                return false;
            }
        } else {
            if (CollectionUtils.isEmpty(stepCondVOList)) {
                log.info("阶梯次数参数不能为空");
                return false;
            }
            BigDecimal beforeAmountMax = null;
            for (ActivityFreeWheelCondVO activityFreeWheelCondVO : stepCondVOList) {
                if (activityFreeWheelCondVO.getMinDepositAmt() == null) {
                    log.info("阶梯次数时,累计金额最小值为空");
                    return false;
                }

                if (activityFreeWheelCondVO.getMaxDepositAmt() == null) {
                    log.info("阶梯次数时,累计金额最大值为空");
                    return false;
                }

                if (activityFreeWheelCondVO.getMaxDepositAmt().compareTo(activityFreeWheelCondVO.getMinDepositAmt()) <= 0) {
                    log.info("阶梯次数时,累计金额最大值小于累计金额最小值");
                    return false;
                }
                if (activityFreeWheelCondVO.getAcquireNum() == null) {
                    log.info("阶梯次数时,赠送数量为空");
                    return false;
                }
                if (beforeAmountMax != null && beforeAmountMax.compareTo(activityFreeWheelCondVO.getMinDepositAmt()) >= 0) {
                    log.info("上一行的最大充值金额大于这一行的最小充值金额");
                    return false;
                }
                beforeAmountMax = activityFreeWheelCondVO.getMaxDepositAmt();
            }
        }
        return true;
    }
}
