package com.cloud.baowang.agent.api.vo.agentreview.info;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "代理短链接信息变更记录分页查询条件入参")
public class ShortUrlChangeRecordQueryVO extends PageVO {


    @Schema(description = "代理账号")
    private String agentAccount;


    @Schema(description = "操作人")
    private String operator;

    @Schema(description = "短链接模糊查询")
    private String shortUrl;
}
