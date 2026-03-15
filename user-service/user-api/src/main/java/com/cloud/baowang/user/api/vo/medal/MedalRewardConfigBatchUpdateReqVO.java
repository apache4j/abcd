package com.cloud.baowang.user.api.vo.medal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 09:52
 * @Version: V1.0
 **/
@Data
@Schema(description = "勋章奖励配置批量修改参数")
public class MedalRewardConfigBatchUpdateReqVO {
    @Schema(description = "站点代码",hidden = true)
    private String siteCode;

    @Schema(description = "操作人 ",hidden = true)
    private String operatorUserNo;

    @Schema(description = "勋章奖励配置单笔修改参数")
   private List<MedalRewardConfigUpdateReqVO> medalRewardConfigUpdateReqVOList;

}
