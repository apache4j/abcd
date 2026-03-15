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
@Schema(description = "勋章获取记录查询条件")
public class MedalAcquireRecordReqVO extends PageVO {

    @Schema(description = "达成条件开始时间")
    private Long completeTimeStart;
    @Schema(description = "达成条件结束时间")
    private Long completeTimeEnd;
    @Schema(description = "解锁开始时间")
    private Long unlockTimeStart;
    @Schema(description = "解锁结束时间")
    private Long unlockTimeEnd;

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


    /**
     * 勋章名称
     */
    @Schema(description = "勋章名称")
    private String medalName;



}
