package com.cloud.baowang.play.api.vo.order.client;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class OrderRecordVenueClientReqVO extends PageVO {
    @Schema(description = "场馆code", hidden = true)
    private String siteCode;
    @Schema(description = "会员id", hidden = true)
    private String userId;
    @Schema(description = "会员账号", hidden = true)
    private String userAccount;
    @Schema(description = "语言", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String lang;
    @Schema(description = "注单状态 数组", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<Integer> orderClassifyList;
    @Schema(description = "投注开始时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long betStartTime;
    @Schema(description = "投注结束时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long betEndTime;
    @Schema(description = "沙巴用户token venueType = 1 必传", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String sabToken;
}
