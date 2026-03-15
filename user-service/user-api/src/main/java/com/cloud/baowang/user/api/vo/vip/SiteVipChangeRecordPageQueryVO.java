package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "vip变更记录分页查询对象")
public class SiteVipChangeRecordPageQueryVO extends PageVO implements Serializable {
    @Schema(description = "siteCode",hidden = true)
    private String siteCode;
    @Schema(description = "开始时间")
    private Long startTime;
    @Schema(description = "结束时间")
    private Long endTime;
    @Schema(description = "变更类型--同system_param vip_level_change_type 类型的code")
    private String changeType;
    @Schema(description = "会员账号")
    private String userAccount;
    @Schema(description = "操作人")
    private String operator;
    /**
     * {@link com.cloud.baowang.user.api.enums.VipChangeTypeEnum}
     */
    @Schema(description = "查询类型0.vip等级,1.VIP段位",hidden = true)
    private Integer operationType;
}
