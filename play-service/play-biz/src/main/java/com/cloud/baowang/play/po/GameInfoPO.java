package com.cloud.baowang.play.po;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.play.api.vo.venue.GameInfoRequestVO;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author qiqi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("game_info")
public class GameInfoPO extends BasePO implements Serializable {


    private String gameId;

    /**
     * 游戏名称
     */
    private String gameName;

    /**
     * 游戏名称-多语言
     */
    private String gameI18nCode;

    /**
     * 游戏平台ID
     */
    private String venueId;

    /**
     * 场馆类型 1:体育,2:视讯,3:棋牌,4:电子,5:彩票,6:斗鸡,7:电竞
     */
    private Integer venueType;

    /**
     * 游戏平台CODE
     */
    private String venueCode;

    /**
     * 游戏平台名称
     */
    private String venueName;

    /**
     * 默认图片
     */
    private String icon;

    /**
     * 状态（ 状态:1:开启中,2:维护中,3:已禁用）
     */
    private Integer status;

    /**
     * 支持终端
     */
    private String supportDevice;

//    /**
//     * 标签 0:无 2:热门推荐 3:喜欢的游戏
//     */
//    private Integer label;
//
//    /**
//     * 角标 0:无 2:NEW 3:HOT
//     */
//    private Integer cornerLabels;

    /**
     * 多语言-游戏图片
     */
    private String iconI18nCode;

    /**
     * 多语言-游戏图片-正方形
     */
    private String seIconI18nCode;

    /**
     * 多语言-游戏图片-竖版
     */
    private String vtIconI18nCode;

    /**
     * 多语言-游戏图片-横版
     */
    private String htIconI18nCode;

    /**
     * 游戏描述
     */
    private String gameDesc;

    /**
     * 是否配置返水
     */
    private Integer isRebate;

    /**
     * 接入参数
     */
    private String accessParameters;

    /**
     * 备注
     */
    private String remark;

    /**
     * 游戏描述-多语言
     */
    private String gameDescI18nCode;

    /**
     * 维护开始时间
     */
    private Long maintenanceStartTime;

    /**
     * 维护结束时间
     */
    private Long maintenanceEndTime;

    /**
     * 支持币种
     */
    private String currencyCode;


    public static LambdaQueryWrapper<GameInfoPO> getQueryWrapper(GameInfoRequestVO requestVO) {
        LambdaQueryWrapper<GameInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .in(CollectionUtil.isNotEmpty(requestVO.getIds()), GameInfoPO::getId, requestVO.getIds())
                .eq(ObjectUtil.isNotEmpty(requestVO.getGameNumberId()), GameInfoPO::getGameId, requestVO.getGameNumberId())
                .eq(ObjectUtil.isNotEmpty(requestVO.getId()), GameInfoPO::getId, requestVO.getId())
                .like(!StringUtils.isBlank(requestVO.getGameName()), GameInfoPO::getGameName, requestVO.getGameName())
                .eq(ObjectUtil.isNotEmpty(requestVO.getStatus()), GameInfoPO::getStatus, requestVO.getStatus())
                .in(CollectionUtil.isNotEmpty(requestVO.getStatusIds()), GameInfoPO::getStatus, requestVO.getStatusIds())
                .eq(ObjectUtil.isNotEmpty(requestVO.getVenueCode()), GameInfoPO::getVenueCode, requestVO.getVenueCode())
                .in(CollectionUtil.isNotEmpty(requestVO.getVenueCodeIds()), GameInfoPO::getVenueCode, requestVO.getVenueCodeIds())
                .orderByAsc(ObjectUtil.isNotEmpty(requestVO.getAsc()) && requestVO.getAsc(), GameInfoPO::getGameName)
                .orderByDesc(ObjectUtil.isNotEmpty(requestVO.getDesc()) && requestVO.getDesc(), GameInfoPO::getGameName)
                .in(CollectionUtil.isNotEmpty(requestVO.getGameCodeIds()), GameInfoPO::getAccessParameters, requestVO.getGameCodeIds())
                .notIn(CollectionUtil.isNotEmpty(requestVO.getNotGameIds()), GameInfoPO::getId, requestVO.getNotGameIds())
                .in(ObjectUtil.isNotEmpty(requestVO.getVenueId()), GameInfoPO::getVenueId, requestVO.getVenueId())
                .eq(ObjectUtil.isNotEmpty(requestVO.getUpdater()), GameInfoPO::getUpdater, requestVO.getUpdater())
                .like(ObjectUtil.isNotEmpty(requestVO.getCurrencyCode()), GameInfoPO::getCurrencyCode, requestVO.getCurrencyCode())
                .in(CollectionUtil.isNotEmpty(requestVO.getGameI18nCodeList()), GameInfoPO::getGameI18nCode, requestVO.getGameI18nCodeList())
                .orderByAsc(GameInfoPO::getStatus);

        if (StringUtils.isNotBlank(requestVO.getOrderField())
                && StringUtils.isNotBlank(requestVO.getOrderType())) {
            if (requestVO.getOrderField().equals("createdTime") && requestVO.getOrderType().equals("asc")) {
                queryWrapper.orderByAsc(GameInfoPO::getCreatedTime);
            }
            if (requestVO.getOrderField().equals("createdTime") && requestVO.getOrderType().equals("desc")) {
                queryWrapper.orderByDesc(GameInfoPO::getCreatedTime);
            }
            if (requestVO.getOrderField().equals("updatedTime") && requestVO.getOrderType().equals("asc")) {
                queryWrapper.orderByAsc(GameInfoPO::getUpdatedTime);
            }
            if (requestVO.getOrderField().equals("updatedTime") && requestVO.getOrderType().equals("desc")) {
                queryWrapper.orderByDesc(GameInfoPO::getUpdatedTime);
            }
        }else{
            queryWrapper.orderByDesc(GameInfoPO::getCreatedTime);
        }

        if (ObjectUtil.isNotEmpty(requestVO.getDeviceType())) {
            queryWrapper.apply("FIND_IN_SET(" + requestVO.getDeviceType() + ", support_device)");
        }

        return queryWrapper;
    }
}
