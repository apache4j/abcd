package com.cloud.baowang.wallet.api.vo.fundrecord;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(title = "会员充值审核记录-列表 返回")
@ExcelIgnoreUnannotated
public class GetUserRechargeRecordResponseVO {

    @Schema(title = "id")
    private Long id;

    @Schema(title = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(25)
    private String orderNo;

    @Schema(title = "会员账号")
    @ExcelProperty("会员账号")
    @ColumnWidth(15)
    private String userAccount;

    @Schema(title = "会员注册信息")
    @ExcelProperty("会员注册信息")
    private String userRegister;


    @Schema(title = "存款人姓名")
    //@ExcelProperty("存款人姓名")
    //@ColumnWidth(15)
    private String userName;

    @Schema(title = "订单状态")
    private String status;
    @Schema(title = "订单状态-Name")
    @ExcelProperty("订单状态")
    @ColumnWidth(15)
    private String statusName;

    @Schema(title = "充值金额")
    @ExcelProperty("充值金额")
    @ColumnWidth(15)
    private BigDecimal applyAmount;

    @Schema(title = "实际到账金额")
    @ExcelProperty("实际到账金额")
    @ColumnWidth(15)
    private BigDecimal arriveAmount;

    @Schema(title = "申请时间")
    private Long applyTime;
    @Schema(title = "申请时间 - 导出")
    @ExcelProperty("申请时间")
    @ColumnWidth(20)
    private String applyTimeExport;

    @Schema(title = "一审人")
    @ExcelProperty("审核人")
    @ColumnWidth(15)
    private String oneReviewer;

    @Schema(title = "入款人")
    //@ExcelProperty("入款人")
    //@ColumnWidth(15)
    private String twoReviewer;

    @Schema(title = "一审完成时间")
    private Long oneReviewFinishTime;
    @Schema(title = "一审完成时间 - 导出")
    @ExcelProperty("审核时间")
    @ColumnWidth(20)
    private String oneReviewFinishTimeExport;

    @Schema(title = "入款完成时间")
    private Long twoReviewFinishTime;
    @Schema(title = "入款完成时间 - 导出")
    //@ExcelProperty("入款完成时间")
    //@ColumnWidth(20)
    private String twoReviewFinishTimeExport;

    @Schema(title = "一审审核用时")
    @ExcelProperty("审核用时")
    @ColumnWidth(10)
    private String oneReviewUseTime;
    @Schema(title = "入款审核用时")
    //@ExcelProperty("入款审核用时")
    //@ColumnWidth(10)
    private String twoReviewUseTime;

    @Schema(title = "申请附件1")
    private String applyFile;
    @Schema(title = "申请附件2-多个")
    private String cashFlowFile;
    @Schema(title = "充值资料")
    private List<String> remarkFiles;

    @Schema(title = "备注")
    @ExcelProperty("备注")
    @ColumnWidth(10)
    private String remark;

    /**
     * 一审审核信息
     */
    private String firstAuditInfo;

    /**
     * 二审审核信息
     */
    private String SecondAuditInfo;
}
