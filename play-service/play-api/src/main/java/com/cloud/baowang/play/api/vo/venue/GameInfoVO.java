package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "游戏信息返回对象")
@I18nClass
public class GameInfoVO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "游戏名称")
    private String gameName;

    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    @Schema(description = "游戏名称-多语言CODE")
    private String gameI18nCode;

    @Schema(description = "游戏名称-多语言集合")
    private List<I18nMsgFrontVO> gameI18nCodeList;

    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    @Schema(description = "游戏图多语言CODE")
    private String iconI18nCode;

    @Schema(description = "游戏图-多语言")
    private List<I18nMsgFrontVO> iconI18nCodeList;

    @I18nField(type = I18nFieldTypeConstants.FILE)
    @Schema(description = "图片")
    private String icon;


    @Schema(title = "图片")
    private String iconFileUrl;

    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    @Schema(description = "多语言-正方形-游戏图片")
    private String seIconI18nCode;

    @Schema(description = "多语言-正方形-游戏图片", required = true)
    private List<I18nMsgFrontVO> seIconI18nCodeList;

    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    @Schema(description = "多语言-竖版-游戏图片")
    private String vtIconI18nCode;

    @Schema(description = "多语言-竖版-游戏图片", required = true)
    private List<I18nMsgFrontVO> vtIconI18nCodeList;

    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    @Schema(description = "多语言-横版-游戏图片")
    private String htIconI18nCode;

    @Schema(description = "多语言-横版-游戏图片", required = true)
    private List<I18nMsgFrontVO> htIconI18nCodeList;


    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    @Schema(description = "多语言游戏描述CODE")
    private String gameDescI18nCode;

    @Schema(description = "多语言-游戏描述", required = true)
    private List<I18nMsgFrontVO> gameDescI18nCodeList;

    @Schema(description = "游戏Id,该gameId只能做查询显示。当涉及到 编辑删除功能时需要用 id字段")
    private String gameId;

    @Schema(description = "游戏平台")
    @I18nField
    private String venueName;

    @Schema(description = "游戏平台Code")
    private String venueCode;

    @Schema(description = "游戏平台Id")
    private String venueId;

    @Schema(description = "一级分类名称")
    private String gameOneClassName;

    @Schema(description = "二级分类名称")
    private String gameTwoClassName;

    @Schema(description = "二级分类ID")
    private String gameTwoId;

    @Schema(description = "标签ID 字典CODE:game_labels")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.GAME_LABEL)
    private Integer label;

    @Schema(title = "标签多语言")
    private String labelText;

//    @Schema(description = "支持终端 支持多个")
//    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TERMINAL)
//    private String supportDevice;
//
//    @Schema(description = "支持终端名称")
//    private String supportDeviceText;

    @Schema(description = "状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_CLASS_STATUS_TYPE)
    private Integer status;

    @Schema(description = "多语言-状态名称")
    private String statusText;

    @Schema(description = "接入参数")
    private String accessParameters;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "角标 字典CODE:corner_labels")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.CORNER_LABELS)
    private Integer cornerLabels;

    @Schema(description = "角标多语言")
    private String cornerLabelsText;

    @Schema(description = "维护开始时间")
    private Long maintenanceStartTime;

    @Schema(description = "维护结束时间")
    private Long maintenanceEndTime;

    @Schema(description = "更新时间")
    private Long updatedTime;

    @Schema(description = "创建时间")
    private Long createdTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "新增操作人")
    private String creator;

    @Schema(description = "修改操作人")
    private String updater;

    @Schema(description = "首页 - 热门排序")
    private Long homeHotSort;

    @Schema(description = "一级分类-首页游戏-排序")
    private Long gameOneHomeSort;

    @Schema(description = "首页 - 一级分类-热门排序")
    private Long gameOneHotSort;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "场馆币种")
    private String venueCurrencyCode;


    @Schema(description = "二级分类排序",hidden = true)
    private Long classTwoSort;

    @Schema(description = "二级分类图片",hidden = true)
    private String classTwoIcon;


    @Schema(description = "二级分类横图片",hidden = true)
    private String classTwoHtIconI18nCode;


    @Schema(description = "一级分类",hidden = true)
    private String gameOneId;


    @Schema(description = "冠名标签")
    private Integer siteLabelChangeType;

}
