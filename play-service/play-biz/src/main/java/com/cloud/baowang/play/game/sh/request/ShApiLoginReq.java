package com.cloud.baowang.play.game.sh.request;

import com.cloud.baowang.common.core.enums.DeviceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShApiLoginReq extends ShReqBase{

    @Schema(title = "用户账号")
    private String userName;

    @Schema(title = "用户IP")
    private String loginIp;

    @Schema(title = "设备型号")
    private String deviceType;

    @Schema(title = "币种")
    private String currency;

    @Schema(title = "桌台号")
    private String deskNumber;

    @Schema(title = "语言")
    private String lang;





}
