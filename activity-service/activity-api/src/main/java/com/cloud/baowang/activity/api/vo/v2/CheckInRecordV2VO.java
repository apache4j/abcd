package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "签到活动历史")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@I18nClass
public class CheckInRecordV2VO {


    @Schema(description = "日期yyyy-MM-dd")
    private String dayStr;


    @Schema(description = "1-签到过期(今天以前的未签到) 5-签到过期(今天以前的未签到-且补签功能已经关闭) 2-已签到（今天之前已经签到，包括今天签到） 3-可签到 （今天签到状态未签到）4-未来可签到（今天以后的时间）")
    private String status;


    @Schema(description = "是否周末或者月末")
    private Boolean isRoundFlag = false;



    @Schema(description = "是否今天")
    private Boolean isTodayFlag = false;

    @Schema(description = "转盘次数，0表示没有")
    private Integer acquireSpinNum = 0;
    @Schema(description = "免费旋转次数，0表示没有")
    private Integer acquireFreeNum = 0;

    @Schema(description = "赠送金额,0表示没有")
    private BigDecimal acquireAmount = BigDecimal.ZERO;

    @Schema(description = "配置值-决定显示图标")
    private Integer rewardTypeCode;

    /**
     * 免费旋转
     */
    @Schema(description = "如果获取奖励图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String iconPic;

    /**
     * 免费旋转
     */
    @Schema(description = "如果获取奖励图标")
    private String iconPicFileUrl;

    @Schema(description = "周几")
    private Integer weekNum ;

    @Schema(description = "今天是否补签过-可显示补签第一个，其他的是false")
    private Boolean isShowMakeupFlag = false;




}
