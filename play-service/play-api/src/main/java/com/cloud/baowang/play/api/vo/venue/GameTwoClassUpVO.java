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
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author qiqi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "二级分类修改请求对象")
public class GameTwoClassUpVO {

    @Schema(description = "游戏平台ID", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String id;

//    @Schema(description = "二级分类名称")
//    @Length(max = 20, message = "名称过长")
//    private String typeName;

    @Schema(description = "游戏多语言数组 字典code:language_type", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> typeI18nCodeList;

    @Schema(description = "上级分类ID，调用游戏配置菜单一级分类配置-一级分类-列表接口获取数据", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String gameOneId;

    @Schema(description = "状态 字典code:platform_class_status_type")
    private Integer status;

    @Schema(description = "图片CODE", required = true)
    private String icon;

    @Schema(description = "皮肤4:二级分类游戏横版图标-多语言")
    private List<I18nMsgFrontVO> htIconI18nCodeList;

    @Schema(description = "冠名标签")
    private Integer siteLabelChangeType;


    @Schema(description = "游戏列表 调用 游戏管理页面 游戏信息-列表接口", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<GameTwoClassCurrencyUserInfoVO> gameTwoClassCurrencyUserInfoList;

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
