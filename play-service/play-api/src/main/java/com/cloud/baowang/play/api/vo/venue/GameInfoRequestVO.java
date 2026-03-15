package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author qiqi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "游戏信息分页列表参数对象")
public class GameInfoRequestVO extends PageVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "游戏ID")
    @Length(max = 5, message = ConstantsCode.PARAM_ERROR)
    private String gameId;

    @Schema(description = "站点后台游戏ID")
    private String gameNumberId;

    @Schema(description = "模糊-游戏名称")
    @Length(max = 12, message = ConstantsCode.PARAM_ERROR)
    private String gameName;

    @Schema(description = "精确-游戏名称")
    private String exactSearchContent;

    @Schema(description = "操作人")
    private String updater;


    @Schema(description = "操作人")
    private String siteUpdater;

    private List<String> ids;


    /**
     * 游戏CODE 接入参数
     */
    private List<String> gameCodeIds;


    private String gameI18nCode;

    /**
     * 多语言CODE查询
     */
    private List<String> gameI18nCodeList;

    /**
     * 游戏Ids
     */
//    private List<String> gameIds;

    @Schema(description = "显示状态 字典code:platform_class_status_type")
    private Integer status;

    @Schema(description = "显示状态 字典code:platform_class_status_type")
    private List<Integer> statusIds;

    @Schema(description = "游戏平台")
    private String venueCode;

    @Schema(description = "游戏平台ID")
    private String venueId;

    @Schema(description = "一级分类")
    private String gameOneId;

    @Schema(description = "二级分类")
    private String gameTwoId;

    @Schema(description = "二级分类")
    private List<String> gameTwoIds;


    @Schema(description = "标签字典code:game_label")
    private Integer label;

    @Schema(description = "角标:corner_labels")
    private Integer cornerLabels;

    /**
     * 平台集合
     */
    @Schema(description = "前端忽略该字段" ,hidden = true)
    private List<String> venueCodeIds;

    /**
     * 是否收藏
     */
    @Schema(description = "前端忽略该字段" ,hidden = true)
    private Boolean collection;


    /**
     * 登录用户的ID
     */
    @Schema(description = "前端忽略该字段" ,hidden = true)
    private String loginUserId;

    /**
     * 收藏用户ID,传了该字段则代表查出所有以收藏的游戏
     */
    @Schema(description = "前端忽略该字段" ,hidden = true)
    private String collectionUserId;

    /**
     * 顺序排序
     */
    @Schema(description = "前端忽略该字段" ,hidden = true)
    private Boolean asc;

    /**
     * 倒叙排序
     */
    @Schema(description = "前端忽略该字段" ,hidden = true)
    private Boolean desc;

    /**
     * 过滤指定游戏
     */
    @Schema(description = "前端忽略该字段" ,hidden = true)
    private List<String> notGameIds;

    @Schema(description = "设备" ,hidden = true)
    private Integer deviceType;

    @Schema(description = "接入参数" ,hidden = true)
    private List<String> accessParametersList;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "排序" ,hidden = true)
    private Integer sortStatus;


}