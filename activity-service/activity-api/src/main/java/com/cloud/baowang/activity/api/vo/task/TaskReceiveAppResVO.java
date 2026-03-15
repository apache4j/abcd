package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: wade
 */
@Data
@Schema(title = "任务客户端 领取任务结果")
@I18nClass
public class TaskReceiveAppResVO implements Serializable {


    /**
     * 彩金奖励
     */
    @Schema(description = "彩金奖励")
    private BigDecimal rewardAmount;









}
