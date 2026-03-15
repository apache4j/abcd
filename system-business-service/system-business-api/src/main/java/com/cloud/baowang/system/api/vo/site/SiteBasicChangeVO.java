package com.cloud.baowang.system.api.vo.site;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/7/27 17:40
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="站点基础信息配置对象")
public class SiteBasicChangeVO {

    @Schema(description ="站点名称")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Size(max = 10, message = ConstantsCode.PARAM_ERROR)
    private String siteName;

    @Schema(description ="所属公司")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Size(max = 10, message = ConstantsCode.PARAM_ERROR)
    private String company;

    @Schema(description ="站点前缀")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Size(max = 2, message = ConstantsCode.PARAM_ERROR)
    private String sitePrefix;

    @Schema(description ="站点类型")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String siteType;

    @Schema(description ="后台管理员账号")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String siteAdminAccount;

    @Schema(description = "白名单列表,逗号拼接")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String allowIps;

    @Schema(description ="抽成方案")
    private String commissionPlan;

    @Schema(description ="支持语言")
    private List<String> language;

    @Schema(description ="支持币种")
    private List<String> currency;

    private String remark;
    @Schema(description = "时区")
    private String timezone;

}
