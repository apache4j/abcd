package com.cloud.baowang.activity.api.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(title = "转盘客户端详情-请求入参")
public class APPTaskReqVO implements Serializable {


    @Schema(title = "站点code", hidden = true)
    private String siteCode;

    @Schema(title = "会员id", hidden = true)
    private String userId;

    @Schema(title = "timeZone", hidden = true)
    private String timeZone;

    @Schema(title = "展示终端", hidden = true)
    private String showTerminal;





}
