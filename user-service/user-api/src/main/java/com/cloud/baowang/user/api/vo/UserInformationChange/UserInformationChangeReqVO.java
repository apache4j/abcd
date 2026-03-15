package com.cloud.baowang.user.api.vo.UserInformationChange;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员信息变更记录查询单个入参对象")
public class UserInformationChangeReqVO extends PageVO {
    @Schema(description = "开始操作时间")
    private Long startOperatingTime;
    @Schema(description = "结束操作时间")
    private Long endReOperatingTime;
    @Schema(description = "会员账号")
    private String memberAccount;
    @Schema(description = "账号类型")
    private List<String> accountType;
    @Schema(description = "变更类型")
    private List<String> changeType;
    @Schema(description = "操作人")
    private String operator;
    @Schema(title = "站点code",hidden = true)
    private String siteCode;
    private Boolean dataDesensitization;
}
