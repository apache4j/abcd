package com.cloud.baowang.system.api.vo.bank;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(description = "银行-银行编码返回vo")
@I18nClass
@Builder
public class BankInfoAdminRspVO {

    @Schema(description = "主键id,区分新增/编辑")
    private String id;

    @Schema(description = "银行编码")
    @I18nField
    private String bankName;

    @Schema(description = "银行编码")
    private String bankCode;

    @Schema(description = "银行-编码id")
    private String bankId;


    @Schema(description = "通道-银行编码")
    private String bankChannelMapping;



}
