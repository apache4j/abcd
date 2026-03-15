package com.cloud.baowang.user.api.vo.user.invite;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/11/23 22:03
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "邀请码配置请求VO")
public class SiteUserInviteConfigReqVO extends BaseVO implements Serializable {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "语言")
    private String language;
    @Schema(description = "首存金额")
    private BigDecimal firstDepositAmount;
    @Schema(description = "累计存款金额")
    private BigDecimal depositAmountTotal;
    @Schema(description = "PC端图标集合")
    private List<InviteIconVO> pcIconList;
    @Schema(description = "移动端图标集合")
    private List<InviteIconVO> h5IconList;
}
