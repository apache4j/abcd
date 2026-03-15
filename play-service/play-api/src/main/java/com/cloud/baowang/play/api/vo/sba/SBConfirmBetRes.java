package com.cloud.baowang.play.api.vo.sba;

import com.cloud.baowang.play.api.vo.base.SBResBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SBConfirmBetRes extends SBResBaseVO {

    @Schema(title = "若状态为成功，则回传余额以供更新小数点后两位数")
    private BigDecimal balance;

    @Schema(title = "账号")
    private String account;

}
