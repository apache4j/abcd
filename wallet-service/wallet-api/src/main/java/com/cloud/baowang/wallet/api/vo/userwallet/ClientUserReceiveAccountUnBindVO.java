package com.cloud.baowang.wallet.api.vo.userwallet;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "客户端-会员收款信息解绑请求对象")
@I18nClass
public class ClientUserReceiveAccountUnBindVO {


    /**
     * id
     */
    @Schema(description = "id")
    private String id;



    @Schema(description = "交易密码")
    private String withdrawPassWord;


}
