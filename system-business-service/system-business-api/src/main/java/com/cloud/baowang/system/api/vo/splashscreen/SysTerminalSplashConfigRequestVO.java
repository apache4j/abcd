package com.cloud.baowang.system.api.vo.splashscreen;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "终端闪屏配置对象")
public class SysTerminalSplashConfigRequestVO extends PageVO {
    private String siteCode;
    @Schema(description = "显示终端  3-IOS_APP, 5-Andriod_APP")
    private String terminal;

    @Schema(description = "闪屏页的名称")
    private String name;

    @Schema(description = "闪屏页的状态，1表示启用，0表示禁用")
    private Integer status;

    @Schema(description = "创建用户")
    private String creator;

}

