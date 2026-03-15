package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author 小智
 * @Date 6/5/23 1:49 PM
 * @Version 1.0
 */
@Data
@TableName("site_vip_operation")
public class SiteVIPOperationPO extends BasePO implements Serializable {

    /* 操作类型 */
    private String operationType;

    /* 站点code */
    private String siteCode;

    /* 操作项 */
    private String operationItem;

    /* 操作前 */
    private String operationBefore;

    private String adjustLevel;

    /* 操作后 */
    private String operationAfter;

    /* 操作人 */
    private String operator;

    /* 操作时间 */
    private Long operationTime;
}
