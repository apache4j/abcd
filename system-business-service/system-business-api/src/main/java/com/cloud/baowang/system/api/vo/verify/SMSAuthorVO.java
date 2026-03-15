package com.cloud.baowang.system.api.vo.verify;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通道发送统计表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title ="短信通道绑定对象")
public class SMSAuthorVO {

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "站点名称")
    private String siteName;

    @Schema(description = "备注")
    private String remark;


}
