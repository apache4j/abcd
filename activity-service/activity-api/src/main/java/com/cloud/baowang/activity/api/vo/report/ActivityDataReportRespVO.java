package com.cloud.baowang.activity.api.vo.report;

import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/3 13:26
 * @Version: V1.0
 **/
@Data
@Schema(description = "活动数据报表结果")
@I18nClass
public class ActivityDataReportRespVO {
    @Schema(description = "站点编码")
    @ExcelProperty("站点编码")
    private String siteCode;
    @Schema(description = "统计日期 yyyy-MM-dd ")
    @ExcelProperty("统计日期")
    private String staticDate;
    @Schema(description = "活动名称")
    @ExcelProperty("活动名称")
    @I18nField
    private String activityName;
    /**
     * 活动模板-同system_param activity_template
     */
    @Schema(title = "活动模板-同system_param activity_template")
    private String activityTemplate;

    @ExcelProperty("活动模板名称")
    private String activityTemplateText;

    @Schema(description = "活动ID")
    @ExcelProperty("活动ID")
    private String  activityNo;
    //@Schema(description = "币种")
   // @ExcelProperty("币种")
   // private String currencyCode;
    @Schema(description = "发放次数的人数")
    @ExcelProperty("发放次数的人数")
    private Long  sendMemberNum;
    @Schema(description = "发放彩金金额")
    @ExcelProperty("发放彩金金额")
    private BigDecimal totalAmount;
    @Schema(description = "发放转盘旋转次数")
    @ExcelProperty("发放转盘旋转次数")
    private Long acquireSpinWheelNum;
    @Schema(description = "发放免费旋转次数")
    @ExcelProperty("发放免费旋转次数")
    private Long acquireWheelNum;


    @Schema(description = "参与人数")
    @ExcelProperty("参与人数")
    private Long  eventMemberNum;
    @Schema(description = "已领取人数")
    @ExcelProperty("已领取人数")
    private Long recvMemberNum;
    @Schema(description = "已领取彩金金额")
    @ExcelProperty("已领取彩金金额")
    private BigDecimal recvAmount;
   // @Schema(description = "未领取人数")
  //  @ExcelProperty("未领取人数")
  //  private Long unRecvMemberNum;
   // @Schema(description = "未领取彩金金额")
   // @ExcelProperty("未领取彩金金额")
   // private BigDecimal unRecvTotalAmount;


    public ActivityDataReportRespVO addTotalAmount(BigDecimal totalAmount){
        BigDecimal resultAmount = this.getTotalAmount()==null?BigDecimal.ZERO:this.getTotalAmount();
        totalAmount = totalAmount==null?BigDecimal.ZERO:totalAmount;
        resultAmount=resultAmount.add(totalAmount);
        this.totalAmount=resultAmount;
        return this;
    }

    public ActivityDataReportRespVO addRecvAmount(BigDecimal recvAmount){
        BigDecimal resultAmount = this.getRecvAmount()==null?BigDecimal.ZERO:this.getRecvAmount();
        recvAmount = recvAmount==null?BigDecimal.ZERO:recvAmount;
        resultAmount=resultAmount.add(recvAmount);
        this.recvAmount=resultAmount;
        return this;
    }

    public ActivityDataReportRespVO addAcquireWheelNum(Long acquireWheelNum){
        Long resultNum = this.getAcquireWheelNum()==null?0:this.getAcquireWheelNum();
        acquireWheelNum=acquireWheelNum==null?0:acquireWheelNum;
        resultNum=resultNum+acquireWheelNum;
        this.acquireWheelNum=resultNum;
        return this;
    }

    public ActivityDataReportRespVO addAcquireSpinWheelNum(Long acquireSpinWheelNum){
        Long resultNum = this.getAcquireSpinWheelNum()==null?0:this.getAcquireSpinWheelNum();
        acquireSpinWheelNum=acquireSpinWheelNum==null?0:acquireSpinWheelNum;
        resultNum=resultNum+acquireSpinWheelNum;
        this.acquireSpinWheelNum=resultNum;
        return this;
    }

    public ActivityDataReportRespVO addRecvMemberNum(Long recvMemberNum){
        Long resultNum = this.getRecvMemberNum()==null?0:this.getRecvMemberNum();
        recvMemberNum=recvMemberNum==null?0:recvMemberNum;
        resultNum=resultNum+recvMemberNum;
        this.recvMemberNum=resultNum;
        return this;
    }



    /*public ActivityDataReportRespVO addUnRecvTotalAmount(BigDecimal unRecvTotalAmount){
        BigDecimal  resulAmount= this.getUnRecvTotalAmount()==null?BigDecimal.ZERO:this.getUnRecvTotalAmount();
        unRecvTotalAmount=unRecvTotalAmount==null?BigDecimal.ZERO:unRecvTotalAmount;
        resulAmount=resulAmount.add(unRecvTotalAmount);
        this.unRecvTotalAmount=resulAmount;
        return this;
    }*/

}
