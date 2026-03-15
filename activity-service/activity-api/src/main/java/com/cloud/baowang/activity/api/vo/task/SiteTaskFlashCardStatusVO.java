package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteTaskFlashCardStatusVO {




    @Schema(description = "操作人", hidden = true)
    private String operator;

    /**
     * 任务类型
     */
    private String taskType;




    /**
     * 站点code
     */
    @Schema(title = "站点code", hidden = true)
    private String siteCode;

    /**

     /**
     * 状态 0已禁用 1开启中
     */
    @Schema(title = "状态 0已禁用 1开启中")
    private Integer status;





}
