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


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "游戏大厅热门游戏")
public class LobbyTopGameVO implements Serializable {

    @Schema(description = "一级分类ID")
    private String gameOneId;

    @Schema(description = "二级分类ID")
    private String gameTwoId;

    @Schema(description = "名称")
    @I18nField
    private String name;

    @Schema(description = "一级分类-目录名称-多语言CODE", required = true)
    @I18nField
    private String directoryI18nCode;

    @Schema(description = "图片")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;

    @Schema(description = "图片")
    private String iconFileUrl;

    @Schema(description = "图片2")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon2;

    @Schema(description = "图片2:深色")
    private String icon2FileUrl;

    @Schema(description = "一级分类-多语言图片", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String typeIconI18nCode;

    @Schema(description = "一级分类-多语言图片")
    private String typeIconI18nCodeFileUrl;

    @Schema(description = "首页排序")
    private Integer homeSort;

  //  @Schema(description = "是否更多游戏: true=更多")
 //   private boolean hasMoreGames ; // 或者 true，视情况而定

    @Schema(title = "模型标记:CA:赌场-常规一级分类,ACELT:彩票,SBA:沙巴体育 ,SIGN_VENUE:单游戏场馆,电竞,斗鸡... ")
    private String modelCode;


    @Schema(description = "热门游戏-子列表")
    private List<LobbyGameInfoVO> gameInfoList;

    @Schema(description = "但场馆游戏详情-子列表")
    private List<LobbySignVenueInfoVO> signGameInfoList;

    @Schema(description = "沙巴体育推荐赛事")
    private List<String> eventsId;

    @Schema(description = "最高返水")
    private BigDecimal maxRebateAmount;

}
