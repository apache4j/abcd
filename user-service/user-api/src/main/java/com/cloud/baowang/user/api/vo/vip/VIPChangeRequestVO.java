package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author 小智
 * @Date 8/5/23 5:10 PM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP变更记录入参对象")
public class VIPChangeRequestVO extends PageVO implements Serializable {

    @Schema(title = "变更类型")
    private Integer changeType;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "操作人")
    private String operator;

    @Schema(title = "变更开始时间")
    private Long changeTimeStart;

    @Schema(title = "变更结束时间")
    private Long changeTimeEnd;
    @Schema(description = "siteCode",hidden = true)
    private String siteCode;
}
