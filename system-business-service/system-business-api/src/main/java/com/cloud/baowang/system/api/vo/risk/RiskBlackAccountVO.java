package com.cloud.baowang.system.api.vo.risk;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class RiskBlackAccountVO  extends BaseVO {
    @Schema(title = "账号")
    private String riskControlAccount;
    @Schema(title = "账号名称")
    private String riskControlAccountName;
    @Schema(title = "风控类型code")
    private String riskControlTypeCode;
    @Schema(title = "账号数量")
    private Integer riskCount;
    @Schema(title = "IP白名单")
    private List<String> ipWhitelist;

    @Schema(title = "是否IP段")
    private Boolean ipSegmentFlag = false;

    @Schema(title = "ip开始")
    private String ipStart;

    @Schema(title = "ip开始")
    private String ipEnd;

    @Schema(title = "备注")
    private String remark;
    @Schema(title = "创建人名字")
    private String creatorName;
    @Schema(title = "更新人名字")
    private String updaterName;

    @Schema(description = "siteCode",hidden = true)
    private String siteCode;
}
