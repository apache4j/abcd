package com.cloud.baowang.system.po.site.rebate;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_rebate_record")
public class UserRebateRecordPO extends BasePO  {

    private String siteCode;


    private String userAccount;
    private String userId;
    private String vipRankCode;

    private String vipRankName;

    private String orderNo;

    //有效投注
  private BigDecimal validAmount;
  //返水金额
    private BigDecimal rebateAmount;

    //计算后预计返水金额
    private BigDecimal expectRebateAmount;

    private String currencyCode;

    //审核人
    private String auditAccount;

    private String auditRemark;
    //审核状态（1-待审核 2-审核中，3-已派发，4-审核拒绝）
    private Integer orderStatus;

    //审核操作1.一审审核，2.结单查看
    private Integer reviewOperation;

    private Integer lockStatus;

    private String locker;

    //审核时间
    private Long auditTime;
    //锁单时间
    private Long lockTime;


    //归属日期
    private long dayMillis;
    //归属日期字符串 年月日
    private String dayStr;

}
