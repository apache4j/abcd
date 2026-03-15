package com.cloud.baowang.report.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("report_user_deposit_withdraw")
public class ReportUserDepositWithdrawPO {

    /**
     * id
     */
    //@TableId(type = IdType.ASSIGN_ID)
    @TableId
    private String id;

    /**
     * 站点
     */
    private String siteCode;
    /**
     * 币种
     */
    private String currencyCode;
    /**
     * 日期
     */
    private Long day;


    /**
     * 存款人数
     */
    private Integer depositorsNums;

    /**
     * 存款次数
     */
    private Integer depositTimes;

    /**
     * 存款总金额
     */
    private BigDecimal depositTotalAmount;

    /**
     * 上级转入人数
     */
    private Integer depositSubordinatesNums;

    private Integer depositSubordinatesTimes;

    /**
     * 上级转入总额
     */
    private BigDecimal depositSubordinatesAmount;

    /**
     * 取款人数
     */
    private Integer withdrawalsNums;

    /**
     * 大额取款人数
     */
    private Integer bigMoneyWithdrawalsNums;

    /**
     * 大额取款次数
     */
    private Integer withdrawTimes;

    /**
     * 大额取款次数
     */
    private Integer bigMoneyWithdrawTimes;

    /**
     * 取款总金额
     */
    private BigDecimal withdrawTotalAmount;

    /**
     * 大额取款总金额
     */
    private BigDecimal bigMoneyWithdrawAmount;

    /**
     * 存取款差额
     */
    private BigDecimal depositWithdrawalDifference;

    @TableField(fill = FieldFill.INSERT, value = "created_time")
    private Long createdTime;

    /**
     * 备注
     */
    private String remark;

    public String getId(){
        if(!StringUtils.hasText(id)){
            this.id= SnowFlakeUtils.getSnowId();
        }
        return id;
    }
}
