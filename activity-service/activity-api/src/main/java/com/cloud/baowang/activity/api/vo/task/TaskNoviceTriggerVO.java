package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Schema(description = "会员新人任务触发类")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskNoviceTriggerVO extends MessageBaseVO {
    /**
     * {@link com.cloud.baowang.activity.api.enums.task.TaskEnum}
     * [welcome,currency,phone,email]
     */
    @Schema(description = "新人类型")
    private List<String> subTaskTypes;

    @Schema(description = "会员id")
    private String userId;

    @Schema(description = "userName")
    private String userName;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "站点代码")
    private String siteCode;

    @Schema(description = "vip等级")
    private Integer vipGradeCode;

    @Schema(description = "vip段位")
    private Integer vipRankCode;


    @Schema(description = "上级代理")
    private String superAgentId;


    @Schema(description = "注册时间")
    private Long registerTime = System.currentTimeMillis();




}
