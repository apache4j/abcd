package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 09:52
 * @Version: V1.0
 **/
@Data
@Schema(description = "勋章奖励配置查询条件")
public class MedalRewardConfigReqVO extends PageVO {

    /**
     * 站点代码
     */
    @Schema(description = "站点代码")
    private String siteCode;

}
