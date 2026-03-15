package com.cloud.baowang.play.api.vo.venue;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author qiqi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "游戏信息添加或修改对象")
public class GameInfoAddOrUpdateVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "游戏平台Code", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String venueCode;

    @Schema(description = "游戏平台ID", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String venueId;

    @Schema(description = "游戏名称")
    private String gameName;

    @Schema(description = "接入参数", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String accessParameters;

    @Schema(description = "标签 字典code:game_label", required = true)
    private Integer label;

    @Schema(description = "角标 字典code:corner_labels", required = true)
    private Integer cornerLabels;

    @Schema(description = "支持终端字典code:device_terminal", required = true)
    private String supportDevice;

    @Schema(description = "游戏描述不能为空,多语言数组名称", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> gameDescI18nCodeList;


    @Schema(description = "游戏名称-多语言", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> gameI18nCodeList;

    @Schema(description = "游戏图片-多语言", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> iconI18nCodeList;

    @Schema(description = "多语言-正方形-游戏图片", required = true)
    private List<I18nMsgFrontVO> seIconI18nCodeList;

    @Schema(description = "多语言-竖版-游戏图片", required = true)
    private List<I18nMsgFrontVO> vtIconI18nCodeList;

    @Schema(description = "多语言-横版-游戏图片", required = true)
    private List<I18nMsgFrontVO> htIconI18nCodeList;


    @Schema(description = "状态")
    private Integer status;


    private String creator;

    private String updater;

    private String currencyCode;


    @Schema(description = "维护时间开始")
    private Long maintenanceStartTime;

    @Schema(description = "维护时间结束")
    private Long maintenanceEndTime;

    public void valid(List<I18nMsgFrontVO> list){
        if (CollectionUtil.isNotEmpty(list)) {
            for (I18nMsgFrontVO item : list) {
                if (StringUtils.isBlank(item.getLanguage()) || StringUtils.isBlank(item.getMessage())) {
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }
        }
    }



}
