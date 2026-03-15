package com.cloud.baowang.agent.api.vo.agentreview.info;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "代理短链接信息变更记录分页查询返回实体")
public class ShortUrlChangeRecordPageVO {
   @Schema(description ="账号")
    private String agentAccount;

    /**
     * 创建人与更新人
     */
   @Schema(description ="操作人/创建人")
    private String operator;


   @Schema(description ="短链接")
    private String shortUrl;

   @Schema(description ="短链接after")
    private String shortAfter;

   @Schema(description ="短链接数量")
    private Long count;

   @Schema(description ="操作时间")
    private Long operatorTime;



}
