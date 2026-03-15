package com.cloud.baowang.user.api.vo.medal;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
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
@Schema(description = "宝箱获取记录结果")
@I18nClass
@ExcelIgnoreUnannotated
public class MedalRewardRecordRespVO {

    @Schema(description = "Id")
    private String id;
    /**
     * 站点代码
     */
    @Schema(description = "站点代码")
    private String siteCode;

    @Schema(description = "领取订单号")
    @ExcelProperty("订单号")
    private String orderNo;


    @Schema(description = "会员账号")
    @ExcelProperty("会员账号")
    private String userAccount;

  //  @Schema(description = "主货币")
   // @ExcelProperty("主货币")
   // private String mainCurrency;

  //  @Schema(description = "上级代理id")
   // private String superAgentId;

  //  @Schema(description = "上级代理账号")
    //@ExcelProperty("上级代理")
  //  private String superAgentAccount;

  //  @Schema(description = "vip当前等级")
  //  private Integer vipGradeCode;

   // @Schema(description = "vip等级名称")
   // @ExcelProperty("vip等级")
  //  private String vipGradeName;
   @Schema(description = "宝箱编号")
   @ExcelProperty("宝箱编号")
   private Integer rewardNo;
    /**
     * 达成数量
     */
    @Schema(description = "达到勋章数")
    @ExcelProperty("达到勋章数")
    private Integer condNum;




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

    @Schema(description = "领取时间")
    private Long openTime;

    @ExcelProperty("领取时间")
    @Schema(description = "领取时间str")
    private String openTimeStr;

    /**
     * 打码倍数
     */
    @Schema(description = "打码倍数")
    private BigDecimal typingMultiple;

    /**
     * 解锁状态 0:可打开 1:已打开
     */
    @Schema(description = "解锁状态 0:可打开 1:已打开")
    private Integer openStatus;


    public String getCompleteTimeStr() {
        if(completeTime!=null){
            return DateUtils.formatDateByZoneId(completeTime,DateUtils.FULL_FORMAT_1, CurrReqUtils.getTimezone());
        }
        return "";
    }


    public String getOpenTimeStr() {
        if(this.openTime!=null){
            return DateUtils.formatDateByZoneId(getOpenTime(),DateUtils.FULL_FORMAT_1, CurrReqUtils.getTimezone());
        }
        return "";
    }


    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建时间")
    private Long createdTime;

    @Schema(description = "修改人")
    private String updater;

    @Schema(description = "修改时间")
    private Long updatedTime;
}
