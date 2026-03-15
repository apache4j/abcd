package com.cloud.baowang.wallet.api.vo.userwallet;

import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 **/
@Data
@Schema(title = "平台币兑换记录列表请求参数")
public class ClientUserPlatformTransferRecordReqVO extends SitePageVO {
    @Schema(description = "开始时间")
    private Long beginTime;

    @Schema(description = "结束时间")
    private Long endTime;

    private String userId;

}
