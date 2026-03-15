package com.cloud.baowang.user.api.vo.welfarecenter;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "app-会员福利中心分页查询对象")
@Data
@I18nClass
public class WelfareCenterRewardPageQueryVO extends PageVO {
    @Schema(description = "id", hidden = true)
    private String id;
    @Schema(description = "userAccount", hidden = true)
    private String userAccount;

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    /**
     * {@link com.cloud.baowang.user.api.enums.WelfareCenterRewardType}
     */
    @Schema(description = "福利类型0.vip福利,1.活动优惠,2.新手任务,3.勋章奖励,4.勋章宝箱,5.每日任务,6.每周任务")
    @I18nField(value = CommonConstant.WELFARE_CENTER_REWARD_TYPE, type = I18nFieldTypeConstants.DICT)
    private Integer welfareCenterRewardType;
    /**
     * 领取状态
     * {@link com.cloud.baowang.user.api.enums.ActivityReceiveStatusEnum}
     */
    @Schema(description = "领取状态0.未领取,1.已领取,2.已过期")
    private Integer receiveStatus;

    @Schema(description = "派发时间-开始时间")
    private Long pfTimeStartTime;

    @Schema(description = "派发时间-结束时间")
    private Long pfTimeEndTime;

    @Schema(description = "当前系统时间,查询用", hidden = true)
    private Long systemTime;

    @Schema(description = "userId", hidden = true)
    private String userId;

    @Schema(description = "查询福利为站点当前时区90天开始时间,查询用", hidden = true)
    private Long siteStartTime;

}
