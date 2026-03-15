package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.activity.api.enums.DisCountTypeEnum;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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



@Schema(description = "免费旋转响应")
@Data
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
public class ActivityFreeWheelRespVO extends ActivityBaseRespVO implements Serializable {
    @Schema(description = "活动id")
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
     * 参与方式,0 手动参与 1 自动参与
     * {@link com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum}
     */
    @Schema(description = "参与方式,0 手动参与 1 自动参与")
    @NotNull(message = "参与方式不能为空")
    @Min(value = 0, message = "参与方式不能小于0")
    @Max(value = 1, message = "参与方式不能大于1")
    private Integer participationMode;



    @Schema(description = "匹配条件 优惠方式=固定次数 时传递")
    ActivityFreeWheelCondVO fixCondVO;

    @Schema(description = "匹配条件 优惠方式=阶梯次数 时传递")
    private List<ActivityFreeWheelCondVO> stepCondVOList;


    private List<ActivityFreeWheelCondVO> activityFreeWheelCondVOList;


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


    public List<ActivityFreeWheelCondVO> getActivityFreeWheelCondVOList() {
        List<ActivityFreeWheelCondVO> list= Lists.newArrayList();
        if(this.getFixCondVO()!=null){
            list.add(this.getFixCondVO());
        }
        if(!CollectionUtils.isEmpty(this.getStepCondVOList())){
            list.addAll(this.getStepCondVOList());
        }
        return list;
    }

}
