package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP升级请求对象")
public class UserVipFlowRecordReqVO {

    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "会员ID全局唯一")
    private String userId;

    @Schema(description = "降级时间")
    private String relegationDaysTime;

    @Schema(description = "查询所有不包含最高等级数据")
    private Integer maxVipLevel;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "vip升级后等级时候对应的vip等级")
    private Integer nextVipGradeCode;

    @Schema(description = "vip当前等级对应的vip等级")
    private Integer vipGradeCode;


    @Schema(description = "等级是11级并且达到满级保级流水并且 是保级天数是今天的用户全部自动 已完成有效流水为0 才传递此参数")
    private Integer vipMaxGradeCode;
    @Schema(description = "满级降级时间降保级流水时间")
    private String maxGradeRelegationDaysTime;

}
