package com.cloud.baowang.play.api.vo.winto.rsp;
import com.cloud.baowang.common.core.serializer.AppBigDecimalJsonSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WintoRspParams {

    private String userName;
    private String nickName;
    private String currency;
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal balance;
    private String updateTime;
}
