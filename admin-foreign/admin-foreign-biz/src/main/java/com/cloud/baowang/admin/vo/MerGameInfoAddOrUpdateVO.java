package com.cloud.baowang.admin.vo;

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
@Schema(description = "总台-游戏信息添加或修改对象")
public class MerGameInfoAddOrUpdateVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "游戏平台Code", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String venueCode;

    @Schema(description = "游戏名称-中文", required = true)
    private String gameName;

    @Schema(description = "接入参数", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String accessParameters;

//    @Schema(description = "支持终端字典code:device_terminal", required = true)
//    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
//    private String supportDevice;

    @Schema(description = "多语言-游戏描述", required = true)
    private List<I18nMsgFrontVO> gameDescI18nCodeList;

    @Schema(description = "多语言-游戏名称", required = true)
    private List<I18nMsgFrontVO> gameI18nCodeList;

    @Schema(description = "多语言-游戏图片", required = true)
    private List<I18nMsgFrontVO> iconI18nCodeList;

    @Schema(description = "多语言-正方形-游戏图片", required = true)
    private List<I18nMsgFrontVO> seIconI18nCodeList;

    @Schema(description = "多语言-竖版-游戏图片", required = true)
    private List<I18nMsgFrontVO> vtIconI18nCodeList;

    @Schema(description = "多语言-横版-游戏图片", required = true)
    private List<I18nMsgFrontVO> htIconI18nCodeList;

    @Schema(description = "默认-游戏图片-中文")
    private String gamePic;

    @Schema(description = "状态")
    private Integer status;


    private String creator;

    private String updater;

    private String creatorName;

    private String updaterName;

    @Schema(description = "维护时间开始")
    private Long maintenanceStartTime;

    @Schema(description = "维护时间结束")
    private Long maintenanceEndTime;

    @Schema(description = "场馆ID")
    private String venueId;

    @Schema(description = "币种")
    private String currencyCode;


}
