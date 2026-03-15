package com.cloud.baowang.activity.api.vo.task;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @className: SiteTaskConfigPO
 * @author: wade
 * @description: 任务配置
 * @date: 18/9/24 14:38
 */
@Schema(description = "任务保存--存款任务-邀请惹任务-入参")
@Data
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class TaskSubConfigReqVO implements Serializable {

    /**
     * 最小配置金额
     */
    @Schema(title = "最小配置金额-每日/每周任务")
    private BigDecimal minBetAmount;


    /**
     * 日累计存款
     */
    @Schema(title = "日累计存款")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER) // 保持数字类型
    @Digits(integer = 10, fraction = 2) // 限制最多2位小数
    private BigDecimal depositAmount;

    /**
     * 配置邀请好友个数
     */
    @Schema(title = "配置邀请好友个数")
    private Integer inviteFriendCount;


    /**
     * 彩金奖励
     */
    @Schema(title = "彩金奖励")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER) // 保持数字类型
    @Digits(integer = 10, fraction = 2) // 限制最多2位小数
    private BigDecimal rewardAmount;

    /**
     * 洗码倍率
     */
    @Schema(title = "排序值")
    //@NotNull(message = "洗码倍率不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer step;

    /*public static void main(String[] args) {
        List<TaskSubConfigReqVO> list = new ArrayList<>();
        TaskSubConfigReqVO reqVO = new TaskSubConfigReqVO();
        reqVO.setStep(1);
        reqVO.setDepositAmount(new BigDecimal(200));
        reqVO.setRewardAmount(new BigDecimal(18));
        reqVO.setInviteFriendCount(1);
        list.add(reqVO);
        TaskSubConfigReqVO reqVO2 = new TaskSubConfigReqVO();
        reqVO2.setStep(2);
        reqVO2.setDepositAmount(new BigDecimal(250));
        reqVO2.setRewardAmount(new BigDecimal(28));
        reqVO2.setInviteFriendCount(2);
        list.add(reqVO2);

        TaskSubConfigReqVO reqVO3 = new TaskSubConfigReqVO();
        reqVO3.setStep(3);
        reqVO3.setDepositAmount(new BigDecimal(350));
        reqVO3.setRewardAmount(new BigDecimal(38));
        reqVO3.setInviteFriendCount(3);
        list.add(reqVO3);
        String json = JSONObject.toJSONString(list);
        System.out.println(json);

    }*/


}
