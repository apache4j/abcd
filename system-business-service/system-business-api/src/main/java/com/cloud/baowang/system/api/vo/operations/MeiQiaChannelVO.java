package com.cloud.baowang.system.api.vo.operations;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="美洽客服通道返回对象")
@I18nClass
public class MeiQiaChannelVO {
    @Schema(description = "三方平台code")
    private String platformCode;

    @Schema(description = "通道代码")
    private String channelCode;

    @Schema(description = "通道名称")
    private String channelName;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.CUSTOMER_TYPE)
    @Schema(description = "客服类型;1-在线客服")
    private Integer customerType;

    @Schema(description = "客服类型名称")
    private String customerTypeText;

    @Schema(description = "通道地址")
    private String channelAddr;
    //=======APP使用================//
    @Schema(description = "App Key")
    private String appKey;
    @Schema(description = "secret_key")
    private String secretKey;

    @Schema(description = "状态",hidden = true)
    private Integer status;
    //=======网页端使用===============//
    @Schema(description = "entId")
    private String entId;
    @Schema(description = "groupToken")
    private String groupToken;

    @Schema(description = "扩展参数")
    private String extJson;


}
