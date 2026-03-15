package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @className: ActivityAssignDayVenueVO
 * @author: wade
 * @description: 游戏大类配置百分比
 * @date: 5/4/25 09:10
 */
@Schema(description = "保存数据库游戏大类配置百分比 指定存款日期匹配条件 百分比时传递 minDepositAmt,maxDepositAmt,acquireNum,acquireAmount")
@Data
@Slf4j
public class ActivityAssignDayVenueDTO {

    /**
     * 游戏大类
     * system_param "venue_type"
     */
    @Schema(description = "游戏大类,可多选,使用逗号隔开")
    private String venueType;

    /**
     * {@link ActivityDiscountTypeEnum}
     */
    @Schema(description = "优惠方式 0:百分比 1:固定金额")
    @NotNull(message = "优惠方式不能为空")
    private Integer discountType;

    /**
     * 洗码倍率
     */
    @Schema(title = "洗码倍率")
    private BigDecimal washRatio;

    @Schema(description = "匹配条件 优惠方式=百分比时 ")
    private ActivityAssignDayCondVO percentCondVO;

    @Schema(description = "匹配条件 优惠方式=固定金额时")
    private List<ActivityAssignDayCondVO> fixCondVOList;

    @Schema(description = "活动规则-多语言")
    //@I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityRuleI18nCode;

    /*@Schema(description = "活动规则-多语言")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> activityRuleI18nCodeList;*/


    /**
     * 把固定金额和百分比的条件合并成一个
     */
    /*public List<ActivityAssignDayCondVO> getActivityAssignDayCondVOList() {
        List<ActivityAssignDayCondVO> list = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(this.getFixCondVOList())) {
            list.addAll(this.getFixCondVOList());
        }
        if (this.getPercentCondVO() != null) {
            list.add(this.getPercentCondVO());
        }
        return list;
    }*/

    public boolean validate() {

        if (Objects.equals(ActivityDiscountTypeEnum.PERCENTAGE.getType(), discountType)) {
            if (percentCondVO == null) {
                log.info("百分比时参数不能为空");
                return false;
            }
            if (percentCondVO.getMinDepositAmt() == null) {
                log.info("百分比,累计金额最小值为空");
                return false;
            }
            if (percentCondVO.getDepositPercent() == null) {
                log.info("百分比,优惠金额为空");
                return false;
            }
            if (percentCondVO.getDepositPercent().compareTo(new BigDecimal("0.01")) < 0) {
                log.info("优惠金额百分比不能小于0.01");
                return false;
            }
            if (percentCondVO.getDepositPercent().compareTo(new BigDecimal("500")) > 0) {
                log.info("优惠金额百分比不能大于500");
                return false;
            }
            if (percentCondVO.getAcquireNum() == null) {
                log.info("百分比,赠送数量为空");
                return false;
            }
            if (percentCondVO.getAcquireAmountMax() == null) {
                log.info("百分比,赠送金额最大值为空");
                return false;
            }
        } else {
            if (CollectionUtils.isEmpty(fixCondVOList)) {
                log.info("固定金额,匹配条件不能为空");
                return false;
            }
            BigDecimal beforeAmountMax = null;
            for (ActivityAssignDayCondVO activityAssignDayCondVO : fixCondVOList) {
                if (activityAssignDayCondVO.getMinDepositAmt() == null) {
                    log.info("固定金额,累计金额最小值为空");
                    return false;
                }
                if (activityAssignDayCondVO.getMaxDepositAmt() == null) {
                    log.info("固定金额,累计金额最大值为空");
                    return false;
                }
                if (activityAssignDayCondVO.getAcquireNum() == null) {
                    log.info("固定金额,赠送数量为空");
                    return false;
                }
                if (activityAssignDayCondVO.getAcquireAmount() == null) {
                    log.info("固定金额,赠送金额为空");
                    return false;
                }

                if (activityAssignDayCondVO.getMaxDepositAmt().compareTo(activityAssignDayCondVO.getMinDepositAmt()) <= 0) {
                    log.info("固定金额,累计金额最大值小于累计金额最小值");
                    return false;
                }
                if (beforeAmountMax != null && beforeAmountMax.compareTo(activityAssignDayCondVO.getMinDepositAmt()) >= 0) {
                    log.info("上一行的最大充值金额大于这一行的最小充值金额");
                    return false;
                }
                beforeAmountMax = activityAssignDayCondVO.getMaxDepositAmt();
            }
        }
        return true;
    }
}
