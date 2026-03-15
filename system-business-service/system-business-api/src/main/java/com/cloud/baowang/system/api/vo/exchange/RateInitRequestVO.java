package com.cloud.baowang.system.api.vo.exchange;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "汇率初始化")
public class RateInitRequestVO {
    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "货币代码")
    private List<String> currencyCodeList;

    @Schema(description = "是否和总站一样 默认false",hidden = true)
    private boolean syncAdmin=false;
    @Schema(description = "是否是站点操作",hidden = true)
    private boolean siteFlag=false;
    @Schema(description = "操作人",hidden = true)
    private String operatorUserNo;

}

