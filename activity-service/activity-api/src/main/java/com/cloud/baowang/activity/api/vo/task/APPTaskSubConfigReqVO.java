package com.cloud.baowang.activity.api.vo.task;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.activity.api.enums.task.TaskReceiveStatusEnum;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.cloud.baowang.common.core.serializer.BigDecimalTwoDecimalSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@Schema(description = "存款任务-邀请任务-显示APP")
@Data
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class APPTaskSubConfigReqVO implements Serializable {

    /**
     * 最小配置金额
     */
    @Schema(title = "任务发放记录Id-发放了才有，领取经历根据id")
    private String recordId;

    /**
     * 最小配置金额
     */
    @Schema(title = "最小阶梯配置金额-每日/每周任务投注金额/盈利/负盈利/存款活动")
    private BigDecimal minBetAmount;

    /**
     *
     */
    @Schema(description = "会员在当周或者当日达到的投注金额/盈利/负盈利 每日存款任务-个人实际存款")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER) // 保持数字类型
    @Digits(integer = 10, fraction = 2) // 限制最多2位小数
    private BigDecimal achieveAmount = BigDecimal.ZERO;


    /**
     * 日累计存款
     */
    @Schema(title = "日累计存款，配置的存款金额")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER) // 保持 JSON 输出为数字
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class) // 自定义序列化
    @Digits(integer = 10, fraction = 2) // 限制最多2位小数
    private BigDecimal depositAmount;


    @Schema(description = "邀请任务-个人实际邀请")
    private Integer achieveCount = 0;

    /**
     * 配置邀请好友个数
     */
    @Schema(title = "配置邀请好友个数")
    private Integer inviteFriendCount;


    /**
     * 彩金奖励
     */
    @Schema(title = "彩金奖励 ")
    //@NotNull(message = "彩金奖励不能为空")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER) // 保持 JSON 输出为数字
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class) // 自定义序列化
    @Digits(integer = 10, fraction = 2) // 限制最多2位小数
    private BigDecimal rewardAmount;

    /**
     * 洗码倍率
     */
    @Schema(title = "排序值")
    //@NotNull(message = "洗码倍率不能为空")
    private Integer step;


    @Schema(title = "是否获取奖励")
    private Boolean rewardFlag = false;


    @Schema(title = "阶梯是否领取奖励 0-未领取 1-已领取 2-已过期 3-未达到领取条件")
    private Integer receiveStatus = 3 ;


    /*public static void main(String[] args) {
        List<APPTaskSubConfigReqVO> list = new ArrayList<>();
        APPTaskSubConfigReqVO reqVO = new APPTaskSubConfigReqVO();
        reqVO.setStep(1);
        reqVO.setDepositAmount(new BigDecimal(200));
        reqVO.setRewardAmount(new BigDecimal(18));
        reqVO.setInviteFriendCount(1);
        list.add(reqVO);
        APPTaskSubConfigReqVO reqVO2 = new APPTaskSubConfigReqVO();
        reqVO2.setStep(2);
        reqVO2.setDepositAmount(new BigDecimal(250));
        reqVO2.setRewardAmount(new BigDecimal(28));
        reqVO2.setInviteFriendCount(2);
        list.add(reqVO2);

        APPTaskSubConfigReqVO reqVO3 = new APPTaskSubConfigReqVO();
        reqVO3.setStep(3);
        reqVO3.setDepositAmount(new BigDecimal(350));
        reqVO3.setRewardAmount(new BigDecimal(38));
        reqVO3.setInviteFriendCount(3);
        list.add(reqVO3);
        String json = JSONObject.toJSONString(list);
        System.out.println(json);

    }
*/

    


}
