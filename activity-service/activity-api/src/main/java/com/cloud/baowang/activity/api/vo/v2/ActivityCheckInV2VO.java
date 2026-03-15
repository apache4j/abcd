package com.cloud.baowang.activity.api.vo.v2;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.activity.api.vo.ActivityBaseVO;
import com.cloud.baowang.activity.api.vo.CheckInRewardConfigVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 签到详情实体
 */
@Data
@I18nClass
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class ActivityCheckInV2VO extends ActivityBaseVO implements Serializable {

    @Schema(description = "前端忽略该字段", hidden = true)
    private String baseId;

    /**
     * 存款金额
     */
    @Schema(description = "存款金额")
    private BigDecimal depositAmount;

    /**
     * 有效投注金额
     */
    @Schema(description = "有效投注金额")
    private BigDecimal betAmount;

    /**
     * 周奖励配置
     */
    @Schema(description = "周奖励配置")
    private List<CheckInRewardConfigVO> rewardWeek;

    /**
     * 月奖励配置
     */
    @Schema(description = "月奖励配置")
    private List<CheckInRewardConfigVO> rewardMonth;


    /**
     * 累计奖励配置
     *
     */
    @Schema(description = "累计奖励配置")
    private List<CheckInRewardConfigVO>  rewardTotal;

    /**
     * 存款金额
     */
    @Schema(description = "补签存款金额")
    private BigDecimal makeDepositAmount;

    /**
     * 有效投注金额
     */
    @Schema(description = "补签有效投注金额")
    private BigDecimal makeBetAmount;

    /**
     * 补签次数限制
     */
    @Schema(description = "补签次数限制")
    private Integer makeupLimit;

    /**
     * 免费旋转
     */
    @Schema(description = "免费旋转图标")
    private String freeWheelPic;

    /**
     * 转盘
     */
    @Schema(description = "转盘图标")
    private String spinWheelPic;

    /**
     * 奖金
     */
    @Schema(description = "奖金图标")
    private String amountPic;

    public boolean validate() {
        if (CollectionUtil.isEmpty(rewardWeek) || CollectionUtil.isEmpty(rewardMonth) || CollectionUtil.isEmpty(rewardTotal)) {
            log.info("周奖励配置或月奖励配置未配置");
            return false;
        }

        if (rewardWeek.size() != 7) {
            log.info("周奖励配置数量错误，应为7，实际为：{}", rewardWeek.size());
            return false;
        }

        for (int i = 0; i < rewardWeek.size(); i++) {
            CheckInRewardConfigVO rewardConfigVO = rewardWeek.get(i);
            if (!rewardConfigVO.validate()) {
                log.info("周奖励配置第 {} 项 validate 校验失败: {}", i + 1, rewardConfigVO);
                return false;
            }
            if (rewardConfigVO.getCode() < 1 || rewardConfigVO.getCode() > 7) {
                log.info("周奖励配置第 {} 项 code 值错误，应为 1-8，实际为：{}", i + 1, rewardConfigVO.getCode());
                return false;
            }
        }

        if (rewardMonth.size() != 12) {
            log.info("月奖励配置数量错误，应为31，实际为：{}", rewardMonth.size());
            return false;
        }

        for (int i = 0; i < rewardMonth.size(); i++) {
            CheckInRewardConfigVO rewardConfigVO = rewardMonth.get(i);
            if (!rewardConfigVO.validate()) {
                log.info("月奖励配置第 {} 项 validate 校验失败: {}", i + 1, rewardConfigVO);
                return false;
            }
            if (rewardConfigVO.getCode() < 1 || rewardConfigVO.getCode() > 31) {
                log.info("月奖励配置第 {} 项 code 值错误，应为 1-31，实际为：{}", i + 1, rewardConfigVO.getCode());
                return false;
            }
        }
        for (int i = 0; i < rewardTotal.size(); i++) {
            CheckInRewardConfigVO rewardConfigVO = rewardTotal.get(i);
            if (!rewardConfigVO.validate()) {
                log.info("累计励配置第 {} 项 validate 校验失败: {}", i + 1, rewardConfigVO);
                return false;
            }
            if (rewardConfigVO.getDayLimit() == null || rewardConfigVO.getDayLimit()<=0){
                log.info("累计励配置置第 {} 项 dayLimit 校验失败: {}", i + 1, rewardConfigVO);
                return false;
            }

        }

        return true;
    }

    /**
     * 补签开关（0：关闭，1：开启）
     */
    @Schema(description = "补签开关（0：关闭，1：开启")
    private Integer checkInSwitch;

    /**
     * 当日存款金额
     */
    @Schema(description = "当日存款金额")
    private BigDecimal depositAmountToday;

    /**
     * 当日投注金额
     */
    @Schema(description = "当日投注金额")
    private BigDecimal betAmountToday;

    /**
     * 极光推送开关（0：关闭，1：开启）
     */
    @Schema(description = "极光推送开关（0：关闭，1：开启）")
    private Integer pushSwitch;

    /**
     * 极光推送终端（如：ANDROID、IOS）
     * {@link com.cloud.baowang.common.core.enums.DeviceType}
     */
    @Schema(description = "1-pc,2-ios_h5,3-ios_app,4-Android_H5,5-Android_APP--system_param中的 device_terminal")
    private String pushTerminal;


}
