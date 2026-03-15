package com.cloud.baowang.activity.api.vo.redbag;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "红包雨活动客户端详情")
@I18nClass
public class RedBagRainClientInfoVO {
    @Schema(description = "倒计时 进行中活动 秒")
    private Long advanceTime;
    @Schema(description = "红包掉落时间 秒")
    private Integer dropTime;
    @Schema(description = "进行中活动sessionId")
    private String redbagSessionId;
    @Schema(description = "当前状态 0 活动未开始 1 活动进行中 2 活动已全部结束，advanceTime为 -1")
    private Integer clientStatus;
    @Schema(description = "场次信息")
    private List<RedBagSessionInfoVO> sessionInfoList;
    @Schema(description = "中奖名单")
    private List<RedBagWinnerVO> winnerList;
    @Schema(description = "活动title")
    @I18nField
    private String activityNameI18nCode;
    @Schema(description = "活动规则")
    @I18nField
    private String ruleDesc;
    @Schema(description = "活动头图-移动端 filekey")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String headPictureI18nCode;
    @Schema(description = "活动头图-移动端完整url")
    private String headPictureI18nCodeFileUrl;
    @Schema(description = "活动头图-PC filekey")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String headPicturePcI18nCode;
    @Schema(description = "活动头图-移动端完整url")
    private String headPicturePcI18nCodeFileUrl;
}
