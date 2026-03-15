package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 活动基础信息的所有字段属性
 */
@Data
@Schema(title = "查询活动列表入参")
public class ActivityPartOrderRecordReqVO extends PageVO implements Serializable {

    @Schema(description = "领取状态，-1:查全部,0:未领取,1:已领取,2:已过期")
    private Integer receiveStatus;

    @Schema(description = "-1昨天,-3 最近三天，-7最近7天")
    private Integer dateNum;

    @Schema(description = "自定义查询开始时间")
    private String startTime;

    @Schema(description = "自定义查询结束时间")
    private Long endTime;


}