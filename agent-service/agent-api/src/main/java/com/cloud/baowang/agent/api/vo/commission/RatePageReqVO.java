package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/08/19 14:41
 * @description:
 */
@Data
@Schema(title = "代理佣金比例配置查询请求对象")
public class RatePageReqVO extends PageVO {
    @Schema(title = "站点Code", hidden = true)
    private String siteCode;
}
