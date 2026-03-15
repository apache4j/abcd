package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/31 15:52
 * @Version: V1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "勋章获取记录查询条件")
public class MedalAcquireRecordCondReqVO {

    @Schema(description = "站点代码")
    private String siteCode;

    @Schema(description = "会员ID")
    private String userId;

    @Schema(description = "勋章代码")
    private MedalCodeEnum medalCodeEnum;

}
