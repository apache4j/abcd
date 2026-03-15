package com.cloud.baowang.system.po.site.rebate;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_rebate_venue_record")
public class UserRebateVenueRecordPO  {
    private String id;
    private String siteCode;
    private String userAccount;
    private String userId;
    private String orderNo;
    private BigDecimal validAmount;
    //返水金额
    private BigDecimal rebateAmount;
    private BigDecimal rebatePercent;
    //实际领取返水金额
    private BigDecimal actRebateAmount;
    private String venueType;
    private String currencyCode;
    //状态 0:拒绝 1:派发,2领取,3过期
    private Integer status;
    private Long createdTime;

    //发放日期
    private Long issueTime;

    //领取日期
    private Long receiveTime;

    //归属日期
    private long dayMillis;
    //归属日期字符串 年月日
    private String dayStr;

}
