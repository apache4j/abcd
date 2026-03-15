package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum;
import com.cloud.baowang.activity.api.enums.DisCountTypeEnum;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Schema(description = "指定存款日期活动响应")
@Data
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
public class ActivityAssignDayRespVO extends ActivityBaseRespVO implements Serializable {
    @Schema(description = "活动Id")
    private String activityId;

    @Schema(description = "指定日期存款")
    @NotNull(message = "指定存款日期不能为空")
    private String weekDays;

    /**
     * {@link DisCountTypeEnum}
     */
    @Schema(description = "优惠方式 0:阶梯次数 1:固定次数")
    @NotNull(message = "优惠方式不能为空")
    private Integer discountType;

    /**
     * 参与方式,0.手动参与，1.自动参与
     * {@link ActivityParticipationModeEnum}
     */

    @Schema(description = "参与方式 0:手动参与 1:自动参与")
    @NotNull(message = "参与方式不能为空")
    private Integer participationMode;

    /**
     * 派发方式
     * {@link ActivityDistributionTypeEnum}
     */
    @Schema(description = "派发方式 0:玩家自领-过期作废 1:玩家自领-过期自动派发 2:立即派发")
    @NotNull(message = "派发方式不能为空")
    private Integer distributionType;


    @Schema(description = "匹配条件 优惠方式=百分比时-通用" )
    private  ActivityAssignDayCondVO percentCondVO;

    @Schema(description = "匹配条件 优惠方式=固定金额时-通用")
    private List<ActivityAssignDayCondVO> fixCondVOList;

    /**
     *  当没有设置游戏大类
     */
    private List<ActivityAssignDayCondVO> activityAssignDayCondVOList;

    /**
     * 通用配置 当没有设置游戏大类
     */
    @Schema(description = "当配置游戏大类，每一个游戏的奖励配置")
    @I18nField
    private List<ActivityAssignDayVenueVO> activityAssignDayVenueVOS;


    /**
     * 场馆类型
     * system_param "venue_type"
     */
    @Schema(description = "场馆类型")
    @NotNull(message = "场馆不能为空,固定PP")
    private String venueCode;
    @Schema(description = "pp游戏code")
    @NotNull(message = "pp游戏code不能为空")
    private String accessParameters;
    @Schema(description = "限注金额")
    @NotNull(message = "限注金额不能为空")
    private BigDecimal betLimitAmount;


    /**
     * 把固定金额和百分比的条件合并成一个
     */
    public List<ActivityAssignDayCondVO> getActivityAssignDayCondVOList() {
        List<ActivityAssignDayCondVO> list= Lists.newArrayList();
        if(!CollectionUtils.isEmpty(this.getFixCondVOList())){
            list.addAll(this.getFixCondVOList());
        }
        if(this.getPercentCondVO()!=null){
            list.add(this.getPercentCondVO());
        }
        return list;
    }
    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     */
    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子,场馆类型：字典CODE：venue_type")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.VENUE_TYPE)
    private String venueType;

    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     */
    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子")
    private String venueTypeText;
}
