package com.cloud.baowang.report.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 2024/11/4 18:27
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("report_vip_info")
public class ReportVIPInfoPO extends BasePO {

    private Long dateShow;

    private String siteCode;

    private Integer vipRankCode;

    private Integer vipGradeCode;

    private Integer currentGradeNum;

    private Integer achieveGradeNum;

    private BigDecimal receiveBonus;
}
