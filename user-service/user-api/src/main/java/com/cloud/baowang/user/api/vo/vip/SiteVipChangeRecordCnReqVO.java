package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * mufan
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "请求对象对象")
public class SiteVipChangeRecordCnReqVO  extends PageVO implements Serializable {

    @Schema(title = "变更开始时间")
    private Long createdTimeStart;

    @Schema(title = "结束结束时间")
    private Long createdTimeEnd;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "升降级标识(0:升级,1:降级")
    private Integer changeType;

    @Schema(description = "操作人")
    private String creator;

    @Hidden
    @Schema(description = "站点Code")
    private String siteCode;


}
