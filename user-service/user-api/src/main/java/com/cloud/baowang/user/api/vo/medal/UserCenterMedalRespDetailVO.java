package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.cloud.baowang.user.api.enums.MedalLockStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption: 用户个人中心 topN
 * @Author: Ford
 * @Date: 2024/8/7 09:18
 * @Version: V1.0
 **/
@Data
@Schema(description = "用户个人中心TOP N勋章列表")
@I18nClass
public class UserCenterMedalRespDetailVO {
    @Schema(description = "展示顺序")
    private Integer sortNum;

    /**
     * 站点代码
     */
    @Schema(description = "站点代码")
    private String siteCode;

    @Schema(description = "会员ID")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;

    /**
     * 勋章代码
     */
    @Schema(description = "勋章代码")
    private String medalCode;

    /**
     * 勋章名称
     */
    @Schema(description = "勋章名称")
    private String medalName;

    /**
     * 解锁条件
     */
    @Schema(description = "解锁条件名称")
    private String unlockCondName;



    /**
     * 奖励金额
     */
    @Schema(description = "奖励金额")
    @JsonFormat(pattern = "0.00")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal rewardAmount;

    /**
     * 打码倍数
     */
    @Schema(description = "打码倍数")
    @JsonFormat(pattern = "0.00")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal typingMultiple;

    /**
     * 达成条件1 N
     */
    @Schema(description = "达成条件1 N")
    private String condNum1;


    /**
     * 达成条件2 N
     */
    @Schema(description = "达成条件2 N")
    private String condNum2;

    /**
     * 解锁条件说明
     */
    @Schema(description = "解锁条件说明")
    private String medalDesc;


    /**
     * 激活图片
     */
    @Schema(description = "激活图片")
    private String activatedPic;

    @Schema(description = "激活图片完整路径")
    private String activatedPicUrl;

    /**
     * 未激活图片
     */
    @Schema(description = "未激活图片")
    private String inactivatedPic;

    @Schema(description = "未激活图片完整路径")
    private String inactivatedPicUrl;

    /**
     *  解锁状态   CAN_UNLOCK(0,"可点亮"),
     *     HAS_UNLOCK(1,"已解锁"),
     *     NOT_UNLOCK(2,"未解锁")
     * see {@link MedalLockStatusEnum}
     */
    @Schema(description = "解锁状态 0:可点亮 1:已点亮 2:未获得")
    private Integer lockStatus;
    //排序顺序
    private Integer lockStatusSortNum;

    @Schema(description = "解锁状态名称")
    private String lockStatusName;



    /**
     * 达成条件时间
     */
    @Schema(description = "勋章达成条件时间戳")
    private Long completeTimeStamp;

    @Schema(description = "点亮勋章时间戳")
    private Long unlockTimeStamp;

    /**
     * 达成条件时间
     */
    @Schema(description = "勋章达成条件时间-按照站点时区格式化后的")
    private String completeTime;

    /**
     * 解锁时间
     */
    @Schema(description = "点亮勋章时间-按照站点时区格式化后的 ")
    private String unlockTime;


    /**
     * 勋章名称多语言
     */
   // @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    @I18nField
    @Schema(description = "勋章名称-多语言CODE")
    private String medalNameI18;

   // @Schema(description = "勋章名称-多语言集合")
   // private List<I18nMsgFrontVO> medalNameI18List;
    /**
     * 勋章描述多语言
     */
   // @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    @I18nField
    @Schema(description = "勋章描述-多语言CODE")
    private String medalDescI18;

    //@Schema(description = "勋章描述-多语言集合")
   // private List<I18nMsgFrontVO> medalDescI18List;



    public Integer getSortNum(){
        if(this.sortNum==null){
            return 1;
        }
        return this.sortNum;
    }


}
