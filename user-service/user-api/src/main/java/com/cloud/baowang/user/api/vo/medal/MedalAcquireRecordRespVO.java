package com.cloud.baowang.user.api.vo.medal;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.user.api.enums.MedalLockStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/31 15:57
 * @Version: V1.0
 **/
@Data
@Schema(description = "勋章获取记录结果")
@I18nClass
@ExcelIgnoreUnannotated
public class MedalAcquireRecordRespVO {

    @Schema(description = "Id")
    private String id;

    /**
     * 站点代码
     */
    @Schema(description = "站点代码")
    private String siteCode;

    @Schema(description = "会员ID")
    private String userId;

    @Schema(description = "会员账号")
    @ExcelProperty("会员账号")
    private String userAccount;

    @Schema(description = "主货币")
    @ExcelProperty("主货币")
    private String mainCurrency;

    @Schema(description = "上级代理id")
    private String superAgentId;


    @Schema(description = "vip等级名称")
    @ExcelProperty("vip等级")
    private String vipGradeName;

    @Schema(description = "上级代理账号")
    @ExcelProperty("上级代理")
    private String superAgentAccount;

    @Schema(description = "vip当前等级")
    private Integer vipGradeCode;


    /**
     * 勋章编号
     */
    @Schema(description = "勋章编号")
    private Long medalId;

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
    @Schema(description = "勋章名称多语言")
    @I18nField
    @ExcelProperty("勋章名称")
    private String medalNameI18;

    /**
     * 奖励金额
     */
    @Schema(description = "奖励金额")
    @ExcelProperty("奖励金额")
    private String rewardAmount;
    /**
     * 达成条件时间
     */
    @Schema(description = "达成条件时间")
    private Long completeTime;

    @ExcelProperty("达成条件时间")
    @Schema(description = "达成条件时间")
    private String completeTimeStr;

    public String getCompleteTimeStr() {
        if(completeTime!=null){
            return DateUtils.formatDateByZoneId(completeTime,DateUtils.FULL_FORMAT_1, CurrReqUtils.getTimezone());
        }
        return "";
    }

    /**
     * 解锁时间
     */
    @Schema(description = "解锁时间")
    private Long unlockTime;

    @Schema(description = "解锁时间")
    @ExcelProperty("解锁时间")
    private String unlockTimeStr;

    public String getUnlockTimeStr() {
        if(unlockTime!=null){
            return DateUtils.formatDateByZoneId(unlockTime,DateUtils.FULL_FORMAT_1, CurrReqUtils.getTimezone());
        }
        return "";
    }

    /**
     * 打码倍数
     */
    @Schema(description = "打码倍数")
    private BigDecimal typingMultiple;

    /**
     * 达成条件 N
     */
    @Schema(description = "达成条件 N")
    private Integer condNum;

    /**
     * 解锁条件说明
     */
    @Schema(description = "解锁条件说明")
    private String medalDesc;

    /**
     *  解锁状态   CAN_UNLOCK(0,"可点亮"),
     *     HAS_UNLOCK(1,"已解锁"),
     *     NOT_UNLOCK(2,"未解锁")
     * see {@link MedalLockStatusEnum}
     */
    private Integer lockStatus;



    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建时间")
    private Long createdTime;

    @Schema(description = "修改人")
    private String updater;

    @Schema(description = "修改时间")
    private Long updatedTime;
}
