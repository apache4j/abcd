package com.cloud.baowang.system.api.vo.param;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.system.api.vo.business.BusinessStorageMenuRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @author: kimi
 * 站点后台 快捷入口
 */
@Data
@Schema(description = "编辑保存快捷入口 Param")
public class SystemSiteSaveQuickEntryParam {

    @Schema(description = "常用功能")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Size(min = 1, message = "首页功能不能为空")
    private List<BusinessStorageMenuRespVO> quickEntry;
    @Schema(description = "当前操作人-前端不需要赋值",hidden = true)
    private String adminId;
    @Schema(description = "站点编号-前端不需要赋值",hidden = true)
    private String siteCode;
}
