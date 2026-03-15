package com.cloud.baowang.activity.po.v2;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.activity.api.enums.ActivityDeadLineEnum;
import com.cloud.baowang.activity.api.vo.ActivityBaseReqVO;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "site_activity_base_v2")
public class SiteActivityBaseV2PO extends BasePO implements Serializable {


    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 活动编号
     */
    private String activityNo;

    /**
     * 任务ID
     */
    private String xxlJobId;
    /**
     * 活动名称-多语言
     */
    private String activityNameI18nCode;

    /**
     * 活动分类-活动分类主键
     */
    private String labelId;

    /**
     * 活动时效-
     * ActivityDeadLineEnum
     */
    private Integer activityDeadline;

    /**
     * 活动开始时间
     */
    private Long activityStartTime;

    /**
     * 活动结束时间
     */
    private Long activityEndTime;

    /**
     * 活动图上架时间
     */
    private Long showStartTime;

    /**
     * 活动图下架时间
     */
    private Long showEndTime;

    /**
     * 活动模板-同system_param activity_template
     */
    private String activityTemplate;

    /**
     * 洗码倍率
     */
    private BigDecimal washRatio;

    /**
     * 活动生效的账户类型
     */
    private Integer accountType;


    /**
     * 活动参与终端
     */
    private String supportTerminal;

    /**
     * 活动展示终端
     */
    private String showTerminal;

    /**
     * 入口图-移动端
     */
    private String entrancePictureI18nCode;

    /**
     * 入口图-移动端-黑夜
     */
    private String entrancePictureBlackI18nCode;

    /**
     * 入口图-PC端
     */
    private String entrancePicturePcI18nCode;

    /**
     * 入口图-PC端-黑夜
     */
    private String entrancePicturePcBlackI18nCode;

    /**
     * 活动头图-移动端
     */
    private String headPictureI18nCode;

    /**
     * 活动头图-移动端-黑夜
     */
    private String headPictureBlackI18nCode;

    /**
     * 活动头图-PC端
     */
    private String headPicturePcI18nCode;

    /**
     * 活动头图-PC端-黑夜
     */
    private String headPicturePcBlackI18nCode;

    /**
     * 注册成功弹窗展示图(移动)
     */
    private String recommendTerminalsPicI18nCode;

    /**
     * 注册成功弹窗展示图(PC)
     */
    private String recommendTerminalsPicPcI18nCode;

    /**
     * 顺序
     */
    private Integer sort;

    /**
     * 状态 0已禁用 1开启中
     */
    private Integer status;

    /**
     * 完成手机号绑定才能参与: 0 - 关, 1 - 开
     */
    private Integer switchPhone;

    /**
     * 完成邮箱绑定才能参与: 0 - 关, 1 - 开
     */
    private Integer switchEmail;

    /**
     * 同登录IP只能1次: 0 - 关, 1 - 开
     */
    private Integer switchIp;

    /**
     * 活动规则,多语言
     */
    private String activityRuleI18nCode;

    /**
     * 活动描述,多语言
     */
    private String activityDescI18nCode;

    /**
     * 活动描述,多语言
     */
    private String activityIntroduceI18nCode;

    /**
     * 0删除，1存在
     */
    private Integer deleteFlag;

    @Schema(description = "是否展示 0 不展示，1 展示")
    private Integer showFlag = 1;

    /**
     * 禁用操作时间
     */
    private Long forbidTime;

    /**
     * 注册成功弹窗终端
     */
    private String recommendTerminals;

    /**
     * 是否推荐活动（0.不推荐。 1. 推荐）
     */
    private Boolean recommended;

    /**
     * 弹窗宣传图PC
     */
    private String picShowupPcI18nCode;

    /**
     * 弹窗宣传图APP
     */
    private String picShowupAppI18nCode;

    @Schema(title = "未登录首页浮动图标是否展示（0 不展示 1 展示）")
    private Boolean floatIconShowFlag;

    @Schema(title = "未登录首页浮动图标(移动端)-code")
    private String floatIconAppI18nCode;

    @Schema(title = "未登录首页浮动图标(PC端)-code")
    private String floatIconPcI18nCode;

    @Schema(title = "浮标排序 越大越靠前")
    private Integer floatIconSort;



    public static LambdaQueryWrapper<SiteActivityBaseV2PO> getQueryWrapper(ActivityBaseReqVO requestVO) {
        LambdaQueryWrapper<SiteActivityBaseV2PO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(ObjectUtil.isNotEmpty(requestVO.getId()), SiteActivityBaseV2PO::getId, requestVO.getId())
                .in(CollectionUtil.isNotEmpty(requestVO.getIds()), SiteActivityBaseV2PO::getId, requestVO.getIds())
                .eq(ObjectUtil.isNotEmpty(requestVO.getActivityNo()), SiteActivityBaseV2PO::getActivityNo, requestVO.getActivityNo())
                .eq(SiteActivityBaseV2PO::getDeleteFlag, EnableStatusEnum.ENABLE.getCode())
                .eq(ObjectUtil.isNotEmpty(requestVO.getSiteCode()), SiteActivityBaseV2PO::getSiteCode, requestVO.getSiteCode())
                .in(ObjectUtil.isNotEmpty(requestVO.getActivityNameCodeList()), SiteActivityBaseV2PO::getActivityNameI18nCode, requestVO.getActivityNameCodeList())
                .eq(ObjectUtil.isNotEmpty(requestVO.getLabelId()), SiteActivityBaseV2PO::getLabelId, requestVO.getLabelId())
                .eq(ObjectUtil.isNotEmpty(requestVO.getActivityTemplate()), SiteActivityBaseV2PO::getActivityTemplate, requestVO.getActivityTemplate())
                .eq(ObjectUtil.isNotEmpty(requestVO.getWashRatio()), SiteActivityBaseV2PO::getWashRatio, requestVO.getWashRatio())
                .eq(ObjectUtil.isNotEmpty(requestVO.getAccountType()), SiteActivityBaseV2PO::getAccountType, requestVO.getAccountType())
                .eq(ObjectUtil.isNotEmpty(requestVO.getStatus()), SiteActivityBaseV2PO::getStatus, requestVO.getStatus())
                .eq(ObjectUtil.isNotEmpty(requestVO.getOperator()), SiteActivityBaseV2PO::getUpdater, requestVO.getOperator())
                .eq(ObjectUtil.isNotEmpty(requestVO.getCreator()), SiteActivityBaseV2PO::getCreator, requestVO.getCreator())
                .le(ObjectUtil.isNotEmpty(requestVO.getShowStartTime()), SiteActivityBaseV2PO::getShowStartTime, requestVO.getShowStartTime())
                .ge(ObjectUtil.isNotEmpty(requestVO.getShowEndTime()), SiteActivityBaseV2PO::getShowEndTime, requestVO.getShowEndTime())
                .like(ObjectUtil.isNotEmpty(requestVO.getRecommendTerminals()), SiteActivityBaseV2PO::getRecommendTerminals, requestVO.getRecommendTerminals())

                .orderByAsc(SiteActivityBaseV2PO::getSort)
                .orderByDesc(SiteActivityBaseV2PO::getUpdatedTime)  // 按更新时间倒序排序
                .orderByDesc(SiteActivityBaseV2PO::getCreatedTime);  // 默认按创建时间倒序作为次要排序

        Integer activityDeadline = requestVO.getActivityDeadline();
        queryWrapper.eq(ObjectUtil.isNotEmpty(requestVO.getActivityDeadline()), SiteActivityBaseV2PO::getActivityDeadline, activityDeadline);

        //活动时效 0-限时，1-长期,只有在没传这个字段或者传限时的时候才会有时间限制
        if (ObjectUtil.isEmpty(activityDeadline) || activityDeadline.equals(ActivityDeadLineEnum.LIMITED_TIME.getType())) {
            queryWrapper.le(ObjectUtil.isNotEmpty(requestVO.getActivityStartTime()), SiteActivityBaseV2PO::getActivityStartTime, requestVO.getActivityStartTime());
            queryWrapper.ge(ObjectUtil.isNotEmpty(requestVO.getActivityEndTime()), SiteActivityBaseV2PO::getActivityEndTime, requestVO.getActivityEndTime());
        }

        if (ObjectUtil.isNotEmpty(requestVO.getShowTerminal())) {
            queryWrapper.apply("FIND_IN_SET(" + requestVO.getShowTerminal() + ", show_terminal)");
        }

        return queryWrapper;
    }


}
