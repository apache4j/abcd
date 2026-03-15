package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "会员标签")
public class UserWithdrawLabelVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "labelId")
    private String labelId;

    @Schema(description = "标签名称")
    private String labelName;

    @Schema(description = "标签描述")
    private String labelDescribe;

    @Schema(description = "颜色")
    private String color;

}
