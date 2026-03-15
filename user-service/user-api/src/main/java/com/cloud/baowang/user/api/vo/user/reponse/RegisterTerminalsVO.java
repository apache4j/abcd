package com.cloud.baowang.user.api.vo.user.reponse;

import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "会员注册信息-下拉框")
public class RegisterTerminalsVO {

    @Schema(title = "注册终端")
    private List<CodeValueVO> registerTerminal;

    @Schema(title = "账号类型")
    private List<CodeValueVO> memberType;
}
