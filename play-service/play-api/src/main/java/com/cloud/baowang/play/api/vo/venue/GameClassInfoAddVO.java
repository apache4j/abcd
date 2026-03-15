package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiqi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "一级分类添加请求对象")
public class GameClassInfoAddVO {

    @Schema(description = "id")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String id;

    @Schema(description = "分类名称", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String typeName;

    @Schema(description = "游戏多语言数组 字典code:language_type", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> languageList;

    @Schema(description = "游戏平台图标", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String venueIcon;

    @Schema(description = "状态 字典code:platform_class_status_type")
    private Integer status;


    private String creator;


    private String updater;



}
