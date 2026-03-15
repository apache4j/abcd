package com.cloud.baowang.play.api.vo.lobby;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: sheldon
 * @Date: 3/29/24 5:29 下午
 */

@Data
@Builder
@I18nClass
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "游戏关系关联详情对象")
public class LobbyGameOneVO implements Serializable {

    @Schema(title = "游戏一级分类ID")
    private String gameOneClassId;

    @Schema(title = "游戏一级分类目录名称")
    @I18nField
    private String directoryName;

    @Schema(title = "游戏一级分类首页名称")
    @I18nField
    private String homeName;

    @Schema(title = "一级分类图片")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;

    @Schema(title = "一级分类图片地址")
    private String iconFileUrl;


    @Schema(title = "一级分类图片:深色")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon2;

    @Schema(title = "一级分类图片地址:深色")
    private String icon2FileUrl;

    @Schema(title = "一级分类,类型图片CODE")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String typeIconI18nCode;

    @Schema(title = "一级分类图片地址")
    private String typeIconI18nCodeFileUrl;


    @Schema(title = "场馆CODE")
    private String venueCode;

    @Schema(title = "模型标记:CA:赌场-常规一级分类,ACELT:彩票,SBA:沙巴体育 ,SIGN_VENUE:单游戏场馆,电竞,斗鸡... ")
    private String modelCode;

    @Schema(description = "二级列表")
    List<LobbyGameTwoVO> twoList;

    @Schema(description = "游戏详情")
    private LobbyGameInfoVO gameInfo;

    @Schema(description = "游戏详情")
    private List<LobbySignVenueInfoVO> gameInfoList;

    @Schema(description = "最高返水")
    private BigDecimal maxRebateAmount;

    @Schema(description = "PC鼠标悬浮列表")
    private List<LobbyGameOneFloatVO> pcFloatList;

    @Schema(description = "皮肤4:国内盘字段:返水场馆类型标签")
    private Integer rebateVenueType;


}
