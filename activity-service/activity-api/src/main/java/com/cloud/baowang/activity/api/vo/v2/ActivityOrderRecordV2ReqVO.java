package com.cloud.baowang.activity.api.vo.v2;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(title = "福利中心-活动礼包记录")
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityOrderRecordV2ReqVO extends PageVO implements Serializable {

    @Schema(description = "0代表空,-1昨天,-3 最近三天，-7最近7天")
    private Integer dateNum;

    @Schema(description = "开始时间")
    private Long startTime;

    @Schema(description = "结束时间")
    private Long endTime;

    @Schema(description = "ID")
    private String id;

    @Schema(description = "活动名称")
    private String activityName;

    @Schema(description = "用户名称")
    private String userAccount;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "时间类型，字典CODE:release_time_type")
    private Integer releaseTimeType;

    @Schema(description = "领取状态，字典CODE:activity_receive_status")
    private Integer receiveStatus;

    @Schema(description = "会员id", hidden = true)
    private String userId;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;

    @Schema(description = "活动模版", hidden = true)
    private String activityTemplate;

    @Schema(description = "活动-多语言集合", hidden = true)
    private List<String> activityNameI18nCodeList;

}
