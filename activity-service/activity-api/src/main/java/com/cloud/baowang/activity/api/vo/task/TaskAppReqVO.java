package com.cloud.baowang.activity.api.vo.task;


import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


/**
 * 新人任务请求 ReqVO
 */
@Data
@Schema(title = "新人任务请求 ReqVO")
public class TaskAppReqVO extends SitePageVO {


    @Schema(title = "用户ID")
    private String userId;
    @Schema(title = "用户账号")
    private String userAccount;

    @Schema(title = "任务类型")
    private String taskType;


    @Schema(title = "子任务类型")
    private String subTaskType;

    @Schema(title = "时区")
    private String siteCode;




}
