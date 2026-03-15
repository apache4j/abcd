package com.cloud.baowang.user.api.vo.UserDetails;

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
@Schema(title = "批量修改会员备注对象")
public class BathUserRemarkReqVO {
    @Schema(title = "备注信息")
    private String remark;
    @Schema(title = "会员账号")
    private List<String> userAccounts;
    @Schema(title = "站点code", hidden = true)
    private String siteCode;

    @Schema(title = "operator", hidden = true)
    private String operator;

}
