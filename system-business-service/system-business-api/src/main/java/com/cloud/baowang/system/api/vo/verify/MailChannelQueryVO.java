package com.cloud.baowang.system.api.vo.verify;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 15:47
 * @description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "查询邮箱通道列表接参对象")
public class MailChannelQueryVO extends PageVO implements Serializable {
    @Schema(title = "通道名称")
    private String channelName;
    @Schema(title = "通道代码")
    private String channelCode;
    @Schema(title = "状态 1 禁用 0 启用")
    private String status;
    @Schema(title = "站点code 站点后台查询不需要传值")
    private String siteCode;
}
