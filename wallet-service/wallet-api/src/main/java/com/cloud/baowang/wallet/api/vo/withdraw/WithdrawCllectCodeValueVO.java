package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "提现收集信息返回对象")
public class WithdrawCllectCodeValueVO implements Serializable {

    @Schema(title = "类型")
    private String type;


    @Schema(title = "编码")
    private String code;

    @I18nField
    @Schema(title = "值")
    private String value;
    @Schema(title = "是否必选 1是 0否")
    private Integer isRequired;


    public WithdrawCllectCodeValueVO(String code, String value,Integer isRequired) {
        this.code = code;
        this.value = value;
        this.isRequired = isRequired;
    }
}
