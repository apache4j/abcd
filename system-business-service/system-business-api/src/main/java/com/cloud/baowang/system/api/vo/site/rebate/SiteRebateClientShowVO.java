package com.cloud.baowang.system.api.vo.site.rebate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Data
@Schema(description = "客户端返水展示查询vo")
@Builder
public class SiteRebateClientShowVO implements Serializable {

    private String siteCode;

    /** 等级 或者段位 code*/
    private Integer vipCode;

    private String currencyCode;
}
