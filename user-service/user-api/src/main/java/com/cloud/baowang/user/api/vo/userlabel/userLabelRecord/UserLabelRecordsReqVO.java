package com.cloud.baowang.user.api.vo.userlabel.userLabelRecord;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员标签记录查询单个入参对象")
public class UserLabelRecordsReqVO extends PageVO implements Serializable {
    @Schema(description = "变更开始时间")
    private Long startUpdatedTime;
    @Schema(description = "变更结束时间")
    private Long endUpdatedTime;
    @Schema(description = "会员账号")
    private String memberAccount;
    @Schema(description = "操作人")
    private String updater;
}
