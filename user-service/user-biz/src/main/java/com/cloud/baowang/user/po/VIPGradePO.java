package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 2024/8/2 18:03
 * @Version : 1.0
 */
@Data
@TableName("vip_grade")
public class VIPGradePO extends BasePO implements Serializable {

    /* VIP等级code */
    private Integer vipGradeCode;

    /* VIP等级名称 */
    private String vipGradeName;
    /**
     * 默认vip段位code
     */
    private Integer defaultRankCode;
}
