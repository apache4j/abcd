package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: fangfei
 * @createTime: 2023/10/28 15:25
 * @description: 佣金发放记录查询请求VO
 */
@Data
@Schema(description = "佣金发放记录查询请求VO")
@Builder
@AllArgsConstructor
public class CommissionGranRecordReqVO extends PageVO implements Serializable {
    @Schema(description =  "发放开始时间")
    private Long grantStartTime;
    @Schema(description =  "发放结束时间")
    private Long grantEndTime;
    @Schema(description =  "代理账号")
    private String agentAccount;
    @Schema(description ="代理类型")
    private String agentType;
    @Schema(title = "代理类别")
    private String agentCategory;
    @Schema(description =  "佣金类型")
    private String commissionType;
    @Schema(description =  "结算周期")
    private String settleCycle;
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "所属商务账号")
    private String merchantAccount;
    @Schema(description = "所属商务名称")
    private String merchantName;

}
