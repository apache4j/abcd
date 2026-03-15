package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/31 15:52
 * @Version: V1.0
 **/
@Data
@Schema(description = "宝箱获取记录查询条件")
public class MedalRewardRecordReqVO extends PageVO {

    @Schema(description = "达成条件开始时间")
    private Long completeTimeStart;
    @Schema(description = "达成条件结束时间")
    private Long completeTimeEnd;
    @Schema(description = "领取开始时间")
    private Long openTimeStart;
    @Schema(description = "领取结束时间")
    private Long openTimeEnd;

    /**
     * 站点代码
     */
    @Schema(description = "站点代码",hidden = true)
    private String siteCode;

    /**
     * 会员ID
     */
    @Schema(description = "会员ID")
    private String userAccount;

    @Schema(description = "宝箱打开状态 ",hidden = true)
    private Integer openStatus;


}
