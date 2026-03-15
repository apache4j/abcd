package com.cloud.baowang.activity.api.vo.task;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @className: ReportTaskOrderRecordPO
 * @author: wade
 * @description: 任务记录报表
 * @date: 4/11/24 09:14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "一致性查询生成数据相应")
public class TaskOrderRecordListResVO implements Serializable {



    /**
     * 逐渐
     */
    private String id;

    /**
     * 订单
     */
    private String orderNo;



}
