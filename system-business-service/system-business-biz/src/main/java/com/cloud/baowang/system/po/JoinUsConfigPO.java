package com.cloud.baowang.system.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : 小智
 * @Date : 6/6/23 2:20 PM
 * @Version : 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("join_us_config")
public class JoinUsConfigPO extends BasePO {

    /* 类型type */
    private String addType;

    /* 类型type名称 */
    private String addTypeName;

    /* 地址 */
    private String address;

    /* 备注 */
    private String remark;

    /* 最近操作人 */
    private String operator;

    /* 最近操作时间 */
    private Long operatorTime;
}
