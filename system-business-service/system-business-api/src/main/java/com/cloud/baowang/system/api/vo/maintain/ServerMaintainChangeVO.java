package com.cloud.baowang.system.api.vo.maintain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "服务维护管理变更vo")
public class ServerMaintainChangeVO implements Serializable {
    @NotNull
    @Schema(description = "维护状态 1开启 0关闭")
    private Integer maintainStatus;
    @NotNull
    @Schema(description = "维护开始时间")
    private Long maintainBeginTime;
    @NotNull
    @Schema(description = "维护结束时间")
        private Long maintainEndTime;
    @NotEmpty
    @Schema(description = "维护终端 0会员端 1代理端")
    private List<Integer> maintainTerminal;
    @NotNull
    @Schema(description = "谷歌验证码")
    private Integer googleVerificationCode;
    @Schema(description = "操作人", hidden = true)
    private String operator;
}
