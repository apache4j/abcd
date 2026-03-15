package com.cloud.baowang.system.api.vo.site.tutorial.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description ="教程配置修改记录列表vo")
public class TutorialOperationRecordRspVO  {
    @Schema(description ="id")
    private String id;
    @Schema(description ="站点code")
    private String siteCode;

    @Schema(description ="变更时间")
    private Long updateTime;

    @Schema(description ="变更目录")
    private String changeCatalog;

    @Schema(description ="变更类型")
    private String changeType;

    @Schema(description ="变更前")
    private String beforeChange;

    @Schema(description ="变更后")
    private String afterChange ;

    @Schema(description ="操作人")
    private String operator;

    @Schema(description = "1-名称 2-状态 3-图片 4富文本")
    private String typeMark;


    @Schema(description ="状态变更-前")
    private Integer beforeStatus;
    @Schema(description ="状态变更-后")
    private Integer afterStatus;

}
