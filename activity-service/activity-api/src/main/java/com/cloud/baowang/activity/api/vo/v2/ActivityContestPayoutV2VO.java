package com.cloud.baowang.activity.api.vo.v2;

import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @description 赛事包赔活动-v2
 * @author BEJSON.com
 * @date 2025-10-18
 */
@Slf4j
@Data
public class ActivityContestPayoutV2VO extends ActivityBaseV2VO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 活动ID
     *
     */
    @Schema(description = "活动Id")
    private String activityId;

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 活动适用范围，0:全体会员，1:新注册会员
     * {@link com.cloud.baowang.activity.api.enums.ActivityScopeEnum }
     */
    private String activityScope;

    /**
     * 游戏类别，类别=体育
     */
    private String venueType;
    @Schema(description = "活动大类列表")
    private List<ActivityContestPayoutVenueV2VO> activityContestPayoutVenueVOS;

    /**
     * 场馆编号
     */
    private String venueCode;

    /**
     * 场馆名称
     */
    private String venueName;


    /**
     * 三方A赛事推荐图-移动端白天图
     */
    @Schema(title = "三方A赛事推荐图-移动端白天图")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String thirdADayAppI18nCode;

    @Schema(title = "三方A赛事推荐图-移动端白天图")
    private List<I18nMsgFrontVO> thirdADayAppI18nCodeList;
    /**
     * 三方A赛事推荐图-移动端白天图
     */
    @Schema(title = "三方A赛事推荐图-移动端夜间图")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String thirdANightAppI18nCode;

    @Schema(title = "三方A赛事推荐图-移动端夜间图")
    private List<I18nMsgFrontVO> thirdANightAppI18nCodeList;
    /**
     * 三方A赛事推荐图- PC端白天图
     */
    @Schema(title = "三方A赛事推荐图- PC端白天图")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String thirdADayPcI18nCode;
    @Schema(title = "三方A赛事推荐图- PC端白天图")
    private List<I18nMsgFrontVO> thirdADayPcI18nCodeList;

    /**
     * 三方A赛事推荐图- PC端白天图
     */
    @Schema(title = "三方A赛事推荐图- PC端夜间图")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String thirdANightPcI18nCode;

    @Schema(title = "三方A赛事推荐图- PC端夜间图")
    private List<I18nMsgFrontVO> thirdANightPcI18nCodeList;

    /**
     * 三方B赛事推荐图-移动端白天图
     */
    @Schema(title = "三方B赛事推荐图-移动端白天图")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String thirdBDayAppI18nCode;

    @Schema(title = "三方B赛事推荐图-移动端白天图")
    private List<I18nMsgFrontVO> thirdBDayAppI18nCodeList;

    /**
     * 三方B赛事推荐图-移动端白天图
     */
    @Schema(title = "三方B赛事推荐图-移动端夜间图")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String thirdBNightAppI18nCode;

    @Schema(title = "三方B赛事推荐图-移动端夜间图")
    private List<I18nMsgFrontVO> thirdBNightAppI18nCodeList;

    /**
     * 三方B赛事推荐图- PC端白天图
     */
    @Schema(title = "三方B赛事推荐图- PC端白天图")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String thirdBDayPcI18nCode;

    @Schema(title = "三方B赛事推荐图- PC端白天图")
    private List<I18nMsgFrontVO> thirdBDayPcI18nCodeList;

    /**
     * 三方B赛事推荐图- PC端白天图
     */
    @Schema(title = "三方B赛事推荐图- PC端夜间图")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String thirdBNightPcI18nCode;

    @Schema(title = "三方B赛事推荐图- PC端夜间图")
    private List<I18nMsgFrontVO> thirdBNightPcI18nCodeList;
    /**
     * 游戏访问参数，游戏id
     */
    private String accessParameters;

    /**
     * 活动币种类型（0.平台币，1. 法币），作为扩展字段
     */
    @Schema(description = "0:平台币, 1: 法币")
    private String platformOrFiatCurrency;

    /**
     * 创建时间
     */
    private Long createdTime;

    /**
     * 修改时间
     */
    private Long updatedTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String updater;

    /**
     * 备注
     */
    private String remark;

    public boolean validate(){

        boolean success = false;

        if (StrUtil.isEmptyIfStr(activityId) || StrUtil.isEmptyIfStr(this.venueCode)
                || StrUtil.isEmptyIfStr(this.venueType)
                || StrUtil.isEmptyIfStr(this.siteCode)){
            log.error("activity contest payout parameter is null!");
            return success;
        }
        return true;
    }
}