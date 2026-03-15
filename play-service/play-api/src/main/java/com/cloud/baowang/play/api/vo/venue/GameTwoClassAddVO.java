package com.cloud.baowang.play.api.vo.venue;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author qiqi
 */
@Data
@Schema(description = "二级分类添加请求对象")
public class GameTwoClassAddVO {

    @Schema(description = "二级分类名称", required = true)
    private String typeName;

    @Schema(description = "游戏多语言数组 字典code:language_type", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> typeI18nCodeList;

    @Schema(description = "一级分类ID", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String gameOneId;

    @Schema(description = "图标", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String icon;

    @Schema(description = "冠名标签")
    private Integer siteLabelChangeType;


    @Schema(description = "皮肤4:二级分类游戏横版图标-多语言")
    private List<I18nMsgFrontVO> htIconI18nCodeList;


    @Schema(description = "游戏列表 调用 游戏管理页面 游戏信息-列表接口", required = true)
    private List<GameTwoClassCurrencyUserInfoVO> gameTwoClassCurrencyUserInfoList;

//    @Schema(description = "币种")
//    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
//    private String currencyCode;
//
//    @Schema(description = "游戏列表 调用 游戏管理页面 游戏信息-列表接口", required = true)
//    private List<GameClassInfoSetSortDetailVO> gameIds;

    private String creator;

    private String updater;


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
