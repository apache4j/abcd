package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @author qiqi
 */
@Data
@Schema(description = "一级分类添加请求对象")
public class GameOneClassInfoAddVO {


    @Schema(description = "分类名称", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String typeName;

    @Schema(description = "游戏多语言数组 字典code:language_type", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> languageList;


    @Schema(description = "模板CODE 字典code:one_model", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String modelCode;

    @Schema(title = "图片code")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String iconCode;

    private String creator;


    private String updater;



}
