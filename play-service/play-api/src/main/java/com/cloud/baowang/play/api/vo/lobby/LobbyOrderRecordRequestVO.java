package com.cloud.baowang.play.api.vo.lobby;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "请求参数注单记录")
public class LobbyOrderRecordRequestVO {

    @Schema(title = "投注类型:1:最新投注,2:大额投注")
    @NotNull(message = "投注类型不可为空")
    private Integer type;

}
