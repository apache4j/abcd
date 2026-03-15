package com.cloud.baowang.activity.param;

import com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/9/13 10:26
 * @Version: V1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SiteActivityEventRecordQueryParam {


    /**
     * 参与IP
     */
    private String ip;


    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 所属活动id
     */
    private String activityId;

    /**
     * 模板
     */
    private String activityTemplate;

    /**
     * userId
     */
    private String userId;

    /**
     * 派发方式
     * {@link ActivityDistributionTypeEnum}
     */
    @Schema(description = "派发方式 0:玩家自领-过期作废 1:玩家自领-过期自动派发 2:立即派发")
    private Integer distributionType;

    /**
     * 指定存款日期
     */
    private Long day;

    private List<Long> dayList;

    /**
     * 结算周期: 0 - 日结, 1 - 周结, 2 - 月结
     */
    private Integer calculateType;

    /**
     * 发放状态,0=未发放，1=已发放
     */
    private Integer status;

    /**
     * 站点时区
     */
    private String timezone;

}
