package com.cloud.baowang.wallet.api.vo.siteSecurity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2025/6/27 17:22
 * @Version: V1.0
 **/
@Data
@Schema(title ="保证金初始化接口")
public class SiteSecurityBalanceInitReqVO {

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 站点名称
     */
    private String company;
    /**
     * 站点类型
     */
    private Integer siteType;

    /**
     * 保证金开启状态 0:未开启 1:已开启
     */
    private Integer securityStatus;

    /**
     * 操作人
     */
    private String operatorUser;


}
