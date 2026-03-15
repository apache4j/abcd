package com.cloud.baowang.activity.po.v2;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.activity.api.vo.v2.ActivityContestPayoutVenueV2VO;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 赛事包赔活动PO
 */
@Data
@NoArgsConstructor
@TableName(value = "site_activity_contest_payout_v2")
public class SiteActivityContestPayoutV2PO extends SiteBasePO {

    /**
     * 所属活动
     */
    private String activityId;

    /**
     * 活动适用范围,0:全体会员，1.新注册会员
     * {@link com.cloud.baowang.activity.api.enums.ActivityScopeEnum}
     */
    private String activityScope;
    /**
     * 站点编码
     */
    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "活动类型(游戏类型,类=体育)")
    private String venueType;
 /*   @Schema(description = "活动大类列表")
    private List<ActivityContestPayoutVenueV2VO> activityContestPayoutVenueVOS;*/

    @Schema(description = "场馆编码")
    @NotNull(message = "场馆编码")
    private String venueCode;

    @Schema(description = "场馆名称")
    private String venueName;

    @Schema(description = "游戏id")
    @NotNull(message = "游戏code不能为空")
    private String accessParameters;


    @Schema(title = "平台币还是法币")
    private String platformOrFiatCurrency;


    /**
     * 三方A赛事推荐图-移动端白天图
     */
    private String thirdADayAppI18nCode;

    /**
     * 三方A赛事推荐图-移动端夜间图
     */
    private String thirdANightAppI18nCode;


    /**
     * 三方A赛事推荐图- PC端白天图
     */
    private String thirdADayPcI18nCode;

    /**
     * 三方A赛事推荐图- PC端夜间图
     */
    private String thirdANightPcI18nCode;

    /**
     * 三方B赛事推荐图-移动端白天图
     */
    private String thirdBDayAppI18nCode;

    /**
     * 三方B赛事推荐图-移动端夜间图
     */
    private String thirdBNightAppI18nCode;

    /**
     * 三方B赛事推荐图- PC端白天图
     */
    private String thirdBDayPcI18nCode;

    /**
     * 三方B赛事推荐图- PC端夜间图
     */
    private String thirdBNightPcI18nCode;
}
