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
@Schema(description = "邀请码配置响应VO")
public class SiteUserInviteConfigResponseVO extends BaseVO implements Serializable {
    @Schema(description = "语言")
    private String language;
    @Schema(description = "展示code")
    private String showCode;
    @Schema(description = "语种名称")
    private String languageName;
    @Schema(description = "首存金额")
    private BigDecimal firstDepositAmount;
    @Schema(description = "累计存款金额")
    private BigDecimal depositAmountTotal;
    @Schema(description = "货币单位")
    private String currencyUnit;
    @Schema(title = "PC端图标集合", description = "PC端图标集合")
    private List<SiteUserInviteIconVO> pcIconList;
    @Schema(title = "移动端端图标集合", description = "移动端端图标集合")
    private List<SiteUserInviteIconVO> h5IconList;
}
