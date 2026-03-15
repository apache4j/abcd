package com.cloud.baowang.wallet.api.vo.fundadjust;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "会员资金调整减额 导入返回对象")
public class UserManualDownAccountResponseVO {

    @Schema(title = "会员账号信息集合")
    private List<UserManualDownAccountResultVO> userManualDownAccountResultVOS;

    @Schema(description = "主货币")
    private String currencyCode;
}
