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

import java.math.BigDecimal;
import java.util.List;

/**
 * @author qiqi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "一级分类添加请求对象")
public class GameOneClassInfoUpVO {

    @Schema(description = "id", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String id;

    @Schema(description = "目录名称", required = true)
    private String directoryName;

    @Schema(description = "首页名称", required = true)
    private String homeName;

    @Schema(description = "目录名称 多语言 字典code:language_type", required = true)
    private List<I18nMsgFrontVO> directoryI18nCodeList;

    @Schema(description = "首页名称 多语言 字典code:language_type", required = true)
    private List<I18nMsgFrontVO> homeI18nCodeList;

    @Schema(description = "一级分类图片-多语言 ", required = true)
    private List<I18nMsgFrontVO> typeIconI18nCodeList;


    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "图标")
    private String icon2;


    @Schema(description = "类型 字典code:up_game_one_type")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer gameOneType;

    @Schema(description = "场馆ID")
    private String venueId;

    @Schema(description = "场馆CODE", hidden = true)
    private String venueCode;

    @Schema(description = "一级分类场馆关系", hidden = true)
    private List<AddGameOneClassVenueVO> gameOneClassVenue;


    @Schema(description = "皮肤4:国内盘字段:奖金池")
    private BigDecimal prizePoolTotal;

    @Schema(description = "皮肤4:国内盘字段:奖金池开始金额")
    private BigDecimal prizePoolStart;

    @Schema(description = "皮肤4:国内盘字段:奖金池结束金额")
    private BigDecimal prizePoolEnd;

    @Schema(description = "皮肤4:国内盘字段:返水场馆类型标签")
    private Integer rebateVenueType;



    public void valid(List<I18nMsgFrontVO> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            for (I18nMsgFrontVO item : list) {
                if (StringUtils.isBlank(item.getLanguage()) || StringUtils.isBlank(item.getMessage())) {
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }
        }
    }
}
