package com.cloud.baowang.wallet.api.vo.rebate;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.utils.TimeVoUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 26/6/23 11:23 AM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title ="VIP奖励发放记录分页对象")
@ExcelIgnoreUnannotated
public class VIPAwardPageVO implements Serializable {

    @Schema(title ="VIP奖励发放订单号")
    @ExcelProperty("VIP奖励发放订单号")
    @ColumnWidth(25)
    private String orderId;

    @Schema(title ="订单生成时间")
    private Long createTime;


    @ExcelProperty("订单生成时间")
    @ColumnWidth(25)
    @Schema(title ="订单生成时间")
    private String createTimeStr;
    public String getCreateTimeStr() {
        return TimeVoUtil.getyyyyMMddHHmmss(createTime);
    }
    @Schema(title ="领取时间")
    private Long receiveTime;

    @ExcelProperty("领取时间")
    @ColumnWidth(25)
    @Schema(title ="领取时间(导出使用)")
    private String receiveTimeStr;
    public String getReceiveTimeStr() {
        return TimeVoUtil.getyyyyMMddHHmmss(receiveTime);
    }
    @Schema(title ="奖励类型")
    private String awardType;

    @Schema(title ="奖励类型名称")
    @ExcelProperty("奖励类型名称")
    @ColumnWidth(20)
    private String awardName;

    @ExcelProperty("奖励金额")
    @ColumnWidth(20)
    @Schema(title ="奖励金额")
    private BigDecimal awardAmount;


    @Schema(title ="领取状态")
    private String receiveStatus;

    @ExcelProperty("领取状态")
    @ColumnWidth(20)
    @Schema(title ="领取状态名称")
    private String receiveStatusName;

    @ExcelProperty("会员账号")
    @ColumnWidth(20)
    @Schema(title ="会员账号")
    private String userAccount;

    @ExcelProperty("VIP等级")
    @ColumnWidth(20)
    @Schema(title ="VIP等级")
    private Integer vipRankCode;

    @Schema(title ="账号类型")
    private String accountType;

    @ExcelProperty("账号类型")
    @ColumnWidth(20)
    @Schema(title ="账号类型名称")
    private String accountTypeName;
}
