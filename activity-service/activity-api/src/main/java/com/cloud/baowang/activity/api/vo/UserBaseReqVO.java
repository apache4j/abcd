package com.cloud.baowang.activity.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "用户请求基础个人信息")
public class UserBaseReqVO {

    //
    private String activityId;

    //用户ID
    private String userId;

    //用户账户
    private String userAccount;

    //站点
    private String siteCode;

    //时区
    private String timezone;

    //设备
    private Integer deviceType;


    // 申请操作:true 派发操作:false
    private boolean applyFlag = true;

    //开始时间戳
    private Long dayStartTime;
    //结束时间戳
    private Long dayEndTime;

    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     */
    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子,场馆类型：字典CODE：venue_type")
    private String venueType;

    @Schema(description = "时间")
    private String dateStr;

    @Schema(description = "终端")
    private Integer showTerminal;

}
