package com.cloud.baowang.play.api.vo.sba;

import com.cloud.baowang.play.api.vo.base.SBResBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SBPlaceBetParlayRes extends SBResBaseVO {

    private List<SBPlaceBetRes> txns;

    @Schema(title = "账号")
    private String account;

}
