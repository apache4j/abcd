package com.cloud.baowang.agent.api.vo.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/11/06 21:36
 * @description:
 * */
@Data
@Schema(title = "商务账号验证响应VO")
public class MerchantAccountCheckResVO {

    @Schema(title = "商务账号")
    private String merchantAccount;

    @Schema(title = "是否已设置google密钥, true 已设置  false 未设置")
    private Boolean isSetGoogle;
}
